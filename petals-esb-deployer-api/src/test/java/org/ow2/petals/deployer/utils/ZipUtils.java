/**
 * Copyright (c) 2018-2020 Linagora
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Alexandre Lagane - Linagora
 */
public class ZipUtils {

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    /**
     * Return a zip archive of a directory (the directory is considered the root of the archive).
     * 
     * The created zip archive is a temporary file.
     *
     * @param resourcePath
     *            The path to the directory in resources
     * @return The zip file created
     * @throws IOException
     * @throws URISyntaxException
     */
    public static File createZipFromResourceDirectory(final String resourcePath)
            throws IOException, URISyntaxException {
        final File zip = Files.createTempFile(null, null).toFile();
        final File resource = new File(
                Thread.currentThread().getContextClassLoader().getResource(resourcePath).toURI());
        final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip));
        for (final File file : resource.listFiles()) {
            addDirectoryToZip("", file, zos);
        }
        zos.close();
        return zip;
    }

    private static void addDirectoryToZip(final String prefix, final File file, final ZipOutputStream zos)
            throws IOException {
        if (file.isDirectory()) {
            for (final File child : file.listFiles()) {
                addDirectoryToZip(prefix + file.getName() + "/", child, zos);
            }
        } else {
            zos.putNextEntry(new ZipEntry(prefix + file.getName()));
            final FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            while (fis.available() > 0) {
                int read = fis.read(buffer, 0, buffer.length);
                zos.write(buffer, 0, read);
            }
            fis.close();
        }
    }
}
