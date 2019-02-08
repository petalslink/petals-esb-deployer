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
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.ow2.petals.deployer.model.xml._1.Model;
import org.ow2.petals.deployer.model.xml._1.ObjectFactory;
import org.ow2.petals.deployer.utils.exceptions.ModelParsingException;
import org.ow2.petals.deployer.utils.exceptions.UncheckedException;

/**
 * @author Alexandre Lagane - Linagora
 *
 */
public class XmlModelBuilder {

    private final static Logger LOG = Logger.getLogger(XmlModelBuilder.class.getName());

    private final static Marshaller MARSHALLER;

    private final static Unmarshaller UNMARSHALLER;

    private final static ObjectFactory OF = new ObjectFactory();

    static {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(Model.class);
            MARSHALLER = jaxbContext.createMarshaller();
            MARSHALLER.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            UNMARSHALLER = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            throw new UncheckedException(e);
        }
    }

    /**
     * Read the model at the url. The model must be an XML model (with schema defined in {code model.xsd} in resources
     * directory)
     * 
     * @param url
     * @return the read model
     * @throws ModelParsingException
     */
    public static Model readModelFromUrl(final URL url) throws ModelParsingException {
        LOG.fine("Downloadind XML model");
        File modelFile;
        try {
            modelFile = Files.createTempFile("model", ".xml").toFile();
        } catch (IOException e) {
            throw new UncheckedException(e);
        }
        try {
            FileUtils.copyURLToFile(url, modelFile, ModelDeployer.CONNECTION_TIMEOUT, ModelDeployer.READ_TIMEOUT);

            LOG.fine("Parsing XML model");
            return UNMARSHALLER.unmarshal(new StreamSource(modelFile), Model.class).getValue();
        } catch (IOException | JAXBException e) {
            throw new ModelParsingException(e);
        }

    }

    /**
     * Write model to file. The same file is returned if model is successfully written.
     * 
     * @param model
     * @param file
     * @return file if successfully written else null
     * @throws ModelParsingException
     */
    public static File writeModelToFile(Model model, File file) throws ModelParsingException {
        try {
            MARSHALLER.marshal(OF.createModel(model), file);
            return file;
        } catch (JAXBException e) {
            throw new ModelParsingException(e);
        }

    }
}
