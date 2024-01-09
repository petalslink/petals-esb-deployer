/**
 * Copyright (c) 2019-2024 Linagora
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
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import jakarta.validation.constraints.NotNull;
import javax.xml.XMLConstants;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.ow2.petals.deployer.model.xml._1.Model;
import org.ow2.petals.deployer.model.xml._1.ObjectFactory;
import org.ow2.petals.deployer.utils.exceptions.ModelParsingException;
import org.ow2.petals.deployer.utils.exceptions.UncheckedException;
import org.xml.sax.SAXException;

/**
 * Utility class for parsing models.
 * 
 * @author Alexandre Lagane - Linagora
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
            final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            final URL modelSchemaUrl = Thread.currentThread().getContextClassLoader().getResource("model.xsd");
            UNMARSHALLER.setSchema(schemaFactory.newSchema(modelSchemaUrl));
        } catch (JAXBException | SAXException e) {
            throw new UncheckedException(e);
        }
    }

    private XmlModelBuilder() {
    }

    /**
     * Read the model at the url. The model must be an XML model (with schema defined in {code model.xsd} in resources
     * directory)
     * 
     * @param url
     * @return the read model
     * @throws ModelParsingException
     *             An error occurs parsing or unmarshalling the resource given by the URL.
     */
    @NotNull
    public static Model readModelFromUrl(@NotNull final URL url) throws ModelParsingException {
        try {
            final URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(ModelDeployer.CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(ModelDeployer.READ_TIMEOUT);
            try (final InputStream urlStream = urlConnection.getInputStream()) {
                LOG.fine("Retrieving and parsing XML model on the fly");
                return UNMARSHALLER.unmarshal(new StreamSource(urlStream), Model.class).getValue();
            }
        } catch (final IOException | JAXBException e) {
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
     *             An error occurs writing or marshalling the resource given by the URL.
     */
    @NotNull
    public static File writeModelToFile(@NotNull Model model, @NotNull File file) throws ModelParsingException {
        try {
            MARSHALLER.marshal(OF.createModel(model), file);
            return file;
        } catch (final JAXBException e) {
            throw new ModelParsingException(e);
        }

    }
}
