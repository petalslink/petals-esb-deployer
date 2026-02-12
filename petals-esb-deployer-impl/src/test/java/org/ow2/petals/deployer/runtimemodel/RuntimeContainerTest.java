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
import org.ow2.petals.deployer.runtimemodel.exceptions.DuplicatedComponentException;
import org.ow2.petals.deployer.runtimemodel.exceptions.DuplicatedServiceUnitException;

/**
 * @author Alexandre Lagane - Linagora
 */
public class RuntimeContainerTest {

    @Test
    public void runtimeContainerGetters() throws Exception {
        final RuntimeContainer cont = new RuntimeContainer("cont", 7700, "user", "password", "localhost");

        assertEquals("cont", cont.getId());
        assertEquals(7700, cont.getPort());
        assertEquals("user", cont.getUser());
        assertEquals("password", cont.getPassword());
        assertEquals("localhost", cont.getHostname());

        cont.setHostname("192.168.1.42");
        assertEquals("192.168.1.42", cont.getHostname());

        final Collection<RuntimeComponent> initialComponents = cont.getComponents();

        assertEquals(0, initialComponents.size());
        assertNull(cont.getComponent("comp1"));
        assertNull(cont.getComponent("comp2"));

        final RuntimeComponent mockComp1 = mock(RuntimeComponent.class);
        when(mockComp1.getId()).thenReturn("comp1");
        cont.addComponent(mockComp1);

        assertEquals(1, cont.getComponents().size());
        assertSame(mockComp1, cont.getComponent("comp1"));
        assertNull(cont.getComponent("comp2"));

        final RuntimeComponent mockComp2 = mock(RuntimeComponent.class);
        when(mockComp2.getId()).thenReturn("comp2");
        cont.addComponent(mockComp2);

        assertEquals(2, cont.getComponents().size());
        assertSame(mockComp1, cont.getComponent("comp1"));
        assertSame(mockComp2, cont.getComponent("comp2"));

        final Collection<RuntimeServiceUnit> serviceUnits = cont.getServiceUnits();

        assertEquals(0, serviceUnits.size());
        assertNull(cont.getServiceUnit("su1"));
        assertNull(cont.getServiceUnit("su2"));

        final RuntimeServiceUnit mockSu1 = mock(RuntimeServiceUnit.class);
        when(mockSu1.getId()).thenReturn("su1");
        cont.addServiceUnit(mockSu1);

        assertEquals(1, cont.getServiceUnits().size());
        assertSame(mockSu1, cont.getServiceUnit("su1"));
        assertNull(cont.getServiceUnit("su2"));

        final RuntimeServiceUnit mockSu2 = mock(RuntimeServiceUnit.class);
        when(mockSu2.getId()).thenReturn("su2");
        cont.addServiceUnit(mockSu2);

        assertEquals(2, cont.getServiceUnits().size());
        assertSame(mockSu1, cont.getServiceUnit("su1"));
        assertSame(mockSu2, cont.getServiceUnit("su2"));
    }

    @Test
    public void duplicatedComponentException() throws Exception {
        RuntimeContainer cont = new RuntimeContainer("cont", 7700, "user", "password", "localhost");

        RuntimeComponent mockComp = mock(RuntimeComponent.class);
        when(mockComp.getId()).thenReturn("comp");
        cont.addComponent(mockComp);

        RuntimeComponent mockCompWithSameId = mock(RuntimeComponent.class);
        when(mockCompWithSameId.getId()).thenReturn("comp");
        assertThrows(DuplicatedComponentException.class, () -> {
            cont.addComponent(mockCompWithSameId);
        }, "Should have caught DuplicatedComponentException");
    }

    @Test
    public void duplicatedServiceUnitException() throws Exception {
        RuntimeContainer cont = new RuntimeContainer("cont", 7700, "user", "password", "localhost");

        RuntimeServiceUnit mockSu = mock(RuntimeServiceUnit.class);
        when(mockSu.getId()).thenReturn("su");
        cont.addServiceUnit(mockSu);

        RuntimeServiceUnit mockSuWithSameId = mock(RuntimeServiceUnit.class);
        when(mockSuWithSameId.getId()).thenReturn("su");
        assertThrows(DuplicatedServiceUnitException.class, () -> {
            cont.addServiceUnit(mockSuWithSameId);
        }, "Should have caught DuplicatedServiceUnitException");
    }

