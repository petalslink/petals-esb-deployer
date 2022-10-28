/**
 * Copyright (c) 2018-2022 Linagora
 *
 * This program/library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This program/library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program/library; If not, see http://www.gnu.org/licenses/
 * for the GNU Lesser General Public License version 2.1.
 */

package org.ow2.petals.deployer.utils;

import java.io.IOException;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;

import javax.validation.constraints.NotNull;

import org.ow2.petals.admin.api.PetalsAdministration;
import org.ow2.petals.admin.api.PetalsAdministrationFactory;
import org.ow2.petals.admin.api.artifact.Component;
import org.ow2.petals.admin.api.artifact.Component.ComponentType;
import org.ow2.petals.admin.api.artifact.SharedLibrary;
import org.ow2.petals.admin.api.artifact.lifecycle.ArtifactLifecycleFactory;
import org.ow2.petals.admin.api.artifact.lifecycle.ComponentLifecycle;
import org.ow2.petals.admin.api.artifact.lifecycle.SharedLibraryLifecycle;
import org.ow2.petals.admin.api.exception.ArtifactAdministrationException;
import org.ow2.petals.admin.api.exception.ArtifactDeployedException;
import org.ow2.petals.admin.api.exception.ArtifactNotDeployedException;
import org.ow2.petals.admin.api.exception.ArtifactNotFoundException;
import org.ow2.petals.admin.api.exception.ArtifactStartedException;
import org.ow2.petals.admin.api.exception.ConnectionFailedException;
import org.ow2.petals.admin.api.exception.ContainerAdministrationException;
import org.ow2.petals.deployer.runtimemodel.RuntimeComponent;
import org.ow2.petals.deployer.runtimemodel.RuntimeContainer;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel;
import org.ow2.petals.deployer.runtimemodel.RuntimeServiceUnit;
import org.ow2.petals.deployer.runtimemodel.RuntimeSharedLibrary;
import org.ow2.petals.deployer.utils.exceptions.ComponentDeploymentException;
import org.ow2.petals.deployer.utils.exceptions.RuntimeModelDeployerException;
import org.ow2.petals.deployer.utils.exceptions.UncheckedException;
import org.ow2.petals.jbi.descriptor.JBIDescriptorException;
import org.ow2.petals.jbi.descriptor.extension.JBIDescriptorExtensionBuilder;
import org.ow2.petals.jbi.descriptor.extension.exception.NoComponentNameDeployableServiceUnitException;
import org.ow2.petals.jbi.descriptor.original.JBIDescriptorBuilder;
import org.ow2.petals.jbi.descriptor.original.generated.Jbi;
import org.ow2.petals.jbi.descriptor.original.generated.ServiceAssembly;

/**
 * @author Alexandre Lagane - Linagora
 */
public class RuntimeModelDeployer {

    private final static Logger LOG = Logger.getLogger(RuntimeModelDeployer.class.getName());

    private final PetalsAdministration petalsAdmin;

    private final ArtifactLifecycleFactory artifactLifecycleFactory;

    private final JBIDescriptorBuilder jdb;

    private final JBIDescriptorExtensionBuilder jdeb;

    public RuntimeModelDeployer() {
        this(null, null);
    }

    /**
     * Used only for testing purposes, to mock petalsAdmin and artifactLifecycleFactory.
     * 
     * @param petalsAdmin
     * @param artifactLifecycleFactory
     */
    protected RuntimeModelDeployer(final PetalsAdministration petalsAdmin,
            final ArtifactLifecycleFactory artifactLifecycleFactory) {
        try {
            this.petalsAdmin = petalsAdmin != null ? petalsAdmin
                    : PetalsAdministrationFactory.getInstance().newPetalsAdministrationAPI();
            this.artifactLifecycleFactory = artifactLifecycleFactory != null ? artifactLifecycleFactory
                    : this.petalsAdmin.newArtifactLifecycleFactory();
            this.jdb = JBIDescriptorBuilder.getInstance();
            this.jdeb = JBIDescriptorExtensionBuilder.getInstance();
        } catch (JBIDescriptorException e) {
            throw new UncheckedException(e);
        }
    }

