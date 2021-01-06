/**
 * Copyright (c) 2018-2021 Linagora
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.validation.constraints.NotNull;

import org.apache.commons.io.FileUtils;
import org.ow2.petals.admin.api.PetalsAdministration;
import org.ow2.petals.admin.api.PetalsAdministrationFactory;
import org.ow2.petals.admin.api.artifact.Component;
import org.ow2.petals.admin.api.artifact.Component.ComponentType;
import org.ow2.petals.admin.api.artifact.SharedLibrary;
import org.ow2.petals.admin.api.artifact.lifecycle.ArtifactLifecycleFactory;
import org.ow2.petals.admin.api.artifact.lifecycle.ComponentLifecycle;
import org.ow2.petals.admin.api.artifact.lifecycle.ServiceAssemblyLifecycle;
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
            throws IOException, JBIDescriptorException, ArtifactStartedException, ArtifactNotDeployedException,
            ArtifactNotFoundException, ArtifactAdministrationException, RuntimeModelDeployerException {
        final String suId = serviceUnit.getId();
        final File suFile = Files.createTempFile(suId, ".zip").toFile();
        FileUtils.copyURLToFile(serviceUnit.getUrl(), suFile, ModelDeployerImpl.CONNECTION_TIMEOUT,
                ModelDeployerImpl.READ_TIMEOUT);
        final Jbi jbi = jdb.buildJavaJBIDescriptorFromArchive(suFile);
        final ServiceAssembly jbiSa = jbi.getServiceAssembly();

        if (!deployedServiceUnits.contains(suId)) {
            LOG.fine("Deploying service assembly");
            for (final org.ow2.petals.jbi.descriptor.original.generated.ServiceUnit jbiSu : jbiSa.getServiceUnit()) {
                final String compName = jbiSu.getTarget().getComponentName();
                if (!deployedComponents.contains(compName)) {
                    final RuntimeComponent component = model.getContainers().iterator().next().getComponent(compName);
                    if (component != null) {
                        deployRuntimeComponent(component, deployedSharedLibraries);
                        deployedComponents.add(compName);
                    } else {
                        throw new ComponentDeploymentException("Component " + compName + " (needed by service unit "
                                + jbiSu.getIdentification().getName() + ") not found in the model");
                    }
                }
            }
            final ServiceAssemblyLifecycle saLifecycle = artifactLifecycleFactory.createServiceAssemblyLifecycle(
                    new org.ow2.petals.admin.api.artifact.ServiceAssembly(jbiSa.getIdentification().getName()));
            saLifecycle.deploy(suFile.toURI().toURL());
            saLifecycle.start();
            for (final org.ow2.petals.jbi.descriptor.original.generated.ServiceUnit jbiSu : jbiSa.getServiceUnit()) {
                final String suName = jbiSu.getIdentification().getName();
                if (!deployedServiceUnits.contains(suName)) {
                    deployedServiceUnits.add(suName);
                    LOG.fine("Service unit " + suName + " deployed and started");
                } else {
                    throw new RuntimeModelDeployerException("Service unit " + suName + " already deployed");
                }
            }
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
        final File compFile = Files.createTempFile(compId, "zip").toFile();
        FileUtils.copyURLToFile(component.getUrl(), compFile, ModelDeployerImpl.CONNECTION_TIMEOUT,
                ModelDeployerImpl.READ_TIMEOUT);
        final Jbi jbi = jdb.buildJavaJBIDescriptorFromArchive(compFile);

        LOG.fine("Deploying component " + component.getId());
        Properties parameters = new Properties();
        parameters.putAll(component.getParameters());
        final ComponentLifecycle compLifecycle = artifactLifecycleFactory
                .createComponentLifecycle(new org.ow2.petals.admin.api.artifact.Component(compId,
                        convertComponentTypeFromJbiToPetalsAdmin(jbi.getComponent().getType()), parameters));

        compLifecycle.deploy(compFile.toURI().toURL());
        compLifecycle.start();
        LOG.fine("Component " + compId + " deployed and started");
    }

    private void deployRuntimeSharedLibrary(final RuntimeSharedLibrary sl)
            throws IOException, JBIDescriptorException, ArtifactDeployedException, ArtifactAdministrationException {
        final String slId = sl.getId();
        final String slVersion = sl.getVersion();
        final File slFile = Files.createTempFile(slId + '-' + slVersion, "zip").toFile();
        FileUtils.copyURLToFile(sl.getUrl(), slFile, ModelDeployerImpl.CONNECTION_TIMEOUT,
                ModelDeployerImpl.READ_TIMEOUT);

        LOG.fine("Deploying shared library " + slId + ":" + slVersion);
        final SharedLibraryLifecycle slLifeCycle = artifactLifecycleFactory
                .createSharedLibraryLifecycle(new SharedLibrary(slId, slVersion));

        slLifeCycle.deploy(slFile.toURI().toURL());
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