    @Test
    public void similarContainers() throws Exception {
        RuntimeContainer cont1 = new RuntimeContainer("cont", 7700, "user", "password", "localhost");
        RuntimeContainer cont2 = new RuntimeContainer("cont", 7700, "user", "password", "localhost");

        assertTrue(cont1.isSimilarTo(cont2));
        assertTrue(cont2.isSimilarTo(cont1));

        RuntimeComponent mockComp1Cont1 = mock(RuntimeComponent.class);
        RuntimeComponent mockComp2Cont1 = mock(RuntimeComponent.class);
        RuntimeComponent mockComp1Cont2 = mock(RuntimeComponent.class);
        RuntimeComponent mockComp2Cont2 = mock(RuntimeComponent.class);

        when(mockComp1Cont1.getId()).thenReturn("comp1");
        when(mockComp1Cont1.isSimilarTo(any())).thenReturn(false);
        when(mockComp1Cont1.isSimilarTo(mockComp1Cont2)).thenReturn(true);

        when(mockComp2Cont1.getId()).thenReturn("comp2");
        when(mockComp2Cont1.isSimilarTo(any())).thenReturn(false);
        when(mockComp2Cont1.isSimilarTo(mockComp2Cont2)).thenReturn(true);

        when(mockComp1Cont2.getId()).thenReturn("comp1");
        when(mockComp1Cont2.isSimilarTo(any())).thenReturn(false);
        when(mockComp1Cont2.isSimilarTo(mockComp1Cont1)).thenReturn(true);

        when(mockComp2Cont2.getId()).thenReturn("comp2");
        when(mockComp2Cont2.isSimilarTo(any())).thenReturn(false);
        when(mockComp2Cont2.isSimilarTo(mockComp2Cont1)).thenReturn(true);

        cont1.addComponent(mockComp1Cont1);
        assertFalse(cont1.isSimilarTo(cont2));
        assertFalse(cont2.isSimilarTo(cont1));

        cont1.addComponent(mockComp2Cont1);
        assertFalse(cont1.isSimilarTo(cont2));
        assertFalse(cont2.isSimilarTo(cont1));

        cont2.addComponent(mockComp2Cont2);
        assertFalse(cont1.isSimilarTo(cont2));
        assertFalse(cont2.isSimilarTo(cont1));

        cont2.addComponent(mockComp1Cont2);
        assertTrue(cont1.isSimilarTo(cont2));
        assertTrue(cont2.isSimilarTo(cont1));

        RuntimeServiceUnit mockSu1Cont1 = mock(RuntimeServiceUnit.class);
        RuntimeServiceUnit mockSu2Cont1 = mock(RuntimeServiceUnit.class);
        RuntimeServiceUnit mockSu1Cont2 = mock(RuntimeServiceUnit.class);
        RuntimeServiceUnit mockSu2Cont2 = mock(RuntimeServiceUnit.class);

        when(mockSu1Cont1.getId()).thenReturn("su1");
        when(mockSu1Cont1.isSimilarTo(any())).thenReturn(false);
        when(mockSu1Cont1.isSimilarTo(mockSu1Cont2)).thenReturn(true);

        when(mockSu2Cont1.getId()).thenReturn("su2");
        when(mockSu2Cont1.isSimilarTo(any())).thenReturn(false);
        when(mockSu2Cont1.isSimilarTo(mockSu2Cont2)).thenReturn(true);

        when(mockSu1Cont2.getId()).thenReturn("su1");
        when(mockSu1Cont2.isSimilarTo(any())).thenReturn(false);
        when(mockSu1Cont2.isSimilarTo(mockSu1Cont1)).thenReturn(true);

        when(mockSu2Cont2.getId()).thenReturn("su2");
        when(mockSu2Cont2.isSimilarTo(any())).thenReturn(false);
        when(mockSu2Cont2.isSimilarTo(mockSu2Cont1)).thenReturn(true);

        cont1.addServiceUnit(mockSu1Cont1);
        assertFalse(cont1.isSimilarTo(cont2));
        assertFalse(cont2.isSimilarTo(cont1));

        cont1.addServiceUnit(mockSu2Cont1);
        assertFalse(cont1.isSimilarTo(cont2));
        assertFalse(cont2.isSimilarTo(cont1));

        cont2.addServiceUnit(mockSu2Cont2);
        assertFalse(cont1.isSimilarTo(cont2));
        assertFalse(cont2.isSimilarTo(cont1));

        cont2.addServiceUnit(mockSu1Cont2);
        assertTrue(cont1.isSimilarTo(cont2));
        assertTrue(cont2.isSimilarTo(cont1));
    }

    @Test
    public void notSimilarContainers() {
        RuntimeContainer cont = new RuntimeContainer("cont", 7700, "user", "password", "localhost");

        RuntimeContainer contWithDifferentId = new RuntimeContainer("otherId", 7700, "user", "password", "localhost");
        assertFalse(cont.isSimilarTo(contWithDifferentId));
        assertFalse(contWithDifferentId.isSimilarTo(cont));

        RuntimeContainer contWithDifferentPort = new RuntimeContainer("cont", 9999, "user", "password", "localhost");
        assertFalse(cont.isSimilarTo(contWithDifferentPort));
        assertFalse(contWithDifferentPort.isSimilarTo(cont));

        RuntimeContainer contWithDifferentUser = new RuntimeContainer("cont", 7700, "otherUser", "password",
                "localhost");
        assertFalse(cont.isSimilarTo(contWithDifferentUser));
        assertFalse(contWithDifferentUser.isSimilarTo(cont));

        RuntimeContainer contWithDifferentPassword = new RuntimeContainer("cont", 7700, "user", "otherPassword",
                "localhost");
        assertFalse(cont.isSimilarTo(contWithDifferentPassword));
        assertFalse(contWithDifferentPassword.isSimilarTo(cont));

        RuntimeContainer contWithDifferentHostname = new RuntimeContainer("cont", 7700, "user", "password",
                "192.168.1.42");
        assertFalse(cont.isSimilarTo(contWithDifferentHostname));
        assertFalse(contWithDifferentHostname.isSimilarTo(cont));
    }
}
