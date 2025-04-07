/**
 * Copyright (c) 2018-2025 Linagora
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.ow2.petals.admin.api.PetalsAdministration;
import org.ow2.petals.admin.api.PetalsAdministrationFactory;
import org.ow2.petals.admin.api.artifact.Component;
import org.ow2.petals.admin.api.artifact.Component.ComponentType;
import org.ow2.petals.admin.api.conf.ZipArchiveCustomizer;
import org.ow2.petals.admin.api.exception.ArtifactAdministrationException;
import org.ow2.petals.admin.api.exception.ArtifactUrlRewriterException;
import org.ow2.petals.admin.api.exception.ContainerAdministrationException;
import org.ow2.petals.deployer.runtimemodel.RuntimeComponent;
import org.ow2.petals.deployer.runtimemodel.RuntimeContainer;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel;
import org.ow2.petals.deployer.runtimemodel.RuntimeServiceUnit;
import org.ow2.petals.deployer.runtimemodel.RuntimeSharedLibrary;
import org.ow2.petals.deployer.utils.exceptions.ComponentDeploymentException;
import org.ow2.petals.deployer.utils.exceptions.ModelDeploymentExecutionException;
import org.ow2.petals.deployer.utils.exceptions.RuntimeModelDeployerException;
import org.ow2.petals.deployer.utils.exceptions.UncheckedException;
import org.ow2.petals.jbi.descriptor.JBIDescriptorException;
import org.ow2.petals.jbi.descriptor.extension.JBIDescriptorExtensionBuilder;
import org.ow2.petals.jbi.descriptor.extension.exception.JbiExtensionException;
import org.ow2.petals.jbi.descriptor.extension.exception.NoComponentNameDeployableServiceUnitException;
import org.ow2.petals.jbi.descriptor.original.JBIDescriptorBuilder;
import org.ow2.petals.jbi.descriptor.original.generated.Jbi;
import org.ow2.petals.jbi.descriptor.original.generated.ServiceAssembly;

import jakarta.validation.constraints.NotNull;

/**
 * @author Alexandre Lagane - Linagora
 */
public class RuntimeModelDeployer {

    private final static Logger LOG = Logger.getLogger(RuntimeModelDeployer.class.getName());

    private final PetalsAdministration petalsAdmin;

    private final JBIDescriptorBuilder jdb;

    private final JBIDescriptorExtensionBuilder jdeb;

    /**
     * @param petalsAdmin
     *            Petals Admin API instance to use to interact with Petals ESB nodes. If {@code null}, a new default
     *            instance will be created and used.
     */
    protected RuntimeModelDeployer(final PetalsAdministration petalsAdmin) {
        try {
            this.petalsAdmin = petalsAdmin != null ? petalsAdmin
                    : PetalsAdministrationFactory.getInstance().newPetalsAdministrationAPI();
            this.jdb = JBIDescriptorBuilder.getInstance();
            this.jdeb = JBIDescriptorExtensionBuilder.getInstance();
        } catch (JBIDescriptorException e) {
            throw new UncheckedException(e);
        }
    }

    public void deployRuntimeModel(@NotNull final RuntimeModel model) throws ModelDeploymentExecutionException {
        LOG.fine("Deploying model ...");

        final RuntimeContainer container = model.getContainers().iterator().next();
        this.deployRuntimeContainer(container, model);

        LOG.fine("Model deployed");
    }

    private void deployRuntimeContainer(@NotNull final RuntimeContainer container, @NotNull final RuntimeModel model)
            throws ModelDeploymentExecutionException {
        LOG.fine(String.format("Deploying container '%s' ...", container.getId()));

        this.connectToRuntimeContainer(container);
        this.deployRuntimeServiceUnits(container, model);

        LOG.fine(String.format("Model deployed on container '%s'.", container.getId()));
    }

    private void connectToRuntimeContainer(@NotNull final RuntimeContainer container)
            throws RuntimeModelDeployerException {

        final String hostname = container.getHostname();
        final int port = container.getPort();
        final String user = container.getUser();
        final String password = container.getPassword();

        try {
            LOG.fine(String.format("Connecting to container '%s' (%s@%s:%s) ...", container.getId(), user, hostname,
                    port));
            this.petalsAdmin.connect(hostname, port, user, password);
            LOG.fine(String.format("Connected to container '%s'.", container.getId()));
        } catch (final ContainerAdministrationException e) {
            throw new RuntimeModelDeployerException(e);
        }
    }

