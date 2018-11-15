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

import org.junit.Before;

public class RuntimeModelComparatorTest {

    private RuntimeModelComparator comparator;

    @Before
    public void setupModelComparator() {
        comparator = new RuntimeModelComparator();
    }

//    @Test
//    public void compareComponentsWithDifferentIds() {
//        Component c1 = new Component();
//        c1.setId("comp1");
//
//        Component c2 = new Component();
//        c2.setId("comp2");
//
//        assertFalse(comparator.compareComponents(c1, c2));
//        assertFalse(comparator.compareComponents(c1, c2));
//    }
//
//    @Test
//    public void compareComponents() {
//        Component c1 = new Component();
//        c1.setId("comp");
//
//        Component c2 = new Component();
//        c2.setId("comp");
//
//        assertTrue(comparator.compareComponents(c1, c2));
//    }
//
//    @Test
//    public void compareComponentInstancesWithDifferentComponents() {
//        ModelComparator mockedComparator = spy(ModelComparator.class);
//        doReturn(false).when(mockedComparator).compareComponents(null, null);
//
//        ComponentInstance ci1 = new ComponentInstance();
//        ci1.setReference("comp");
//
//        ComponentInstance ci2 = new ComponentInstance();
//        ci2.setReference("comp");
//
//        assertFalse(mockedComparator.compareComponentsInstances(ci1, ci2));
//        assertFalse(mockedComparator.compareComponentsInstances(ci2, ci1));
//    }
//
//    @Test
//    public void compareComponentInstancesWithDifferentIds() {
//        ModelComparator mockedComparator = spy(ModelComparator.class);
//        doReturn(true).when(mockedComparator).compareComponents(null, null);
//
//        ComponentInstance ci1 = new ComponentInstance();
//        ci1.setReference("comp1");
//
//        ComponentInstance ci2 = new ComponentInstance();
//        ci2.setReference("comp2");
//
//        assertFalse(mockedComparator.compareComponentsInstances(ci1, ci2));
//        assertFalse(mockedComparator.compareComponentsInstances(ci2, ci1));
//    }
//
//    @Test
//    public void compareComponentInstances() {
//        ModelComparator mockedComparator = spy(ModelComparator.class);
//        doReturn(true).when(mockedComparator).compareComponents(null, null);
//
//        ComponentInstance ci1 = new ComponentInstance();
//        ci1.setReference("comp");
//
//        ComponentInstance ci2 = new ComponentInstance();
//        ci2.setReference("comp");
//
//        assertTrue(mockedComparator.compareComponentsInstances(ci1, ci2));
//        assertTrue(mockedComparator.compareComponentsInstances(ci2, ci1));
//    }
//
//    @Test
//    public void compareServiceUnitsWithDifferentIds() {
//        ServiceUnit su1 = new ServiceUnit();
//        su1.setId("su1");
//        su1.setTargetComponent("comp");
//
//        ServiceUnit su2 = new ServiceUnit();
//        su2.setId("su2");
//        su2.setTargetComponent("comp");
//
//        assertFalse(comparator.compareServiceUnits(su1, su2));
//        assertFalse(comparator.compareServiceUnits(su2, su1));
//    }
//
//    @Test
//    public void compareServiceUnitsWithDifferentComponents() {
//
//        ServiceUnit su1 = new ServiceUnit();
//        su1.setId("su");
//        su1.setTargetComponent("comp1");
//
//        ServiceUnit su2 = new ServiceUnit();
//        su2.setId("su");
//        su2.setTargetComponent("comp2");
//
//        assertFalse(comparator.compareServiceUnits(su1, su2));
//        assertFalse(comparator.compareServiceUnits(su2, su1));
//    }
//
//    @Test
//    public void compareServiceUnits() {
//        ServiceUnit su1 = new ServiceUnit();
//        su1.setId("su");
//        su1.setTargetComponent("comp");
//
//        ServiceUnit su2 = new ServiceUnit();
//        su2.setId("su");
//        su2.setTargetComponent("comp");
//
//        assertTrue(comparator.compareServiceUnits(su1, su2));
//        assertTrue(comparator.compareServiceUnits(su2, su1));
//    }
//
//    @Test
//    public void compareServiceUnitInstancesWithDifferentContainerInstances() {
//        ModelComparator mockedComparator = spy(ModelComparator.class);
//        doReturn(false).when(mockedComparator).compareContainerInstances(null, null);
//        doReturn(true).when(mockedComparator).compareServiceUnits(null, null);
//
//        ServiceUnitInstance sui1 = new ServiceUnitInstance();
//
//        ServiceUnitInstance sui2 = new ServiceUnitInstance();
//
//        assertFalse(mockedComparator.compareServiceUnitInstances(sui1, sui2));
//        assertFalse(mockedComparator.compareServiceUnitInstances(sui2, sui1));
//    }
//
//    @Test
//    public void compareServiceUnitInstancesWithDifferentServiceUnits() {
//        ModelComparator mockedComparator = spy(ModelComparator.class);
//        doReturn(true).when(mockedComparator).compareContainerInstances(null, null);
//        doReturn(false).when(mockedComparator).compareServiceUnits(null, null);
//
//        ServiceUnitInstance sui1 = new ServiceUnitInstance();
//
//        ServiceUnitInstance sui2 = new ServiceUnitInstance();
//
//        assertFalse(mockedComparator.compareServiceUnitInstances(sui1, sui2));
//        assertFalse(mockedComparator.compareServiceUnitInstances(sui2, sui1));
//    }
//
//    @Test
//    public void compareMachinesWithDifferentHostnames() {
//        ProvisionedMachine m1 = new ProvisionedMachine();
//        m1.setId("machine");
//        m1.setHostname("localhost");
//
//        ProvisionedMachine m2 = new ProvisionedMachine();
//        m2.setId("machine");
//        m2.setHostname("1.2.3.4");
//
//        assertFalse(comparator.compareMachines(m1, m2));
//        assertFalse(comparator.compareMachines(m2, m1));
//    }
//
//    @Test
//    public void compareMachinesWithDifferentIds() {
//        ProvisionedMachine m1 = new ProvisionedMachine();
//        m1.setId("machine1");
//        m1.setHostname("localhost");
//
//        ProvisionedMachine m2 = new ProvisionedMachine();
//        m2.setId("machine2");
//        m2.setHostname("localhost");
//
//        assertTrue(comparator.compareMachines(m1, m2));
//        assertTrue(comparator.compareMachines(m2, m1));
//    }
//
//    @Test
//    public void compareMachinesNotProvisioned() {
//        Machine m1 = new Machine() {
//        };
//
//        ProvisionedMachine m2 = new ProvisionedMachine();
//        m2.setId("machine");
//        m2.setHostname("localhost");
//
//        assertFalse(comparator.compareMachines(m1, m2));
//        assertFalse(comparator.compareMachines(m2, m1));
//    }
//
//    @Test
//    public void compareMachines() {
//        ProvisionedMachine m1 = new ProvisionedMachine();
//        m1.setId("machine");
//        m1.setHostname("localhost");
//
//        ProvisionedMachine m2 = new ProvisionedMachine();
//        m2.setId("machine");
//        m2.setHostname("localhost");
//
//        assertTrue(comparator.compareMachines(m1, m2));
//        assertTrue(comparator.compareMachines(m2, m1));
//    }
//
//    @Test
//    public void compareServiceUnitInstances() {
//        ModelComparator mockedComparator = spy(ModelComparator.class);
//        doReturn(true).when(mockedComparator).compareContainerInstances(null, null);
//        doReturn(true).when(mockedComparator).compareServiceUnits(null, null);
//
//        ServiceUnitInstance sui1 = new ServiceUnitInstance();
//
//        ServiceUnitInstance sui2 = new ServiceUnitInstance();
//
//        assertTrue(mockedComparator.compareServiceUnitInstances(sui1, sui2));
//        assertTrue(mockedComparator.compareServiceUnitInstances(sui2, sui1));
//    }
//
//    @Test
//    public void compareContainerInstancesWithDifferentIds() {
//        ModelComparator mockedComparator = spy(ModelComparator.class);
//        doReturn(true).when(mockedComparator).compareMachines(null, null);
//
//        Container c1 = new Container();
//        c1.setId("cont1");
//        c1.setDefaultJmxUser("petals");
//        c1.setDefaultJmxPort(7700);
//        c1.setDefaultJmxPassword("password");
//
//        ContainerInstance ci1 = new ContainerInstance();
//        ci1.setReference(c1.getId());
//
//        Container c2 = new Container();
//        c2.setId("cont2");
//
//        ContainerInstance ci2 = new ContainerInstance();
//        ci2.setReference(c2.getId());
//        ci2.setJmxUser("petals");
//        ci2.setJmxPort(7700);
//        ci2.setJmxPassword("password");
//
//        assertFalse(mockedComparator.compareContainerInstances(ci1, ci2));
//        assertFalse(mockedComparator.compareContainerInstances(ci2, ci1));
//    }
//
//    @Test
//    public void compareContainerInstancesWithDifferentMachines() {
//        ModelComparator mockedComparator = spy(ModelComparator.class);
//        doReturn(false).when(mockedComparator).compareMachines(null, null);
//
//        Container c1 = new Container();
//        c1.setId("cont");
//        c1.setDefaultJmxUser("petals");
//        c1.setDefaultJmxPort(7700);
//        c1.setDefaultJmxPassword("password");
//
//        ContainerInstance ci1 = new ContainerInstance();
//        ci1.setReference(c1.getId());
//
//        Container c2 = new Container();
//        c2.setId("cont");
//
//        ContainerInstance ci2 = new ContainerInstance();
//        ci2.setReference(c2.getId());
//        ci2.setJmxUser("petals");
//        ci2.setJmxPort(7700);
//        ci2.setJmxPassword("password");
//
//        assertFalse(mockedComparator.compareContainerInstances(ci1, ci2));
//        assertFalse(mockedComparator.compareContainerInstances(ci2, ci1));
//    }
//
//    @Test
//    public void compareContainerInstancesWithDifferentJmxUsers() {
//        ModelComparator mockedComparator = spy(ModelComparator.class);
//        doReturn(true).when(mockedComparator).compareMachines(null, null);
//
//        Container c1 = new Container();
//        c1.setId("cont");
//        c1.setDefaultJmxUser("petals");
//        c1.setDefaultJmxPort(7700);
//        c1.setDefaultJmxPassword("password");
//
//        ContainerInstance ci1 = new ContainerInstance();
//        ci1.setReference(c1.getId());
//
//        Container c2 = new Container();
//        c2.setId("cont");
//
//        ContainerInstance ci2 = new ContainerInstance();
//        ci2.setReference(c2.getId());
//        ci2.setJmxUser("user");
//        ci2.setJmxPort(7700);
//        ci2.setJmxPassword("password");
//
//        assertFalse(mockedComparator.compareContainerInstances(ci1, ci2));
//        assertFalse(mockedComparator.compareContainerInstances(ci2, ci1));
//    }
//
//    @Test
//    public void compareContainerInstancesWithDifferentJmxPorts() {
//        ModelComparator mockedComparator = spy(ModelComparator.class);
//        doReturn(true).when(mockedComparator).compareMachines(null, null);
//
//        Container c1 = new Container();
//        c1.setId("cont");
//        c1.setDefaultJmxUser("petals");
//        c1.setDefaultJmxPort(80);
//        c1.setDefaultJmxPassword("password");
//
//        ContainerInstance ci1 = new ContainerInstance();
//        ci1.setReference(c1.getId());
//
//        Container c2 = new Container();
//        c2.setId("cont");
//
//        ContainerInstance ci2 = new ContainerInstance();
//        ci2.setReference(c2.getId());
//        ci2.setJmxUser("petals");
//        ci2.setJmxPort(7700);
//        ci2.setJmxPassword("password");
//
//        assertFalse(mockedComparator.compareContainerInstances(ci1, ci2));
//        assertFalse(mockedComparator.compareContainerInstances(ci2, ci1));
//    }
//
//    @Test
//    public void compareContainerInstancesWithDifferentJmxPasswords() {
//        ModelComparator mockedComparator = spy(ModelComparator.class);
//        doReturn(true).when(mockedComparator).compareMachines(null, null);
//
//        Container c1 = new Container();
//        c1.setId("cont");
//        c1.setDefaultJmxUser("petals");
//        c1.setDefaultJmxPort(7700);
//        c1.setDefaultJmxPassword("password");
//
//        ContainerInstance ci1 = new ContainerInstance();
//        ci1.setReference(c1.getId());
//
//        Container c2 = new Container();
//        c2.setId("cont");
//
//        ContainerInstance ci2 = new ContainerInstance();
//        ci2.setReference(c2.getId());
//        ci2.setJmxUser("petals");
//        ci2.setJmxPort(7700);
//        ci2.setJmxPassword("azerty123");
//
//        assertFalse(mockedComparator.compareContainerInstances(ci1, ci2));
//        assertFalse(mockedComparator.compareContainerInstances(ci2, ci1));
//    }
//
//    @Test
//    public void compareContainerInstances() {
//        ModelComparator mockedComparator = spy(ModelComparator.class);
//        doReturn(true).when(mockedComparator).compareMachines(null, null);
//
//        Container c1 = new Container();
//        c1.setId("cont");
//        c1.setDefaultJmxUser("petals");
//        c1.setDefaultJmxPort(7700);
//        c1.setDefaultJmxPassword("password");
//
//        ContainerInstance ci1 = new ContainerInstance();
//        ci1.setReference(c1.getId());
//
//        Container c2 = new Container();
//        c2.setId("cont");
//
//        ContainerInstance ci2 = new ContainerInstance();
//        ci2.setReference(c2.getId());
//        ci2.setJmxUser("petals");
//        ci2.setJmxPort(7700);
//        ci2.setJmxPassword("password");
//
//        assertTrue(mockedComparator.compareContainerInstances(ci1, ci2));
//        assertTrue(mockedComparator.compareContainerInstances(ci2, ci1));
//    }
//
//    @Test
//    public void compareComponentInstanceListsWithDifferentSizes() {
//        List<ComponentInstance> compInstances1 = new ArrayList<>();
//        compInstances1.add(null);
//
//        List<ComponentInstance> compInstances2 = new ArrayList<>();
//        compInstances2.add(null);
//        compInstances2.add(null);
//
//        assertFalse(comparator.compareComponentInstanceLists(compInstances1, compInstances2));
//        assertFalse(comparator.compareComponentInstanceLists(compInstances2, compInstances1));
//    }
//
//    @Test
//    public void compareComponentInstanceListsNotUnique() {
//        ComponentInstance ci1 = new ComponentInstance();
//        ci1.setReference("comp1");
//        ComponentInstance ci2 = new ComponentInstance();
//        ci2.setReference("comp2");
//
//        ModelComparator mockedComparator = spy(ModelComparator.class);
//        doReturn(false).when(mockedComparator).compareComponentsInstances(ci1, ci2);
//        doReturn(false).when(mockedComparator).compareComponentsInstances(ci2, ci1);
//        doReturn(true).when(mockedComparator).compareComponentsInstances(ci1, ci1);
//        doReturn(true).when(mockedComparator).compareComponentsInstances(ci2, ci2);
//
//        List<ComponentInstance> compInstances1 = new ArrayList<>();
//        compInstances1.add(ci1);
//        compInstances1.add(ci1);
//
//        List<ComponentInstance> compInstances2 = new ArrayList<>();
//        compInstances2.add(ci1);
//        compInstances2.add(ci2);
//
//        assertFalse(mockedComparator.compareComponentInstanceLists(compInstances1, compInstances2));
//        assertFalse(mockedComparator.compareComponentInstanceLists(compInstances2, compInstances1));
//    }
//
//    @Test
//    public void compareComponentInstanceListsWithDifferentItems() {
//        ComponentInstance ci1 = new ComponentInstance();
//        ci1.setReference("comp");
//
//        ModelComparator mockedComparator = spy(ModelComparator.class);
//        doReturn(false).when(mockedComparator).compareComponentsInstances(ci1, ci1);
//
//        List<ComponentInstance> compInstances1 = new ArrayList<>();
//        compInstances1.add(ci1);
//
//        List<ComponentInstance> compInstances2 = new ArrayList<>();
//        compInstances2.add(ci1);
//
//        assertFalse(mockedComparator.compareComponentInstanceLists(compInstances1, compInstances2));
//        assertFalse(mockedComparator.compareComponentInstanceLists(compInstances2, compInstances1));
//    }
//
//    @Test
//    public void compareComponentInstanceLists() {
//        ComponentInstance ci1 = new ComponentInstance();
//        ci1.setReference("comp1");
//        ComponentInstance ci2 = new ComponentInstance();
    // ci1.setId("comp2");
//
//        ModelComparator mockedComparator = spy(ModelComparator.class);
//        doReturn(true).when(mockedComparator).compareComponentsInstances(ci1, ci1);
//        doReturn(true).when(mockedComparator).compareComponentsInstances(ci2, ci2);
//
//        List<ComponentInstance> compInstances1 = new ArrayList<>();
//        compInstances1.add(ci1);
//        compInstances1.add(ci2);
//
//        List<ComponentInstance> compInstances2 = new ArrayList<>();
//        compInstances2.add(ci2);
//        compInstances2.add(ci1);
//
//        assertTrue(mockedComparator.compareComponentInstanceLists(compInstances1, compInstances2));
//        assertTrue(mockedComparator.compareComponentInstanceLists(compInstances2, compInstances1));
//    }
//
//    @Test
//    public void compareServiceUnitInstanceListsWithDifferentSizes() {
//        List<ServiceUnitInstance> suInstances1 = new ArrayList<>();
//        suInstances1.add(null);
//
//        List<ServiceUnitInstance> suInstances2 = new ArrayList<>();
//        suInstances2.add(null);
//        suInstances2.add(null);
//
//        assertFalse(comparator.compareServiceUnitInstanceLists(suInstances1, suInstances2));
//        assertFalse(comparator.compareServiceUnitInstanceLists(suInstances2, suInstances1));
//    }
//
//    @Test
//    public void compareServiceUnitInstanceListsNotUnique() {
//        ServiceUnit su1 = new ServiceUnit();
//        su1.setId("su1");
//        ServiceUnitInstance sui1 = new ServiceUnitInstance();
//        sui1.setReference(su1.getId());
//        ServiceUnit su2 = new ServiceUnit();
//        su2.setId("su2");
//        ServiceUnitInstance sui2 = new ServiceUnitInstance();
//        sui2.setReference(su2.getId());
//
//        ModelComparator mockedComparator = spy(ModelComparator.class);
//        doReturn(false).when(mockedComparator).compareServiceUnitInstances(sui1, sui2);
//        doReturn(false).when(mockedComparator).compareServiceUnitInstances(sui2, sui1);
//        doReturn(true).when(mockedComparator).compareServiceUnitInstances(sui1, sui1);
//        doReturn(true).when(mockedComparator).compareServiceUnitInstances(sui2, sui2);
//
//        List<ServiceUnitInstance> suInstances1 = new ArrayList<>();
//        suInstances1.add(sui1);
//        suInstances1.add(sui1);
//
//        List<ServiceUnitInstance> suInstances2 = new ArrayList<>();
//        suInstances2.add(sui1);
//        suInstances2.add(sui2);
//
//        assertFalse(mockedComparator.compareServiceUnitInstanceLists(suInstances1, suInstances2));
//        assertFalse(mockedComparator.compareServiceUnitInstanceLists(suInstances2, suInstances1));
//    }
//
//    @Test
//    public void compareServiceUnitInstanceListsWithDifferentItems() {
//        ServiceUnit su1 = new ServiceUnit();
//        su1.setId("su");
//        ServiceUnitInstance sui1 = new ServiceUnitInstance();
//        sui1.setReference(su1.getId());
//
//        ModelComparator mockedComparator = spy(ModelComparator.class);
//        doReturn(false).when(mockedComparator).compareServiceUnitInstances(sui1, sui1);
//
//        List<ServiceUnitInstance> suInstances1 = new ArrayList<>();
//        suInstances1.add(sui1);
//
//        List<ServiceUnitInstance> suInstances2 = new ArrayList<>();
//        suInstances2.add(sui1);
//
//        assertFalse(mockedComparator.compareServiceUnitInstanceLists(suInstances1, suInstances2));
//        assertFalse(mockedComparator.compareServiceUnitInstanceLists(suInstances2, suInstances1));
//    }
//
//    @Test
//    public void compareServiceUnitLists() {
//        ServiceUnit su1 = new ServiceUnit();
//        su1.setId("su1");
//        ServiceUnitInstance sui1 = new ServiceUnitInstance();
//        sui1.setReference(su1.getId());
//        ServiceUnit su2 = new ServiceUnit();
//        su2.setId("su2");
//        ServiceUnitInstance sui2 = new ServiceUnitInstance();
//        sui2.setReference(su2.getId());
//
//        ModelComparator mockedComparator = spy(ModelComparator.class);
//        doReturn(true).when(mockedComparator).compareServiceUnitInstances(sui1, sui1);
//        doReturn(true).when(mockedComparator).compareServiceUnitInstances(sui2, sui2);
//
//        List<ServiceUnitInstance> suInstances1 = new ArrayList<>();
//        suInstances1.add(sui1);
//        suInstances1.add(sui2);
//
//        List<ServiceUnitInstance> suInstances2 = new ArrayList<>();
//        suInstances2.add(sui2);
//        suInstances2.add(sui1);
//
//        assertTrue(mockedComparator.compareServiceUnitInstanceLists(suInstances1, suInstances2));
//        assertTrue(mockedComparator.compareServiceUnitInstanceLists(suInstances2, suInstances1));
//    }
//
//    @Test
//    public void compareModelsWithDifferentContainerInstances() {
//        List<ContainerInstance> containerInstances = new ArrayList<ContainerInstance>();
//        containerInstances.add(null);
//        TopologyInstance topoInst = new TopologyInstance();
//        topoInst.setContainerInstances(containerInstances);
//        Bus bus = new Bus();
//        bus.setTopologyInstance(topoInst);
//        List<Bus> buses = new ArrayList<Bus>();
//        buses.add(bus);
//        BusModel busModel = new BusModel();
//        busModel.setBuses(buses);
//        Model model = new Model();
//        model.setBusModel(busModel);
//
//        ModelComparator mockedComparator = spy(ModelComparator.class);
//        doReturn(false).when(mockedComparator).compareContainerInstances(null, null);
//        doReturn(true).when(mockedComparator).compareServiceUnitInstanceLists(null, null);
//        doReturn(true).when(mockedComparator).compareComponentInstanceLists(null, null);
//
//        assertFalse(mockedComparator.compareModels(model, model));
//    }
//
//    @Test
//    public void compareModelsWithDifferentServiceUnitInstanceLists() {
//        List<ContainerInstance> containerInstances = new ArrayList<ContainerInstance>();
//        containerInstances.add(null);
//        TopologyInstance topoInst = new TopologyInstance();
//        topoInst.setContainerInstances(containerInstances);
//        Bus bus = new Bus();
//        bus.setTopologyInstance(topoInst);
//        List<Bus> buses = new ArrayList<Bus>();
//        buses.add(bus);
//        BusModel busModel = new BusModel();
//        busModel.setBuses(buses);
//        Model model = new Model();
//        model.setBusModel(busModel);
//
//        ModelComparator mockedComparator = spy(ModelComparator.class);
//        doReturn(true).when(mockedComparator).compareContainerInstances(null, null);
//        doReturn(false).when(mockedComparator).compareServiceUnitInstanceLists(null, null);
//        doReturn(true).when(mockedComparator).compareComponentInstanceLists(null, null);
//
//        assertFalse(mockedComparator.compareModels(model, model));
//    }
//
//    @Test
//    public void compareModelsWithDifferentComponentInstanceLists() {
//        List<ContainerInstance> containerInstances = new ArrayList<ContainerInstance>();
//        containerInstances.add(null);
//        TopologyInstance topoInst = new TopologyInstance();
//        topoInst.setContainerInstances(containerInstances);
//        Bus bus = new Bus();
//        bus.setTopologyInstance(topoInst);
//        List<Bus> buses = new ArrayList<Bus>();
//        buses.add(bus);
//        BusModel busModel = new BusModel();
//        busModel.setBuses(buses);
//        Model model = new Model();
//        model.setBusModel(busModel);
//
//        ModelComparator mockedComparator = spy(ModelComparator.class);
//        doReturn(true).when(mockedComparator).compareContainerInstances(null, null);
//        doReturn(true).when(mockedComparator).compareServiceUnitInstanceLists(null, null);
//        doReturn(false).when(mockedComparator).compareComponentInstanceLists(null, null);
//
//        assertFalse(mockedComparator.compareModels(model, model));
//    }
}
