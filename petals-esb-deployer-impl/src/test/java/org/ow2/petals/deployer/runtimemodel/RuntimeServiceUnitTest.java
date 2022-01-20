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
public class RuntimeServiceUnitTest {

    @Test
    public void runtimeServiceUnitGetters() throws Exception {
        URL url = new URL("file:/su.zip");

        RuntimeServiceUnit su = new RuntimeServiceUnit("su");
        assertEquals("su", su.getId());
        assertNull(su.getUrl());

        su.setUrl(url);
        assertEquals(url, su.getUrl());

        su = new RuntimeServiceUnit("su", url);
        assertEquals("su", su.getId());
        assertEquals(url, su.getUrl());
    }

    @Test
    public void similarRuntimeServiceUnits() throws Exception {
        RuntimeServiceUnit su = new RuntimeServiceUnit("su", new URL("file:/su.zip"));
        RuntimeServiceUnit suWithDifferentUrl = new RuntimeServiceUnit("su", new URL("file:/other-url.zip"));

        assertTrue(su.isSimilarTo(suWithDifferentUrl));
        assertTrue(suWithDifferentUrl.isSimilarTo(su));
    }

    @Test
    public void notSimilarRuntimeServiceUnits() throws Exception {
        RuntimeServiceUnit su = new RuntimeServiceUnit("su", new URL("file:/su.zip"));
        RuntimeServiceUnit suWithDifferentId = new RuntimeServiceUnit("differentId", new URL("file:/su.zip"));

        assertFalse(su.isSimilarTo(suWithDifferentId));
        assertFalse(suWithDifferentId.isSimilarTo(su));
    }
}
