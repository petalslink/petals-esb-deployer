/**
 * Copyright (c) 2019-2025 Linagora
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.net.URL;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.ow2.petals.deployer.model.component_repository.xml._1.Component;
import org.ow2.petals.deployer.model.xml._1.Model;
import org.ow2.petals.deployer.utils.exceptions.ModelParsingException;
import org.xml.sax.InputSource;

/**
 * @author Alexandre Lagane - Linagora
 */
public class XmlModelBuilderTest {

    @Test
    public void testReadAndWrite(final @TempDir File tempFolder) throws Exception {
        final URL initialModelUrl = Thread.currentThread().getContextClassLoader().getResource("model.xml");
        final File initialModelFile = new File(initialModelUrl.toURI());
        final Model model = XmlModelBuilder.readModelFromUrl(initialModelUrl);

        assertMultilineParam(model);

        final File marshalledModelFile = File.createTempFile("marshalled-model", ".xml", tempFolder);
        XmlModelBuilder.writeModelToFile(model, marshalledModelFile);

        XMLUnit.setIgnoreWhitespace(true);
        assertTrue(XMLUnit.compareXML(new InputSource(new FileReader(initialModelFile)),
                new InputSource(new FileReader(marshalledModelFile))).similar());
    }

    /**
     * Check that schema validation is enabled.
     * 
     * @throws Exception
     */
    @Test
    public void testReadUnknownElement() throws Exception {
        final URL modelUrl = Thread.currentThread().getContextClassLoader()
                .getResource("model-with-unknown-element.xml");
        assertThrows(ModelParsingException.class, () -> {
            XmlModelBuilder.readModelFromUrl(modelUrl);
        });
    }

    private static void assertMultilineParam(final Model model) {
        final Component comp = (Component) model.getComponentRepository().getComponentOrSharedLibrary().get(0);
        final String multilineParamValue = comp.getParameter().get(2).getValue();
        assertEquals("line1\nline2", multilineParamValue);
    }
}
