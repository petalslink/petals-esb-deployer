/**
 * Copyright (c) 2019-2026 Linagora
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
public class RuntimeComponentTest {

    @Test
    public void runtimeComponentGetters() throws Exception {
        final URL url = new URL("file:/comp.zip");

        final RuntimeComponent comp = new RuntimeComponent("comp");
        assertEquals("comp", comp.getId());
        assertNull(comp.getUrl());

        comp.setUrl(url);
        assertEquals(url, comp.getUrl());

        final RuntimeComponent compFromUrl = new RuntimeComponent("comp", url);
        assertEquals("comp", compFromUrl.getId());
        assertEquals(url, compFromUrl.getUrl());
    }

    @Test
    public void similarRuntimeComponents() throws Exception {
        final RuntimeComponent comp1 = new RuntimeComponent("comp", new URL("file:/comp.zip"));
        final RuntimeComponent comp2 = new RuntimeComponent("comp", new URL("file:/other-comp.zip"));

        assertTrue(comp1.isSimilarTo(comp2));
        assertTrue(comp2.isSimilarTo(comp1));
    }

    @Test
    public void notSimilarRuntimeComponents() throws Exception {
        final RuntimeComponent comp = new RuntimeComponent("comp", new URL("file:/comp.zip"));
        final RuntimeComponent compWithDifferentId = new RuntimeComponent("differentId", new URL("file:/comp.zip"));

        assertFalse(comp.isSimilarTo(compWithDifferentId));
        assertFalse(compWithDifferentId.isSimilarTo(comp));
    }
}
