/**
 * Copyright (c) 2019-2022 Linagora
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.junit.Test;

/**
 * @author Alexandre Lagane - Linagora
 */
public class RuntimeSharedLibraryTest {

    @Test
    public void runtimeSharedLibraryGetters() throws Exception {
        URL url = new URL("file:/sl.zip");

        RuntimeSharedLibrary sl = new RuntimeSharedLibrary("sl-id", "1.0");
        assertEquals("sl-id", sl.getId());
        assertEquals("1.0", sl.getVersion());
        assertNull(sl.getUrl());

        sl.setUrl(url);
        assertEquals(url, sl.getUrl());

        sl = new RuntimeSharedLibrary("sl-id", "1.0", url);
        assertEquals("sl-id", sl.getId());
        assertEquals("1.0", sl.getVersion());
        assertEquals(url, sl.getUrl());
    }

    @Test
    public void similarRuntimeSharedLibraries() throws Exception {
        RuntimeSharedLibrary sl = new RuntimeSharedLibrary("sl-id", "1.0", new URL("file:/sl.zip"));
        RuntimeSharedLibrary slWithDifferentUrl = new RuntimeSharedLibrary("sl-id", "1.0",
                new URL("file:/other-url.zip"));

        assertTrue(sl.isSimilarTo(slWithDifferentUrl));
        assertTrue(slWithDifferentUrl.isSimilarTo(sl));
    }

    @Test
    public void notSimilarRuntimeSharedLibraries() throws Exception {
        RuntimeSharedLibrary sl = new RuntimeSharedLibrary("sl-id", "1.0", new URL("file:/sl.zip"));
        RuntimeSharedLibrary suWithDifferentId = new RuntimeSharedLibrary("other-id", "1.0", new URL("file:/sl.zip"));

        assertFalse(sl.isSimilarTo(suWithDifferentId));
        assertFalse(suWithDifferentId.isSimilarTo(sl));

        sl = new RuntimeSharedLibrary("sl-id", "1.0", new URL("file:/sl.zip"));
        suWithDifferentId = new RuntimeSharedLibrary("sl-id", "2.0", new URL("file:/sl.zip"));

        assertFalse(sl.isSimilarTo(suWithDifferentId));
        assertFalse(suWithDifferentId.isSimilarTo(sl));
    }
}
