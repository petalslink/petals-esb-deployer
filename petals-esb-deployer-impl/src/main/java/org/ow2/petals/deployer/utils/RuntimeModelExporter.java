/**
 * Copyright (c) 2018-2026 Linagora
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

import java.util.Map.Entry;
import java.util.logging.Logger;

import org.ow2.petals.admin.api.ArtifactAdministration;
import org.ow2.petals.admin.api.PetalsAdministration;
import org.ow2.petals.admin.api.PetalsAdministrationFactory;
import org.ow2.petals.admin.api.artifact.Artifact;
import org.ow2.petals.admin.api.artifact.Component;
import org.ow2.petals.admin.api.artifact.ServiceUnit;
import org.ow2.petals.admin.api.artifact.SharedLibrary;
import org.ow2.petals.admin.api.exception.ArtifactAdministrationException;
import org.ow2.petals.admin.api.exception.ContainerAdministrationException;
import org.ow2.petals.admin.api.exception.DuplicatedServiceException;
import org.ow2.petals.admin.api.exception.MissingServiceException;
import org.ow2.petals.deployer.runtimemodel.RuntimeComponent;
import org.ow2.petals.deployer.runtimemodel.RuntimeContainer;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel;
import org.ow2.petals.deployer.runtimemodel.RuntimeServiceUnit;
import org.ow2.petals.deployer.runtimemodel.RuntimeSharedLibrary;
import org.ow2.petals.deployer.runtimemodel.exceptions.RuntimeModelException;
import org.ow2.petals.jbi.descriptor.JBIDescriptorException;

import jakarta.validation.constraints.NotNull;

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
    @NotNull
    public RuntimeModel exportRuntimeModel(@NotNull final String hostname, @NotNull final int port,
            @NotNull final String user, @NotNull final String password, final String topologyPassphrase)
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
                    Component comp = (Component) artifact;
                    String compName = comp.getName();
                    RuntimeComponent runtimeComp = new RuntimeComponent(compName);

                    for (Entry<Object, Object> paramEntry : comp.getParameters().entrySet()) {
                        runtimeComp.setParameterValue((String) paramEntry.getKey(), (String) paramEntry.getValue());
                    }

                    for (final SharedLibrary sl : comp.getSharedLibraries()) {
                        String slId = sl.getName();
                        String slVersion = sl.getVersion();
                        RuntimeSharedLibrary runtimeSl = cont.getSharedLibrary(slId, slVersion);
                        if (runtimeSl == null) {
                            runtimeSl = new RuntimeSharedLibrary(slId, slVersion);
                            cont.addSharedLibrary(runtimeSl);
                        }
                        runtimeComp.addSharedLibrary(runtimeSl);
                    }
                    cont.addComponent(runtimeComp);
                    break;
                case "SU":
                    cont.addServiceUnit(new RuntimeServiceUnit(((ServiceUnit) artifact).getName()));
                    break;
                case "SA":
                    // already get service units in "SU" case
                    break;
                case "SL":
                    // shared libraries are added in the "BC" and "SE" case
                    break;
                default:
                    throw new UnsupportedOperationException(
                            "Export model with artifact of type " + artifact.getType() + " is not implemented yet");
            }
        }

        return model;
    }
}
