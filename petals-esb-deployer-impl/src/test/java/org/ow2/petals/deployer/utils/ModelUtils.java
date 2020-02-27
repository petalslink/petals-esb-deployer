/**
 * Copyright (c) 2019-2020 Linagora
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import org.ow2.petals.deployer.model.bus.xml._1.Bus;
import org.ow2.petals.deployer.model.bus.xml._1.BusModel;
import org.ow2.petals.deployer.model.bus.xml._1.ComponentInstance;
import org.ow2.petals.deployer.model.bus.xml._1.ContainerInstance;
import org.ow2.petals.deployer.model.bus.xml._1.ParameterInstance;
import org.ow2.petals.deployer.model.bus.xml._1.ProvisionedMachine;
import org.ow2.petals.deployer.model.bus.xml._1.ServiceUnitInstance;
import org.ow2.petals.deployer.model.component_repository.xml._1.Component;
import org.ow2.petals.deployer.model.component_repository.xml._1.ComponentRepository;
import org.ow2.petals.deployer.model.component_repository.xml._1.Parameter;
import org.ow2.petals.deployer.model.component_repository.xml._1.SharedLibrary;
import org.ow2.petals.deployer.model.component_repository.xml._1.SharedLibraryReference;
import org.ow2.petals.deployer.model.service_unit.xml._1.ServiceUnit;
import org.ow2.petals.deployer.model.service_unit.xml._1.ServiceUnitModel;
import org.ow2.petals.deployer.model.topology.xml._1.Container;
import org.ow2.petals.deployer.model.topology.xml._1.Topology;
import org.ow2.petals.deployer.model.topology.xml._1.TopologyModel;
import org.ow2.petals.deployer.model.xml._1.Model;

public class ModelUtils {

    final public static String CONTAINER_NAME = "sample-0";

    final public static String CONTAINER_HOST = "localhost";

    final public static int CONTAINER_JMX_PORT = 7700;

    final public static String CONTAINER_USER = "petals";

    final public static String CONTAINER_PWD = "petals";

    public static Model generateTestModel() throws MalformedURLException, IOException, URISyntaxException {
        final Model model = new Model();

        /* Component Repository */

        final ComponentRepository compRepo = new ComponentRepository();
        model.setComponentRepository(compRepo);

        final Component bcSoap = new Component();
        bcSoap.setId("petals-bc-soap");
        bcSoap.setUrl("file:/artifacts/petals-bc-soap-5.0.0");
        compRepo.getComponentOrSharedLibrary().add(bcSoap);

        /* Service Unit Model */

        final ServiceUnitModel suModel = new ServiceUnitModel();
        model.setServiceUnitModel(suModel);

        final List<ServiceUnit> serviceUnits = suModel.getServiceUnit();

        final ServiceUnit suProv1 = new ServiceUnit();
        suProv1.setId("su-SOAP-Hello_Service1-provide");
        suProv1.setUrl("file:/artifacts/sa-SOAP-Hello_Service1-provide");
        serviceUnits.add(suProv1);

        final ServiceUnit suProv2 = new ServiceUnit();
        suProv2.setId("su-SOAP-Hello_Service2-provide");
        suProv2.setUrl("file:/artifacts/sa-SOAP-Hello_Service2-provide");
        serviceUnits.add(suProv2);

        final ServiceUnit suCons = new ServiceUnit();
        suCons.setId("su-SOAP-Hello_PortType-consume");
        suCons.setUrl("file:/artifacts/sa-SOAP-Hello_PortType-consume");
        serviceUnits.add(suCons);

        /* Topology Model */

        final TopologyModel topoModel = new TopologyModel();
        model.setTopologyModel(topoModel);

        final Topology topo = new Topology();
        topo.setId("topo1");
        topoModel.getTopology().add(topo);

        final Container cont = new Container();
        cont.setId(CONTAINER_NAME);
        cont.setDefaultJmxPort(CONTAINER_JMX_PORT);
        cont.setDefaultJmxUser(CONTAINER_USER);
        cont.setDefaultJmxPassword(CONTAINER_PWD);
        topo.getContainer().add(cont);

        /* Bus Model */

        final BusModel busModel = new BusModel();
        model.setBusModel(busModel);

        final ProvisionedMachine machine = new ProvisionedMachine();
        machine.setId("main");
        machine.setHostname("localhost");
        busModel.getMachine().add(machine);

        final Bus bus = new Bus();
        bus.setTopologyRef(topo.getId());
        busModel.getBus().add(bus);

        final ContainerInstance contInst = new ContainerInstance();
        contInst.setRef(cont.getId());
        contInst.setMachineRef(machine.getId());
        bus.getContainerInstance().add(contInst);

        final ComponentInstance bcSoapInst = new ComponentInstance();
        bcSoapInst.setRef(bcSoap.getId());
        contInst.getComponentInstance().add(bcSoapInst);

        final List<ServiceUnitInstance> suInstances = contInst.getServiceUnitInstance();

        ServiceUnitInstance suInst = new ServiceUnitInstance();
        suInst.setRef(suProv1.getId());
        suInstances.add(suInst);

        suInst = new ServiceUnitInstance();
        suInst.setRef(suProv2.getId());
        suInstances.add(suInst);

        suInst = new ServiceUnitInstance();
        suInst.setRef(suCons.getId());
        suInstances.add(suInst);

        return model;
    }

    public static Model generateTestModelWithMavenUrl() throws MalformedURLException, IOException, URISyntaxException {
        final Model model = new Model();

        /* Component Repository */

        final ComponentRepository compRepo = new ComponentRepository();
        model.setComponentRepository(compRepo);

        final Component dummyMavenComp = new Component();
        dummyMavenComp.setId("dummy-maven-comp");
        dummyMavenComp.setUrl("mvn:dummy-maven-comp");
        compRepo.getComponentOrSharedLibrary().add(dummyMavenComp);

        /* Service Unit Model */

        final ServiceUnitModel suModel = new ServiceUnitModel();
        model.setServiceUnitModel(suModel);

        final List<ServiceUnit> serviceUnits = suModel.getServiceUnit();

        final ServiceUnit dummyMavenSu = new ServiceUnit();
        dummyMavenSu.setId("dummy-maven-su");
        dummyMavenSu.setUrl("mvn:dummy-maven-su");
        serviceUnits.add(dummyMavenSu);

        /* Topology Model */

        final TopologyModel topoModel = new TopologyModel();
        model.setTopologyModel(topoModel);

        final Topology topo = new Topology();
        topo.setId("topo1");
        topoModel.getTopology().add(topo);

        final Container cont = new Container();
        cont.setId(CONTAINER_NAME);
        cont.setDefaultJmxPort(CONTAINER_JMX_PORT);
        cont.setDefaultJmxUser(CONTAINER_USER);
        cont.setDefaultJmxPassword(CONTAINER_PWD);
        topo.getContainer().add(cont);

        /* Bus Model */

        final BusModel busModel = new BusModel();
        model.setBusModel(busModel);

        final ProvisionedMachine machine = new ProvisionedMachine();
        machine.setId("main");
        machine.setHostname("localhost");
        busModel.getMachine().add(machine);

        final Bus bus = new Bus();
        bus.setTopologyRef(topo.getId());
        busModel.getBus().add(bus);

        final ContainerInstance contInst = new ContainerInstance();
        contInst.setRef(cont.getId());
        contInst.setMachineRef(machine.getId());
        bus.getContainerInstance().add(contInst);

        final ComponentInstance dummyMavenCompInst = new ComponentInstance();
        dummyMavenCompInst.setRef(dummyMavenComp.getId());
        contInst.getComponentInstance().add(dummyMavenCompInst);

        final List<ServiceUnitInstance> suInstances = contInst.getServiceUnitInstance();

        ServiceUnitInstance dummyMavenSuInst = new ServiceUnitInstance();
        dummyMavenSuInst.setRef(dummyMavenSu.getId());
        suInstances.add(dummyMavenSuInst);

        return model;
    }

    public static Model generateTestModelWithSharedLibrary()
            throws MalformedURLException, IOException, URISyntaxException {
        final Model model = generateTestModel();

        SharedLibrary sl = new SharedLibrary();
        sl.setId("id-shared-library");
        sl.setVersion("1.0");
        sl.setUrl("file:dummy-sl-file");
        model.getComponentRepository().getComponentOrSharedLibrary().add(sl);

        Component comp = new Component();
        comp.setId("id-comp-with-shared-library");
        comp.setUrl("file:dummy-comp-with-sl-file");
        SharedLibraryReference slRef = new SharedLibraryReference();
        slRef.setRefId(sl.getId());
        slRef.setRefVersion(sl.getVersion());
        comp.getSharedLibraryReference().add(slRef);
        model.getComponentRepository().getComponentOrSharedLibrary().add(comp);

        ComponentInstance compInst = new ComponentInstance();
        compInst.setRef(comp.getId());
        model.getBusModel().getBus().get(0).getContainerInstance().get(0).getComponentInstance().add(compInst);

        return model;
    }

    public static Model generateTestModelWithParameter()
            throws MalformedURLException, IOException, URISyntaxException {
        final Model model = generateTestModel();

        Component comp = new Component();
        comp.setId("id-comp-with-parameter");
        comp.setUrl("file:dummy-comp-with-parameter");
        Parameter param = new Parameter();
        param.setName("param-with-default-value");
        param.setValue("default-value");
        comp.getParameter().add(param);
        model.getComponentRepository().getComponentOrSharedLibrary().add(comp);

        ComponentInstance compInst = new ComponentInstance();
        compInst.setRef(comp.getId());
        ParameterInstance paramInst = new ParameterInstance();
        paramInst.setRef(param.getName());
        paramInst.setValue("overridden-value");
        compInst.getParameterInstance().add(paramInst);
        model.getBusModel().getBus().get(0).getContainerInstance().get(0).getComponentInstance().add(compInst);

        return model;
    }
}
