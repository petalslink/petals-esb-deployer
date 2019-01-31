/**
 * Copyright (c) 2019 Linagora
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
import java.net.URL;
import java.util.List;

import org.ow2.petals.admin.topology.Container.State;
import org.ow2.petals.deployer.model.bus.xml._1.Bus;
import org.ow2.petals.deployer.model.bus.xml._1.BusModel;
import org.ow2.petals.deployer.model.bus.xml._1.ComponentInstance;
import org.ow2.petals.deployer.model.bus.xml._1.ContainerInstance;
import org.ow2.petals.deployer.model.bus.xml._1.ProvisionedMachine;
import org.ow2.petals.deployer.model.bus.xml._1.ServiceUnitInstance;
import org.ow2.petals.deployer.model.component_repository.xml._1.Component;
import org.ow2.petals.deployer.model.component_repository.xml._1.ComponentRepository;
import org.ow2.petals.deployer.model.service_unit.xml._1.ServiceUnit;
import org.ow2.petals.deployer.model.service_unit.xml._1.ServiceUnitModel;
import org.ow2.petals.deployer.model.topology.xml._1.Container;
import org.ow2.petals.deployer.model.topology.xml._1.Topology;
import org.ow2.petals.deployer.model.topology.xml._1.TopologyModel;
import org.ow2.petals.deployer.model.xml._1.Model;
import org.ow2.petals.deployer.runtimemodel.RuntimeComponent;
import org.ow2.petals.deployer.runtimemodel.RuntimeContainer;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel;
import org.ow2.petals.deployer.runtimemodel.RuntimeServiceUnit;
import org.ow2.petals.deployer.utils.exceptions.UncheckedException;

public class ModelUtils {

    final public static String CONTAINER_NAME = "sample-0";

    final public static String CONTAINER_HOST = "localhost";

    final public static int CONTAINER_JMX_PORT = 7700;

    final public static String CONTAINER_USER = "petals";

    final public static String CONTAINER_PWD = "petals";

    final public static State CONTAINER_STATE = State.REACHABLE;

    public static Model generateTestModel() throws MalformedURLException, IOException, URISyntaxException {
        final Model model = new Model();

        /* Component Repository */

        final ComponentRepository compRepo = new ComponentRepository();
        model.setComponentRepository(compRepo);

        final Component bcSoap = new Component();
        bcSoap.setId("petals-bc-soap");
        bcSoap.setUrl("file:/artifacts/petals-bc-soap-5.0.0");
        compRepo.getComponent().add(bcSoap);

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

    public static RuntimeModel generateTestRuntimeModel() {
        try {
            final RuntimeModel model = new RuntimeModel();

            final RuntimeContainer cont = new RuntimeContainer(CONTAINER_NAME, CONTAINER_JMX_PORT, CONTAINER_USER,
                    CONTAINER_PWD, CONTAINER_HOST);
            model.addContainer(cont);

            cont.addServiceUnit(new RuntimeServiceUnit("su-SOAP-Hello_Service1-provide",
                    new URL("file:/artifacts/sa-SOAP-Hello_Service1-provide")));
            cont.addServiceUnit(new RuntimeServiceUnit("su-SOAP-Hello_Service2-provide",
                    new URL("file:/artifacts/sa-SOAP-Hello_Service2-provide")));
            cont.addServiceUnit(new RuntimeServiceUnit("su-SOAP-Hello_PortType-consume",
                    new URL("file:/artifacts/sa-SOAP-Hello_PortType-consume")));

            cont.addComponent(new RuntimeComponent("petals-bc-soap", new URL("file:/artifacts/petals-bc-soap-5.0.0")));

            return model;
        } catch (final Exception e) {
            throw new UncheckedException(e);
        }
    }
}
