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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Test;
import org.ow2.petals.deployer.runtimemodel.exceptions.DuplicatedContainerException;

/**
 * @author Alexandre Lagane - Linagora
 */
public class RuntimeModelTest {

    @Test
    public void runtimeModelGetters() throws Exception {
        RuntimeModel model = new RuntimeModel();
        Collection<RuntimeContainer> containers = model.getContainers();

        assertEquals(0, containers.size());
        assertNull(model.getContainer("cont1"));
        assertNull(model.getContainer("cont2"));

        RuntimeContainer mockCont1 = mock(RuntimeContainer.class);
        when(mockCont1.getId()).thenReturn("cont1");
        model.addContainer(mockCont1);

        containers = model.getContainers();

        assertEquals(1, containers.size());
        assertSame(mockCont1, model.getContainer("cont1"));
        assertNull(model.getContainer("cont2"));

        RuntimeContainer mockCont2 = mock(RuntimeContainer.class);
        when(mockCont2.getId()).thenReturn("cont2");
        model.addContainer(mockCont2);

        containers = model.getContainers();

        assertEquals(2, containers.size());
        assertSame(mockCont1, model.getContainer("cont1"));
        assertSame(mockCont2, model.getContainer("cont2"));
    }

    @Test
    public void runtimeModelDuplicatedContainerException() throws Exception {
        RuntimeModel model = new RuntimeModel();

        RuntimeContainer mockCont = mock(RuntimeContainer.class);
        when(mockCont.getId()).thenReturn("cont");
        model.addContainer(mockCont);

        RuntimeContainer mockContWithSameId = mock(RuntimeContainer.class);
        when(mockContWithSameId.getId()).thenReturn("cont");
        try {
            model.addContainer(mockContWithSameId);
            fail("Should have caught DuplicatedContainerException");
        } catch (DuplicatedContainerException e) {
        }
    }

    @Test
    public void similarRuntimeModels() throws Exception {
        RuntimeModel model1 = new RuntimeModel();
        RuntimeModel model2 = new RuntimeModel();

        assertTrue(model1.isSimilarTo(model2));
        assertTrue(model2.isSimilarTo(model1));

        RuntimeContainer mockCont1Model1 = mock(RuntimeContainer.class);
        RuntimeContainer mockCont2Model1 = mock(RuntimeContainer.class);
        RuntimeContainer mockCont1Model2 = mock(RuntimeContainer.class);
        RuntimeContainer mockCont2Model2 = mock(RuntimeContainer.class);

        when(mockCont1Model1.getId()).thenReturn("cont1");
        when(mockCont1Model1.isSimilarTo(any())).thenReturn(false);
        when(mockCont1Model1.isSimilarTo(mockCont1Model2)).thenReturn(true);

        when(mockCont2Model1.getId()).thenReturn("cont2");
        when(mockCont2Model1.isSimilarTo(any())).thenReturn(false);
        when(mockCont2Model1.isSimilarTo(mockCont2Model2)).thenReturn(true);

        when(mockCont1Model2.getId()).thenReturn("cont1");
        when(mockCont1Model2.isSimilarTo(any())).thenReturn(false);
        when(mockCont1Model2.isSimilarTo(mockCont1Model1)).thenReturn(true);

        when(mockCont2Model2.getId()).thenReturn("cont2");
        when(mockCont2Model2.isSimilarTo(any())).thenReturn(false);
        when(mockCont2Model2.isSimilarTo(mockCont2Model1)).thenReturn(true);

        model1.addContainer(mockCont1Model1);
        assertFalse(model1.isSimilarTo(model2));
        assertFalse(model2.isSimilarTo(model1));

        model1.addContainer(mockCont2Model1);
        assertFalse(model1.isSimilarTo(model2));
        assertFalse(model2.isSimilarTo(model1));

        model2.addContainer(mockCont1Model2);
        assertFalse(model1.isSimilarTo(model2));
        assertFalse(model2.isSimilarTo(model1));

        model2.addContainer(mockCont2Model2);
        assertTrue(model1.isSimilarTo(model2));
        assertTrue(model2.isSimilarTo(model1));
    }
}
