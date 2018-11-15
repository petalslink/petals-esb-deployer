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

public class RuntimeModelComparator {
    public boolean compareRuntimeModels(final RuntimeModel m1, final RuntimeModel m2) {
        return compareRuntimeContainerMaps(m1, m2);
    }

    private boolean compareRuntimeComponentMaps(RuntimeContainer cont1, RuntimeContainer cont2) {
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

    private boolean compareRuntimeContainerMaps(RuntimeModel m1, RuntimeModel m2) {
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

    private boolean compareRuntimeContainers(RuntimeContainer cont1, RuntimeContainer cont2) {
        return cont1.getId().equals(cont2.getId()) && cont1.getPort() == cont2.getPort()
                && cont1.getUser().equals(cont2.getUser()) && cont1.getPassword().equals(cont2.getPassword())
                && cont1.getHostname().equals(cont2.getHostname()) && compareRuntimeServiceUnitMaps(cont1, cont2)
                && compareRuntimeComponentMaps(cont1, cont2);
    }

    private boolean compareRuntimeServiceUnitMaps(RuntimeContainer cont1, RuntimeContainer cont2) {
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

    private boolean compareRuntimeServiceUnits(RuntimeServiceUnit su1, RuntimeServiceUnit su2) {
        return su1.getId().equals(su2.getId());
    }

    private boolean compareRuntimeComponents(RuntimeComponent comp1, RuntimeComponent comp2) {
        return comp1.getId().equals(comp2.getId());
    }

//    public boolean compareModels(final Model m1, final Model m2) {
//        Bus bus1 = m1.getBusModel().getBuses().get(0);
//        Bus bus2 = m2.getBusModel().getBuses().get(0);
//        ContainerInstance contInst1 = bus1.getTopologyInstance().getContainerInstances().get(0);
//        ContainerInstance contInst2 = bus2.getTopologyInstance().getContainerInstances().get(0);
//
//        if (!compareContainerInstances(contInst1, contInst2)) {
//            return false;
//        }
//
//        return true;
//    }
//
//    public boolean compareServiceUnitInstanceLists(final List<ServiceUnitInstance> suInstances1,
//            final List<ServiceUnitInstance> suInstances2) {
//
//        if (suInstances1.size() != suInstances2.size()) {
//            return false;
//        }
//
//        // Check unicity and build map of suInstances1
//        Map<String, ServiceUnitInstance> suInstById1 = new HashMap<String, ServiceUnitInstance>();
//        for (ServiceUnitInstance suInst : suInstances1) {
//            String suId = suInst.getReference();
//            if (suInstById1.containsKey(suId)) {
//                return false;
//            }
//            suInstById1.put(suId, suInst);
//        }
//
//        // Check unicity of suInstances2 and compare its elements with suInstances1
//        Set<String> suInstIds = new HashSet<String>();
//        for (ServiceUnitInstance suInst : suInstances2) {
//            String suId = suInst.getReference();
//            if (!suInstIds.add(suId) || !compareServiceUnitInstances(suInst, suInstById1.get(suId))) {
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//    public boolean compareComponentInstanceLists(final List<ComponentInstance> compInstances1,
//            final List<ComponentInstance> compInstances2) {
//
//        if (compInstances1.size() != compInstances2.size()) {
//            return false;
//        }
//
//        // Check unicity and build map of compInstances1
//        Map<String, ComponentInstance> compInstById1 = new HashMap<String, ComponentInstance>();
//        for (ComponentInstance compInst : compInstances1) {
//            String compInstId = compInst.getReference();
//            if (compInstById1.containsKey(compInstId)) {
//                return false;
//            }
//            compInstById1.put(compInstId, compInst);
//        }
//
//        // Check unicity of compInstances2 and compare its elements with compInstances1
//        Set<String> compInstIds = new HashSet<String>();
//        for (ComponentInstance compInst : compInstances2) {
//            String compInstId = compInst.getReference();
//            if (!compInstIds.add(compInstId) || !compareComponentsInstances(compInst, compInstById1.get(compInstId))) {
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//    /**
//     * m1 and m2 must be both instances of {@link ProvisionedMachine}
//     *
//     * @param m1
//     * @param m2
//     * @return false if m1 and m2 are different
//     */
//    public boolean compareMachines(Machine m1, Machine m2) {
//        return m1 instanceof ProvisionedMachine && m2 instanceof ProvisionedMachine
//                && ((ProvisionedMachine) m1).getHostname().equals(((ProvisionedMachine) m2).getHostname());
//    }
//
//    /**
//     * We only compare container instances, their getters will fetch data from their reference {@link Container}.
//     *
//     * @param ci1
//     * @param ci2
//     * @return false if ci1 and ci2 are different
//     */
//    public boolean compareContainerInstances(final ContainerInstance ci1, final ContainerInstance ci2) {
//        return ci1.getReference().equals(ci2.getReference()) && ci1.getJmxUser().equals(ci2.getJmxUser())
//                && ci1.getJmxPort().equals(ci2.getJmxPort()) && ci1.getJmxPassword().equals(ci2.getJmxPassword())
//                && compareServiceUnitInstanceLists(ci1.getServiceUnitInstances(), ci2.getServiceUnitInstances())
//                && compareComponentInstanceLists(ci1.getComponentInstances(), ci2.getComponentInstances());
//    }
//
//    public boolean compareServiceUnitInstances(final ServiceUnitInstance sui1, final ServiceUnitInstance sui2) {
//        return sui1.getContainerInstanceReference().equals(sui2.getContainerInstanceReference())
//                && sui1.getReference().equals(sui2.getReference());
//    }
//
//    /**
//     * @param su1
//     * @param su2
//     * @return false if su1 and su2 are different
//     */
//    public boolean compareServiceUnits(final ServiceUnit su1, final ServiceUnit su2) {
//        return su1.getId().equals(su2.getId()) && su1.getTargetComponent().equals(su2.getTargetComponent());
//    }
//
//    public boolean compareComponentsInstances(final ComponentInstance ci1, final ComponentInstance ci2) {
//        return ci1.getReference().equals(ci2.getReference()) && ci1.getReference().equals(ci2.getReference());
//    }
//
//    /**
//     * The urls are not compared, because we cannot set them when exporting model from a running Petals.
//     *
//     * @param c1
//     * @param c2
//     * @return false if c1 and c2 are different
//     */
//    public boolean compareComponents(final Component c1, final Component c2) {
//        return c1.getId().equals(c2.getId());
//    }
}
