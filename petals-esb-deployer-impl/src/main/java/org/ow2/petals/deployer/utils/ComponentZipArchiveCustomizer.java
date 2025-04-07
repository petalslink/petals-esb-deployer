/**
 * Copyright (c) 2022-2025 Linagora
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.ow2.petals.admin.api.conf.ZipArchiveCustomizer;
import org.ow2.petals.admin.api.exception.ZipArchiveCustomizerException;
import org.ow2.petals.deployer.runtimemodel.RuntimeComponent;
import org.ow2.petals.deployer.runtimemodel.RuntimeSharedLibrary;
import org.ow2.petals.jbi.descriptor.JBIDescriptorException;
import org.ow2.petals.jbi.descriptor.original.JBIDescriptorBuilder;
import org.ow2.petals.jbi.descriptor.original.generated.Component.SharedLibrary;
import org.ow2.petals.jbi.descriptor.original.generated.Jbi;

public class ComponentZipArchiveCustomizer implements ZipArchiveCustomizer {

    private final RuntimeComponent runtimeComponent;

    public ComponentZipArchiveCustomizer(final RuntimeComponent runtimeComponent) {
        this.runtimeComponent = runtimeComponent;
    }

    @Override
    public void customize(final InputStream inputStream, final OutputStream outputStream)
            throws ZipArchiveCustomizerException {
        final ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        final ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

        try {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().equals(JBIDescriptorBuilder.JBI_DESCRIPTOR_RESOURCE_IN_ARCHIVE)) {
                    zipOutputStream.putNextEntry(new ZipEntry(zipEntry.getName()));

                    // Hack to avoid to close the input stream at XML parser level (see note about
                    // JBIDescriptorBuilder.buildJavaJBIDescriptor())
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    IOUtils.copy(zipInputStream, baos);
                    final Jbi compJbi = JBIDescriptorBuilder.getInstance()
                            .buildJavaJBIDescriptor(new ByteArrayInputStream(baos.toByteArray()));
                    this.updateJBIDescriptor(compJbi);
                    JBIDescriptorBuilder.getInstance().writeXMLJBIdescriptor(compJbi, zipOutputStream);
                } else {
                    zipOutputStream.putNextEntry(new ZipEntry(zipEntry));
                    IOUtils.copy(zipInputStream, zipOutputStream);
                }
                zipInputStream.closeEntry();
                zipOutputStream.closeEntry();
            }
            zipOutputStream.finish();
        } catch (final IOException | JBIDescriptorException e) {
            throw new ZipArchiveCustomizerException(e);
        }

    }

    /**
     * <p>
     * The JBI descriptor of the component archive must contain the required shared libraries. So, it could be required
     * to update the JBI descriptor of the JBI component archive:
     * </p>
     * <ul>
     * <li>to match the shared library definition of the model.</li>
     * </ul>
     */
    private void updateJBIDescriptor(final Jbi compJbi) throws ZipArchiveCustomizerException {

        if (compJbi.getComponent() != null) {
            for (final RuntimeSharedLibrary runtimeSharedLibrary : this.runtimeComponent.getSharedLibraries()) {
                final SharedLibrary sharedLibrary = new SharedLibrary();
                sharedLibrary.setContent(runtimeSharedLibrary.getId());
                sharedLibrary.setVersion(runtimeSharedLibrary.getVersion());
                compJbi.getComponent().getSharedLibraryList().add(sharedLibrary);
            }
        } else {
            throw new ZipArchiveCustomizerException(
                    String.format("URL '%s' of the component '%s' seems to be an artifact that is not a component.",
                            this.runtimeComponent.getUrl(), this.runtimeComponent.getId()));
        }
    }
}
