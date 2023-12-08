/**
 * Copyright (c) 2019-2023 Linagora
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
package org.ow2.petals.deployer.runtimemodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;

import org.junit.jupiter.api.Test;

/**
 * @author Alexandre Lagane - Linagora
 */
public class RuntimeSharedLibraryTest {

    @Test
    public void runtimeSharedLibraryGetters() throws Exception {
        final URL url = new URL("file:/sl.zip");

        final RuntimeSharedLibrary sl1 = new RuntimeSharedLibrary("sl-id", "1.0");
        assertEquals("sl-id", sl1.getId());
        assertEquals("1.0", sl1.getVersion());
        assertNull(sl1.getUrl());

        sl1.setUrl(url);
        assertEquals(url, sl1.getUrl());

        final RuntimeSharedLibrary sl2 = new RuntimeSharedLibrary("sl-id", "1.0", url);
        assertEquals("sl-id", sl2.getId());
        assertEquals("1.0", sl2.getVersion());
        assertEquals(url, sl2.getUrl());
    }

    @Test
    public void similarRuntimeSharedLibraries() throws Exception {
        final RuntimeSharedLibrary sl = new RuntimeSharedLibrary("sl-id", "1.0", new URL("file:/sl.zip"));
        final RuntimeSharedLibrary slWithDifferentUrl = new RuntimeSharedLibrary("sl-id", "1.0",
                new URL("file:/other-url.zip"));

        assertTrue(sl.isSimilarTo(slWithDifferentUrl));
        assertTrue(slWithDifferentUrl.isSimilarTo(sl));
    }

    @Test
    public void notSimilarRuntimeSharedLibraries() throws Exception {
        final RuntimeSharedLibrary sl1 = new RuntimeSharedLibrary("sl-id", "1.0", new URL("file:/sl.zip"));
        final RuntimeSharedLibrary suWithDifferentId = new RuntimeSharedLibrary("other-id", "1.0",
                new URL("file:/sl.zip"));

        assertFalse(sl1.isSimilarTo(suWithDifferentId));
        assertFalse(suWithDifferentId.isSimilarTo(sl1));

        final RuntimeSharedLibrary suWithDifferentVersion = new RuntimeSharedLibrary("sl-id", "2.0",
                new URL("file:/sl.zip"));

        assertFalse(sl1.isSimilarTo(suWithDifferentVersion));
        assertFalse(suWithDifferentVersion.isSimilarTo(sl1));
    }
}