    public void deployRuntimeModel(@NotNull final RuntimeModel model)
            throws ConnectionFailedException, ContainerAdministrationException, ArtifactStartedException,
            ArtifactNotDeployedException, ArtifactNotFoundException, IOException, JBIDescriptorException,
            ArtifactAdministrationException, RuntimeModelDeployerException {
        final RuntimeContainer container = model.getContainers().iterator().next();
        final Collection<RuntimeServiceUnit> serviceUnits = container.getServiceUnits();

        LOG.fine("Deploying model (" + serviceUnits.size() + " service units)");

        final String hostname = container.getHostname();
        final int port = container.getPort();
        final String user = container.getUser();
        final String password = container.getPassword();

        petalsAdmin.connect(hostname, port, user, password);

        final Set<String> deployedSharedLibraries = new HashSet<String>();
        final Set<String> deployedComponents = new HashSet<String>();
        final Set<String> deployedServiceUnits = new HashSet<String>();

        for (final RuntimeServiceUnit serviceUnit : serviceUnits) {
            deployRuntimeServiceUnit(serviceUnit, model, deployedComponents, deployedServiceUnits,
                    deployedSharedLibraries);
        }

        LOG.fine("Model deployed");
    }

    private void deployRuntimeServiceUnit(final RuntimeServiceUnit serviceUnit, final RuntimeModel model,
            final Set<String> deployedComponents, final Set<String> deployedServiceUnits,
            final Set<String> deployedSharedLibraries)
            throws IOException, JBIDescriptorException, ArtifactAdministrationException, RuntimeModelDeployerException {

        if (!deployedServiceUnits.contains(serviceUnit.getId())) {
            LOG.fine(String.format("Deploying service unit '%s' located at '%s'", serviceUnit.getId(),
                    serviceUnit.getUrl().toString()));

            final URLConnection suUrlConnection = serviceUnit.getUrl().openConnection();
            suUrlConnection.setConnectTimeout(ModelDeployer.CONNECTION_TIMEOUT);
            suUrlConnection.setReadTimeout(ModelDeployer.READ_TIMEOUT);
            try (final ZipInputStream suZipInputStream = new ZipInputStream(suUrlConnection.getInputStream())) {
                final Jbi jbi = jdb.buildJavaJBIDescriptorFromArchive(suZipInputStream);

                final ServiceAssembly jbiSa = jbi.getServiceAssembly();
                if (jbiSa != null) {
                    // Service unit embedded into a service assembly
                    this.deployRuntimeServiceUnitProvidedIntoAServiceAssembly(jbi, serviceUnit, model,
                            deployedComponents, deployedServiceUnits, deployedSharedLibraries);
                } else {
                    this.deployRuntimeServiceUnitAsAutoDeployable(jbi, serviceUnit, model, deployedComponents,
                            deployedServiceUnits, deployedSharedLibraries);
                }
            }
        } else {
            LOG.fine(String.format("Service unit '%s' already deployed and started", serviceUnit.getId()));
        }
    }

    private void deployRuntimeServiceUnitProvidedIntoAServiceAssembly(final Jbi jbi,
            final RuntimeServiceUnit serviceUnit, final RuntimeModel model, final Set<String> deployedComponents,
            final Set<String> deployedServiceUnits, final Set<String> deployedSharedLibraries)
            throws IOException, JBIDescriptorException, ArtifactAdministrationException, RuntimeModelDeployerException {

        assert jbi.getServiceAssembly() != null;

        // Before to deploy the SA containing the requested SU, it is needed to deploy all required components
        for (final org.ow2.petals.jbi.descriptor.original.generated.ServiceUnit jbiSu : jbi.getServiceAssembly()
                .getServiceUnit()) {
            final String targetComponentName = jbiSu.getTarget().getComponentName();
            this.deployRuntimeComponentIfNeeded(targetComponentName, serviceUnit, model, deployedComponents,
                    deployedSharedLibraries);
        }

        // Now, we can deploy the SA ...
        this.petalsAdmin.newArtifactAdministration().deployAndStartArtifact(serviceUnit.getUrl(), true);

        // ... and register all SUs contained in the SA as 'deployed'
        for (final org.ow2.petals.jbi.descriptor.original.generated.ServiceUnit jbiSu : jbi.getServiceAssembly()
                .getServiceUnit()) {
            final String suName = jbiSu.getIdentification().getName();
            if (!deployedServiceUnits.contains(suName)) {
                deployedServiceUnits.add(suName);
                LOG.fine(String.format("Service unit '%s' deployed and started", suName));
            } else {
                throw new RuntimeModelDeployerException("Service unit " + suName + " already deployed");
            }
        }
    }

