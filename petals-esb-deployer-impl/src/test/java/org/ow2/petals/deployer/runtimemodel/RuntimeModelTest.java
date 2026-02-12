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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.ow2.petals.deployer.runtimemodel.exceptions.DuplicatedContainerException;

/**
 * @author Alexandre Lagane - Linagora
 */
public class RuntimeModelTest {

    @Test
    public void runtimeModelGetters() throws Exception {
        final RuntimeModel model = new RuntimeModel();
        final Collection<RuntimeContainer> containers = model.getContainers();

        assertEquals(0, containers.size());
        assertNull(model.getContainer("cont1"));
        assertNull(model.getContainer("cont2"));

        final RuntimeContainer mockCont1 = mock(RuntimeContainer.class);
        when(mockCont1.getId()).thenReturn("cont1");
        model.addContainer(mockCont1);

        assertEquals(1, model.getContainers().size());
        assertSame(mockCont1, model.getContainer("cont1"));
        assertNull(model.getContainer("cont2"));

        final RuntimeContainer mockCont2 = mock(RuntimeContainer.class);
        when(mockCont2.getId()).thenReturn("cont2");
        model.addContainer(mockCont2);

        assertEquals(2, model.getContainers().size());
        assertSame(mockCont1, model.getContainer("cont1"));
        assertSame(mockCont2, model.getContainer("cont2"));
    }

    @Test
    public void runtimeModelDuplicatedContainerException() throws Exception {
        final RuntimeModel model = new RuntimeModel();

        final RuntimeContainer mockCont = mock(RuntimeContainer.class);
        when(mockCont.getId()).thenReturn("cont");
        model.addContainer(mockCont);

        final RuntimeContainer mockContWithSameId = mock(RuntimeContainer.class);
        when(mockContWithSameId.getId()).thenReturn("cont");
        assertThrows(DuplicatedContainerException.class, () -> {
            model.addContainer(mockContWithSameId);
        }, "Should have caught DuplicatedContainerException");
    }

    @Test
    public void similarRuntimeModels() throws Exception {
        final RuntimeModel model1 = new RuntimeModel();
        final RuntimeModel model2 = new RuntimeModel();

        assertTrue(model1.isSimilarTo(model2));
        assertTrue(model2.isSimilarTo(model1));

        final RuntimeContainer mockCont1Model1 = mock(RuntimeContainer.class);
        final RuntimeContainer mockCont2Model1 = mock(RuntimeContainer.class);
        final RuntimeContainer mockCont1Model2 = mock(RuntimeContainer.class);
        final RuntimeContainer mockCont2Model2 = mock(RuntimeContainer.class);

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
