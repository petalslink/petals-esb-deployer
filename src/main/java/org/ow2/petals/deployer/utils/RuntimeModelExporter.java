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

import java.util.logging.Logger;

import org.ow2.petals.admin.api.ArtifactAdministration;
import org.ow2.petals.admin.api.PetalsAdministration;
import org.ow2.petals.admin.api.PetalsAdministrationFactory;
import org.ow2.petals.admin.api.artifact.Artifact;
import org.ow2.petals.admin.api.exception.ArtifactAdministrationException;
import org.ow2.petals.admin.api.exception.ContainerAdministrationException;
import org.ow2.petals.admin.api.exception.DuplicatedServiceException;
import org.ow2.petals.admin.api.exception.MissingServiceException;
import org.ow2.petals.deployer.runtimemodel.RuntimeComponent;
import org.ow2.petals.deployer.runtimemodel.RuntimeContainer;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel;
import org.ow2.petals.deployer.runtimemodel.RuntimeServiceUnit;
import org.ow2.petals.deployer.runtimemodel.exceptions.RuntimeModelException;
import org.ow2.petals.jbi.descriptor.JBIDescriptorException;

/**
 * @author Alexandre Lagane - Linagora
 */
public class RuntimeModelExporter {

    private static final Logger LOG = Logger.getLogger(RuntimeModelExporter.class.getName());

    private final PetalsAdministration petalsAdmin;

    private final ArtifactAdministration artifactAdmin;

    public RuntimeModelExporter() throws DuplicatedServiceException, MissingServiceException, JBIDescriptorException {
        this(null);
    }

    public RuntimeModelExporter(final PetalsAdministration petalsAdmin) {
        this.petalsAdmin = petalsAdmin != null ? petalsAdmin
                : PetalsAdministrationFactory.getInstance().newPetalsAdministrationAPI();
        artifactAdmin = this.petalsAdmin.newArtifactAdministration();
    }

    /**
     * If topologyPassphrase is null, the exported RuntimeModel will not contain JMX usernames and JMX passwords.
     * 
     * @param hostname
     * @param port
     * @param user
     * @param password
     * @param topologyPassphrase
     * @return the RuntimeModel of the container corresponding to other parameters
     * @throws RuntimeModelException
     * @throws ArtifactAdministrationException
     * @throws ContainerAdministrationException
     */
    public RuntimeModel exportRuntimeModel(final String hostname, final int port, final String user,
            final String password, final String topologyPassphrase)
            throws RuntimeModelException, ArtifactAdministrationException, ContainerAdministrationException {
        petalsAdmin.connect(hostname, port, user, password);

        final RuntimeModel model = new RuntimeModel();

        final RuntimeContainer cont = new RuntimeContainer(petalsAdmin.newContainerAdministration()
                .getTopology(topologyPassphrase, false).getContainers().get(0).getContainerName(), port, user, password,
                hostname);
        model.addContainer(cont);

        for (final Artifact artifact : artifactAdmin.listArtifacts()) {
            switch (artifact.getType()) {
                case "BC":
                case "SE":
                    cont.addComponent(
                            new RuntimeComponent(((org.ow2.petals.admin.api.artifact.Component) artifact).getName()));
                    break;
                case "SU":
                    cont.addServiceUnit(new RuntimeServiceUnit(
                            ((org.ow2.petals.admin.api.artifact.ServiceUnit) artifact).getName()));
                    break;
                case "SA":
                    // already get service units in "SU" case
                    break;
                case "SL":
                default:
                    throw new UnsupportedOperationException(
                            "Export model with artifact of type " + artifact.getType() + " is not implemented yet");
            }
        }

        return model;
    }
}
