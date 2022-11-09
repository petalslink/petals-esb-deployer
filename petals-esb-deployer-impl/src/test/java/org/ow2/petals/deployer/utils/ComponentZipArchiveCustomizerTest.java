/**
 * Copyright (c) 2022 Linagora
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipInputStream;

import org.junit.Test;
import org.ow2.petals.admin.api.conf.ZipArchiveCustomizer;
import org.ow2.petals.deployer.runtimemodel.RuntimeComponent;
import org.ow2.petals.deployer.runtimemodel.RuntimeSharedLibrary;
import org.ow2.petals.jbi.descriptor.original.JBIDescriptorBuilder;
import org.ow2.petals.jbi.descriptor.original.generated.Component.SharedLibrary;
import org.ow2.petals.jbi.descriptor.original.generated.Jbi;

public class ComponentZipArchiveCustomizerTest {

    @Test
    public void customize() throws Exception {

        final RuntimeComponent runtimeComponent = new RuntimeComponent("petals-bc-soap",
                ComponentZipArchiveCustomizerTest.class.getResource("/artifacts/petals-bc-soap-5.0.0.zip").toURI()
                        .toURL());

        final String slId = "my-sl-id";
        final String slVersion = "my-sl-version";
        runtimeComponent.addSharedLibrary(new RuntimeSharedLibrary(slId, slVersion));

        final ZipArchiveCustomizer zipCustomizer = new ComponentZipArchiveCustomizer(runtimeComponent);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        zipCustomizer.customize(runtimeComponent.getUrl().openStream(), baos);
        baos.close();

        final Jbi compJbi = JBIDescriptorBuilder.getInstance()
                .buildJavaJBIDescriptorFromArchive(new ZipInputStream(new ByteArrayInputStream(baos.toByteArray())));
        assertNotNull(compJbi.getComponent());
        assertFalse(compJbi.getComponent().getSharedLibraryList().isEmpty());
        assertEquals(1, compJbi.getComponent().getSharedLibraryList().size());
        final SharedLibrary sl = compJbi.getComponent().getSharedLibraryList().get(0);
        assertEquals(slId, sl.getContent());
        assertEquals(slVersion, sl.getVersion());
    }
}
