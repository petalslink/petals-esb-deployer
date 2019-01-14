/**
 * Copyright (c) 2019 Linagora
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
import java.net.URL;
import java.nio.file.Files;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.ow2.petals.admin.api.exception.ArtifactAdministrationException;
import org.ow2.petals.admin.api.exception.ContainerAdministrationException;
import org.ow2.petals.admin.api.exception.DuplicatedServiceException;
import org.ow2.petals.admin.api.exception.MissingServiceException;
import org.ow2.petals.deployer.model.xml._1.Model;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel.RuntimeModelException;
import org.ow2.petals.jbi.descriptor.JBIDescriptorException;

public class ModelDeployer {

    /**
     * Connection timeout in milliseconds to get model or artifacts from URL.
     */
    public static final int CONNECTION_TIMEOUT = 5000;

    /**
     * Read timeout in milliseconds to read model or artifacts from URL.
     */
    public static final int READ_TIMEOUT = 5000;

    private static final Logger LOG = Logger.getLogger(ModelDeployer.class.getName());

    public static void deployModel(URL url) throws ModelDeployerException {
        File modelFile;
        try {
            LOG.fine("Downloadind XML model");

            modelFile = Files.createTempFile("model", ".xml").toFile();
            FileUtils.copyURLToFile(url, modelFile, CONNECTION_TIMEOUT, READ_TIMEOUT);

            LOG.fine("Parsing XML model");

            JAXBContext jaxbContext = JAXBContext.newInstance(Model.class);

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            Model model = unmarshaller.unmarshal(new StreamSource(modelFile), Model.class).getValue();

            ModelConverter converter = new ModelConverter();

            RuntimeModel runtimeModel;
            runtimeModel = converter.convertModelToRuntimeModel(model);

            RuntimeModelDeployer deployer;
            deployer = new RuntimeModelDeployer();

            deployer.deployRuntimeModel(runtimeModel);

        } catch (JAXBException | IOException | RuntimeModelException | DuplicatedServiceException
                | MissingServiceException | JBIDescriptorException | ContainerAdministrationException
                | ArtifactAdministrationException | RuntimeModelDeployerException e) {
            throw new ModelDeployerException(e);
        }
    }
}