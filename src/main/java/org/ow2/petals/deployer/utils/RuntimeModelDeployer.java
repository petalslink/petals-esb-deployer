/**
 * Copyright (c) 2018-2019 Linagora
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
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.ow2.petals.admin.api.PetalsAdministration;
import org.ow2.petals.admin.api.PetalsAdministrationFactory;
import org.ow2.petals.admin.api.artifact.Component.ComponentType;
import org.ow2.petals.admin.api.artifact.lifecycle.ArtifactLifecycleFactory;
import org.ow2.petals.admin.api.artifact.lifecycle.ComponentLifecycle;
import org.ow2.petals.admin.api.artifact.lifecycle.ServiceAssemblyLifecycle;
import org.ow2.petals.admin.api.exception.ArtifactAdministrationException;
import org.ow2.petals.admin.api.exception.ArtifactDeployedException;
import org.ow2.petals.admin.api.exception.ArtifactNotDeployedException;
import org.ow2.petals.admin.api.exception.ArtifactNotFoundException;
import org.ow2.petals.admin.api.exception.ArtifactStartedException;
import org.ow2.petals.admin.api.exception.ConnectionFailedException;
import org.ow2.petals.admin.api.exception.ContainerAdministrationException;
import org.ow2.petals.admin.api.exception.DuplicatedServiceException;
import org.ow2.petals.admin.api.exception.MissingServiceException;
import org.ow2.petals.deployer.runtimemodel.RuntimeComponent;
import org.ow2.petals.deployer.runtimemodel.RuntimeContainer;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel;
import org.ow2.petals.deployer.runtimemodel.RuntimeServiceUnit;
import org.ow2.petals.jbi.descriptor.JBIDescriptorException;
import org.ow2.petals.jbi.descriptor.original.JBIDescriptorBuilder;
import org.ow2.petals.jbi.descriptor.original.generated.Jbi;
import org.ow2.petals.jbi.descriptor.original.generated.ServiceAssembly;

public class RuntimeModelDeployer {

    private static final Logger LOG = Logger.getLogger(RuntimeModelDeployer.class.getName());

    private PetalsAdministration petalsAdmin;

    private ArtifactLifecycleFactory artifactLifecycleFactory;

    private JBIDescriptorBuilder jdb;

    private HashSet<String> deployedComponents = new HashSet<String>();

    private HashSet<String> deployedServiceUnits = new HashSet<String>();

    public RuntimeModelDeployer() throws DuplicatedServiceException, MissingServiceException, JBIDescriptorException {
        this(PetalsAdministrationFactory.getInstance().newPetalsAdministrationAPI(), null,
                JBIDescriptorBuilder.getInstance());

    }

    public RuntimeModelDeployer(PetalsAdministration petalsAdmin, ArtifactLifecycleFactory artifactLifecycleFactory,
            JBIDescriptorBuilder jdb) {
        this.petalsAdmin = petalsAdmin;
        this.artifactLifecycleFactory = artifactLifecycleFactory != null ? artifactLifecycleFactory
                : petalsAdmin.newArtifactLifecycleFactory();
        this.jdb = jdb;
    }

    public void deployRuntimeModel(RuntimeModel model)
            throws ConnectionFailedException, ContainerAdministrationException, ArtifactStartedException,
            ArtifactNotDeployedException, ArtifactNotFoundException, IOException, JBIDescriptorException,
            ArtifactAdministrationException, RuntimeModelDeployerException {
        RuntimeContainer container = model.getContainers().iterator().next();
        Collection<RuntimeServiceUnit> serviceUnits = container.getServiceUnits();

        LOG.fine("Deploying model (" + serviceUnits.size() + " service units)");

        String hostname = container.getHostname();
        int port = container.getPort();
        String user = container.getUser();
        String password = container.getPassword();

        petalsAdmin.connect(hostname, port, user, password);

        for (RuntimeServiceUnit serviceUnit : serviceUnits) {
            deployRuntimeServiceUnit(serviceUnit, model);
        }

        LOG.fine("Model deployed");
    }

    private void deployRuntimeServiceUnit(RuntimeServiceUnit serviceUnit, RuntimeModel model)
            throws IOException, JBIDescriptorException, ArtifactStartedException, ArtifactNotDeployedException,
            ArtifactNotFoundException, ArtifactAdministrationException, RuntimeModelDeployerException {
        String suId = serviceUnit.getId();
        File suFile = Files.createTempFile(suId, ".zip").toFile();
        FileUtils.copyURLToFile(serviceUnit.getUrl(), suFile, ModelDeployer.CONNECTION_TIMEOUT,
                ModelDeployer.READ_TIMEOUT);
        Jbi jbi = jdb.buildJavaJBIDescriptorFromArchive(suFile);
        ServiceAssembly jbiSa = jbi.getServiceAssembly();

        if (!deployedServiceUnits.contains(suId)) {
            LOG.fine("Deploying service assembly");
            for (org.ow2.petals.jbi.descriptor.original.generated.ServiceUnit jbiSu : jbiSa.getServiceUnit()) {
                String compName = jbiSu.getTarget().getComponentName();
                if (!deployedComponents.contains(compName)) {
                    RuntimeComponent component = model.getContainers().iterator().next().getComponent(compName);
                    if (component == null) {
                        throw new RuntimeModelDeployerException("Component " + compName + " (needed by service unit "
                                + jbiSu.getIdentification().getName() + ") not found");
                    }
                    deployRuntimeComponent(component);
                    deployedComponents.add(compName);
                }
            }
            ServiceAssemblyLifecycle saLifecycle = artifactLifecycleFactory.createServiceAssemblyLifecycle(
                    new org.ow2.petals.admin.api.artifact.ServiceAssembly(jbiSa.getIdentification().getName()));
            saLifecycle.deploy(suFile.toURI().toURL());
            saLifecycle.start();
            for (org.ow2.petals.jbi.descriptor.original.generated.ServiceUnit jbiSu : jbiSa.getServiceUnit()) {
                String suName = jbiSu.getIdentification().getName();
                if (!deployedServiceUnits.contains(suName)) {
                    deployedServiceUnits.add(suName);
                    LOG.fine("Service unit " + suName + " deployed and started");
                } else {
                    throw new RuntimeModelDeployerException("Service unit " + suName + " already deployed");
                }
            }
        }
    }

    private void deployRuntimeComponent(RuntimeComponent component)
            throws IOException, JBIDescriptorException, ArtifactDeployedException, ArtifactAdministrationException {
        String compId = component.getId();
        File compFile = Files.createTempFile(compId, "zip").toFile();
        FileUtils.copyURLToFile(component.getUrl(), compFile, ModelDeployer.CONNECTION_TIMEOUT,
                ModelDeployer.READ_TIMEOUT);
        Jbi jbi = jdb.buildJavaJBIDescriptorFromArchive(compFile);

        LOG.fine("Deploying component " + component.getId());
        ComponentLifecycle compLifecycle = artifactLifecycleFactory
                .createComponentLifecycle(new org.ow2.petals.admin.api.artifact.Component(compId,
                        convertComponentType(jbi.getComponent().getType())));

        compLifecycle.deploy(compFile.toURI().toURL());
        compLifecycle.start();
        LOG.fine("Component " + compId + " deployed and started");
    }

    public ComponentType convertComponentType(
            final org.ow2.petals.jbi.descriptor.original.generated.ComponentType jbiType) {
        switch (jbiType) {
            case BINDING_COMPONENT:
                return ComponentType.BC;
            case SERVICE_ENGINE:
                return ComponentType.SE;
            default:
                return null;
        }
    }
}