    private void deployRuntimeServiceUnits(@NotNull final RuntimeContainer container, @NotNull final RuntimeModel model)
            throws RuntimeModelDeployerException {

        final Collection<RuntimeServiceUnit> serviceUnits = container.getServiceUnits();
        LOG.fine(String.format("Deploying %d service unit(s) on container '%s' ...", serviceUnits.size(),
                container.getId()));

        final Set<String> deployedSharedLibraries = new HashSet<String>();
        final Set<String> deployedComponents = new HashSet<String>();
        final Set<String> deployedServiceUnits = new HashSet<String>();

        for (final RuntimeServiceUnit serviceUnit : serviceUnits) {
            this.deployRuntimeServiceUnit(serviceUnit, model, deployedComponents, deployedServiceUnits,
                    deployedSharedLibraries);
        }
        LOG.fine(String.format("%d service unit(s) deployed on container '%s' ...", serviceUnits.size(),
                container.getId()));
    }

    private void deployRuntimeServiceUnit(@NotNull final RuntimeServiceUnit serviceUnit,
            @NotNull final RuntimeModel model, @NotNull final Set<String> deployedComponents,
            @NotNull final Set<String> deployedServiceUnits, @NotNull final Set<String> deployedSharedLibraries)
            throws RuntimeModelDeployerException {

        if (!deployedServiceUnits.contains(serviceUnit.getId())) {
            LOG.fine(String.format("Deploying service unit '%s' located at '%s'", serviceUnit.getId(),
                    serviceUnit.getUrl().toString()));

            try {
                final Jbi jbi = this.jdb.buildJavaJBIDescriptorFromArchive(serviceUnit.getUrl(),
                        ModelDeployer.CONNECTION_TIMEOUT, ModelDeployer.READ_TIMEOUT);

                final ServiceAssembly jbiSa = jbi.getServiceAssembly();
                if (jbiSa != null) {
                    // Service unit embedded into a service assembly
                    this.deployRuntimeServiceUnitProvidedIntoAServiceAssembly(jbi, serviceUnit, model,
                            deployedComponents, deployedServiceUnits, deployedSharedLibraries);
                } else {
                    this.deployRuntimeServiceUnitAsAutoDeployable(jbi, serviceUnit, model, deployedComponents,
                            deployedServiceUnits, deployedSharedLibraries);
                }
            } catch (final JBIDescriptorException e) {
                throw new RuntimeModelDeployerException(e);
            }
        } else {
            LOG.fine(String.format("Service unit '%s' already deployed and started", serviceUnit.getId()));
        }
    }

