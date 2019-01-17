/**
 * Copyright (c) 2018-2019 Linagora
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

import java.util.Collection;

import org.ow2.petals.deployer.runtimemodel.RuntimeComponent;
import org.ow2.petals.deployer.runtimemodel.RuntimeContainer;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel;
import org.ow2.petals.deployer.runtimemodel.RuntimeServiceUnit;

/**
 * @author alagane
 */
public class RuntimeModelComparator {
    /**
     * Compare two runtimes models. The URL are not checked.
     * 
     * @param m1
     * @param m2
     * @return true if m1 and m2 are equivalents
     */
    public static boolean compareRuntimeModels(final RuntimeModel m1, final RuntimeModel m2) {
        return compareRuntimeContainerMaps(m1, m2);
    }

    private static boolean compareRuntimeComponentMaps(RuntimeContainer cont1, RuntimeContainer cont2) {
        Collection<RuntimeComponent> compList1 = cont1.getComponents();
        Collection<RuntimeComponent> compList2 = cont2.getComponents();

        for (RuntimeComponent comp1 : compList1) {
            RuntimeComponent comp2 = cont1.getComponent(comp1.getId());
            if (comp2 == null || !compareRuntimeComponents(comp1, comp2)) {
                return false;
            }
        }

        for (RuntimeComponent comp2 : compList2) {
            if (cont2.getComponent(comp2.getId()) == null) {
                return false;
            }
        }

        return true;
    }

    private static boolean compareRuntimeContainerMaps(RuntimeModel m1, RuntimeModel m2) {
        Collection<RuntimeContainer> contList1 = m1.getContainers();
        Collection<RuntimeContainer> contList2 = m2.getContainers();

        for (RuntimeContainer cont1 : contList1) {
            RuntimeContainer cont2 = m1.getContainer(cont1.getId());
            if (cont2 == null || !compareRuntimeContainers(cont1, cont2)) {
                return false;
            }
        }

        for (RuntimeContainer cont2 : contList2) {
            if (m2.getContainer(cont2.getId()) == null) {
                return false;
            }
        }

        return true;
    }

    private static boolean compareRuntimeContainers(RuntimeContainer cont1, RuntimeContainer cont2) {
        return cont1.getId().equals(cont2.getId()) && cont1.getPort() == cont2.getPort()
                && cont1.getUser().equals(cont2.getUser()) && cont1.getPassword().equals(cont2.getPassword())
                && cont1.getHostname().equals(cont2.getHostname()) && compareRuntimeServiceUnitMaps(cont1, cont2)
                && compareRuntimeComponentMaps(cont1, cont2);
    }

    private static boolean compareRuntimeServiceUnitMaps(RuntimeContainer cont1, RuntimeContainer cont2) {
        Collection<RuntimeServiceUnit> suList1 = cont1.getServiceUnits();
        Collection<RuntimeServiceUnit> suList2 = cont2.getServiceUnits();

        for (RuntimeServiceUnit su1 : suList1) {
            RuntimeServiceUnit su2 = cont1.getServiceUnit(su1.getId());
            if (su2 == null || !compareRuntimeServiceUnits(su1, su2)) {
                return false;
            }
        }

        for (RuntimeServiceUnit su2 : suList2) {
            if (cont2.getServiceUnit(su2.getId()) == null) {
                return false;
            }
        }

        return true;
    }

    private static boolean compareRuntimeServiceUnits(RuntimeServiceUnit su1, RuntimeServiceUnit su2) {
        return su1.getId().equals(su2.getId());
    }

    private static boolean compareRuntimeComponents(RuntimeComponent comp1, RuntimeComponent comp2) {
        return comp1.getId().equals(comp2.getId());
    }
}
