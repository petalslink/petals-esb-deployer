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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.junit.Test;

/**
 * @author Alexandre Lagane - Linagora
 */
public class RuntimeComponentTest {

    @Test
    public void runtimeComponentGetters() throws Exception {
        URL url = new URL("file:/comp.zip");

        RuntimeComponent comp = new RuntimeComponent("comp");
        assertEquals("comp", comp.getId());
        assertNull(comp.getUrl());

        comp.setUrl(url);
        assertEquals(url, comp.getUrl());

        comp = new RuntimeComponent("comp", url);
        assertEquals("comp", comp.getId());
        assertEquals(url, comp.getUrl());
    }

    @Test
    public void similarRuntimeComponents() throws Exception {
        RuntimeComponent comp1 = new RuntimeComponent("comp", new URL("file:/comp.zip"));
        RuntimeComponent comp2 = new RuntimeComponent("comp", new URL("file:/other-comp.zip"));

        assertTrue(comp1.isSimilarTo(comp2));
        assertTrue(comp2.isSimilarTo(comp1));
    }

    @Test
    public void notSimilarRuntimeComponents() throws Exception {
        RuntimeComponent comp = new RuntimeComponent("comp", new URL("file:/comp.zip"));
        RuntimeComponent compWithDifferentId = new RuntimeComponent("differentId", new URL("file:/comp.zip"));

        assertFalse(comp.isSimilarTo(compWithDifferentId));
        assertFalse(compWithDifferentId.isSimilarTo(comp));
    }
}
