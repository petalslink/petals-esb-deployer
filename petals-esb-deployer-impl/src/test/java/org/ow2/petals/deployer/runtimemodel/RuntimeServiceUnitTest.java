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
public class RuntimeServiceUnitTest {

    @Test
    public void runtimeServiceUnitGetters() throws Exception {
        final URL url = new URL("file:/su.zip");

        final RuntimeServiceUnit su1 = new RuntimeServiceUnit("su");
        assertEquals("su", su1.getId());
        assertNull(su1.getUrl());

        su1.setUrl(url);
        assertEquals(url, su1.getUrl());

        final RuntimeServiceUnit su2 = new RuntimeServiceUnit("su", url);
        assertEquals("su", su2.getId());
        assertEquals(url, su2.getUrl());
    }

    @Test
    public void similarRuntimeServiceUnits() throws Exception {
        final RuntimeServiceUnit su = new RuntimeServiceUnit("su", new URL("file:/su.zip"));
        final RuntimeServiceUnit suWithDifferentUrl = new RuntimeServiceUnit("su", new URL("file:/other-url.zip"));

        assertTrue(su.isSimilarTo(suWithDifferentUrl));
        assertTrue(suWithDifferentUrl.isSimilarTo(su));
    }

    @Test
    public void notSimilarRuntimeServiceUnits() throws Exception {
        final RuntimeServiceUnit su = new RuntimeServiceUnit("su", new URL("file:/su.zip"));
        final RuntimeServiceUnit suWithDifferentId = new RuntimeServiceUnit("differentId", new URL("file:/su.zip"));

        assertFalse(su.isSimilarTo(suWithDifferentId));
        assertFalse(suWithDifferentId.isSimilarTo(su));
    }
}