    private void deployRuntimeServiceUnitAsAutoDeployable(final Jbi jbi, final RuntimeServiceUnit serviceUnit,
            final RuntimeModel model, final Set<String> deployedComponents, final Set<String> deployedServiceUnits,
            final Set<String> deployedSharedLibraries)
            throws IOException, JBIDescriptorException, ArtifactAdministrationException, RuntimeModelDeployerException {

        assert jbi.getServiceAssembly() == null;

        final String targetComponentName;
        try {
            targetComponentName = this.jdeb.getDeployableServiceUnitTargetComponent(jbi);
        } catch (final NoComponentNameDeployableServiceUnitException e) {
            throw new RuntimeModelDeployerException(String.format(
                    "Service unit '%s' does not contain target component identification", serviceUnit.getId()), e);
        }

        this.deployRuntimeComponentIfNeeded(targetComponentName, serviceUnit, model, deployedComponents,
                deployedSharedLibraries);

        this.petalsAdmin.newArtifactAdministration().deployAndStartArtifact(serviceUnit.getUrl(), true);
        deployedServiceUnits.add(serviceUnit.getId());
        LOG.fine(String.format("Service unit '%s' deployed and started", serviceUnit.getId()));

    }

    private void deployRuntimeComponentIfNeeded(final String targetComponentName, final RuntimeServiceUnit serviceUnit,
            final RuntimeModel model, final Set<String> deployedComponents, final Set<String> deployedSharedLibraries)
            throws IOException, JBIDescriptorException, ArtifactAdministrationException, RuntimeModelDeployerException {
        if (!deployedComponents.contains(targetComponentName)) {
            LOG.fine(String.format("The target component '%s' required by service unit '%s' must be deployed",
                    targetComponentName, serviceUnit.getId(), serviceUnit.getUrl().toString()));
            final RuntimeComponent component = model.getContainers().iterator().next()
                    .getComponent(targetComponentName);
            if (component != null) {
                deployRuntimeComponent(component, deployedSharedLibraries);
                deployedComponents.add(targetComponentName);
            } else {
                throw new ComponentDeploymentException(
                        String.format("Component '%s' (needed by service unit '%s') not found in the model",
                                targetComponentName, serviceUnit.getId()));
            }
        } else {
            LOG.fine(String.format("The target component '%s' required by service unit '%s' is already deployed",
                    targetComponentName, serviceUnit.getId(), serviceUnit.getUrl().toString()));
        }
    }

    private void deployRuntimeComponent(final RuntimeComponent component, Set<String> deployedSharedLibraries)
            throws IOException, JBIDescriptorException, ArtifactDeployedException, ArtifactAdministrationException {
        for (RuntimeSharedLibrary slToDeploy : component.getSharedLibraries()) {
            String slIdAndVersion = slToDeploy.getId() + ":" + slToDeploy.getVersion();

            if (!deployedSharedLibraries.contains(slIdAndVersion)) {
                deployRuntimeSharedLibrary(slToDeploy);
                deployedSharedLibraries.add(slIdAndVersion);
            }
        }

        final String compId = component.getId();
        final URLConnection compUrlConnection = component.getUrl().openConnection();
        compUrlConnection.setConnectTimeout(ModelDeployer.CONNECTION_TIMEOUT);
        compUrlConnection.setReadTimeout(ModelDeployer.READ_TIMEOUT);
        try (final ZipInputStream compZipInputStream = new ZipInputStream(compUrlConnection.getInputStream())) {
            final Jbi jbi = jdb.buildJavaJBIDescriptorFromArchive(compZipInputStream);

            LOG.fine("Deploying component " + component.getId());
            Properties parameters = new Properties();
            parameters.putAll(component.getParameters());
            final ComponentLifecycle compLifecycle = artifactLifecycleFactory
                    .createComponentLifecycle(new org.ow2.petals.admin.api.artifact.Component(compId,
                            convertComponentTypeFromJbiToPetalsAdmin(jbi.getComponent().getType()), parameters));

            compLifecycle.deploy(component.getUrl());
            compLifecycle.start();
            LOG.fine("Component " + compId + " deployed and started");
        }
    }

    private void deployRuntimeSharedLibrary(final RuntimeSharedLibrary sl)
            throws IOException, JBIDescriptorException, ArtifactDeployedException, ArtifactAdministrationException {
        final String slId = sl.getId();
        final String slVersion = sl.getVersion();

        LOG.fine("Deploying shared library " + slId + ":" + slVersion);
        final SharedLibraryLifecycle slLifeCycle = artifactLifecycleFactory
                .createSharedLibraryLifecycle(new SharedLibrary(slId, slVersion));

        slLifeCycle.deploy(sl.getUrl());
        LOG.fine("Shared library " + slId + ":" + slVersion + " deployed and started");
    }

    public static ComponentType convertComponentTypeFromJbiToPetalsAdmin(
            final org.ow2.petals.jbi.descriptor.original.generated.ComponentType jbiType) {
        switch (jbiType) {
            case BINDING_COMPONENT:
                return Component.ComponentType.BC;
            case SERVICE_ENGINE:
                return Component.ComponentType.SE;
            default:
                return null;
        }
    }
}