    private void deployRuntimeServiceUnitProvidedIntoAServiceAssembly(@NotNull final Jbi jbi,
            @NotNull final RuntimeServiceUnit serviceUnit, @NotNull final RuntimeModel model,
            @NotNull final Set<String> deployedComponents, @NotNull final Set<String> deployedServiceUnits,
            @NotNull final Set<String> deployedSharedLibraries) throws RuntimeModelDeployerException {

        assert jbi.getServiceAssembly() != null;

        // Before to deploy the SA containing the requested SU, it is needed to deploy all required components
        for (final org.ow2.petals.jbi.descriptor.original.generated.ServiceUnit jbiSu : jbi.getServiceAssembly()
                .getServiceUnit()) {
            final String targetComponentName = jbiSu.getTarget().getComponentName();
            this.deployRuntimeComponentIfNeeded(targetComponentName, serviceUnit, model, deployedComponents,
                    deployedSharedLibraries);
        }

        // Now, we can deploy the SA ...
        try {
            this.petalsAdmin.newArtifactAdministration().deployAndStartArtifact(
                    this.petalsAdmin.getArtifactUrlRewriter().rewrite(serviceUnit.getUrl(), ZipArchiveCustomizer.NOOP),
                    false);
        } catch (final ArtifactAdministrationException | ArtifactUrlRewriterException e) {
            throw new RuntimeModelDeployerException(e);
        }

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

    private void deployRuntimeServiceUnitAsAutoDeployable(@NotNull final Jbi jbi,
            @NotNull final RuntimeServiceUnit serviceUnit, @NotNull final RuntimeModel model,
            @NotNull final Set<String> deployedComponents, @NotNull final Set<String> deployedServiceUnits,
            @NotNull final Set<String> deployedSharedLibraries) throws RuntimeModelDeployerException {

        assert jbi.getServiceAssembly() == null;

        final String targetComponentName;
        try {
            targetComponentName = this.jdeb.getDeployableServiceUnitTargetComponent(jbi);
        } catch (final NoComponentNameDeployableServiceUnitException e) {
            throw new RuntimeModelDeployerException(String.format(
                    "Service unit '%s' does not contain target component identification", serviceUnit.getId()), e);
        } catch (final JbiExtensionException e) {
            throw new RuntimeModelDeployerException(e);
        }

        this.deployRuntimeComponentIfNeeded(targetComponentName, serviceUnit, model, deployedComponents,
                deployedSharedLibraries);

        try {
            this.petalsAdmin.newArtifactAdministration().deployAndStartArtifact(
                    this.petalsAdmin.getArtifactUrlRewriter().rewrite(serviceUnit.getUrl(), ZipArchiveCustomizer.NOOP),
                    false);
        } catch (final ArtifactAdministrationException | ArtifactUrlRewriterException e) {
            throw new RuntimeModelDeployerException(e);
        }
        deployedServiceUnits.add(serviceUnit.getId());
        LOG.fine(String.format("Service unit '%s' deployed and started", serviceUnit.getId()));

    }

    private void deployRuntimeComponentIfNeeded(@NotNull final String targetComponentName,
            @NotNull final RuntimeServiceUnit serviceUnit, @NotNull final RuntimeModel model,
            @NotNull final Set<String> deployedComponents, @NotNull final Set<String> deployedSharedLibraries)
            throws RuntimeModelDeployerException {
        if (!deployedComponents.contains(targetComponentName)) {
            LOG.fine(String.format("The target component '%s' required by service unit '%s' must be deployed",
                    targetComponentName, serviceUnit.getId(), serviceUnit.getUrl().toString()));
            final RuntimeComponent component = model.getContainers().iterator().next()
                    .getComponent(targetComponentName);
            if (component != null) {
                this.deployRuntimeComponent(component, deployedSharedLibraries);
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

    private void deployRuntimeComponent(@NotNull final RuntimeComponent component,
            @NotNull final Set<String> deployedSharedLibraries) throws RuntimeModelDeployerException {
        final String compId = component.getId();
        LOG.fine(String.format("Deploying component '%s' ...", compId));

        for (final RuntimeSharedLibrary slToDeploy : component.getSharedLibraries()) {
            final String slIdAndVersion = slToDeploy.getId() + ":" + slToDeploy.getVersion();

            if (!deployedSharedLibraries.contains(slIdAndVersion)) {
                this.deployRuntimeSharedLibrary(slToDeploy);
                deployedSharedLibraries.add(slIdAndVersion);
            }
        }

        final Properties compParameters = new Properties();
        compParameters.putAll(component.getParameters());

        try {
            this.petalsAdmin.newArtifactAdministration().deployAndStartArtifact(
                    this.petalsAdmin.getArtifactUrlRewriter().rewrite(component.getUrl(),
                            new ComponentZipArchiveCustomizer(component)),
                    compParameters, false);
        } catch (final ArtifactAdministrationException | ArtifactUrlRewriterException e) {
            throw new RuntimeModelDeployerException(e);
        }

        LOG.fine(String.format("Component '%s' deployed and started.", compId));
    }

    private void deployRuntimeSharedLibrary(@NotNull final RuntimeSharedLibrary sl)
            throws RuntimeModelDeployerException {
        final String slId = sl.getId();
        final String slVersion = sl.getVersion();

        try {
            LOG.fine(String.format("Deploying shared library '%s:%s' ...", slId, slVersion));
            this.petalsAdmin.newArtifactAdministration()
                    .deployAndStartArtifact(
                            this.petalsAdmin.getArtifactUrlRewriter().rewrite(sl.getUrl(), ZipArchiveCustomizer.NOOP),
                            false);
            LOG.fine(String.format("Shared library '%s:%s' deployed.", slId, slVersion));
        } catch (final ArtifactAdministrationException | ArtifactUrlRewriterException e) {
            throw new RuntimeModelDeployerException(e);
        }
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
