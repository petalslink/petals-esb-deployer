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

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.ow2.petals.deployer.model.xml._1.Model;
import org.ow2.petals.deployer.model.xml._1.ObjectFactory;

/**
 * @author Alexandre Lagane - Linagora
 */
public class ParseModelTest {

    @Test
    public void parseModel() throws Exception {
        final ObjectFactory of = new ObjectFactory();

        final Model model = ModelUtils.generateTestModel();

        final StringWriter marshalledModelWriter = new StringWriter();
        final StringWriter unmarshalledModelWriter = new StringWriter();

        final JAXBContext jaxbContext = JAXBContext.newInstance(Model.class);

        final Marshaller marshaller = jaxbContext.createMarshaller();
        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        marshaller.marshal(of.createModel(model), marshalledModelWriter);

        final Model unmarshalledModel = unmarshaller
                .unmarshal(new StreamSource(new StringReader(marshalledModelWriter.toString())), Model.class)
                .getValue();

        marshaller.marshal(of.createModel(unmarshalledModel), unmarshalledModelWriter);

        assertEquals(marshalledModelWriter.toString(), unmarshalledModelWriter.toString());
    }
}
