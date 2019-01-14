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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
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
import org.ow2.petals.deployer.model.xml._1.ObjectFactory;

public class ParseModelTest {

    final public static String CONTAINER_NAME = "sample-0";

    final public static String CONTAINER_HOST = "localhost";

    final public static int CONTAINER_JMX_PORT = 7700;

    final public static String CONTAINER_USER = "petals";

    final public static String CONTAINER_PWD = "petals";

    final public static State CONTAINER_STATE = State.REACHABLE;

    @Test
    public void read() throws Exception {
        ObjectFactory of = new ObjectFactory();

        Model model = generateTestModel();

        StringWriter marshalledModelWriter = new StringWriter();
        StringWriter unmarshalledModelWriter = new StringWriter();

        JAXBContext jaxbContext = JAXBContext.newInstance(Model.class);

        Marshaller marshaller = jaxbContext.createMarshaller();
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        marshaller.marshal(of.createModel(model), marshalledModelWriter);
        marshaller.marshal(of.createModel(model), new File("model.xml"));

        Model unmarshalledModel = unmarshaller
                .unmarshal(new StreamSource(new StringReader(marshalledModelWriter.toString())), Model.class)
                .getValue();

        marshaller.marshal(of.createModel(unmarshalledModel), unmarshalledModelWriter);

        assertEquals(marshalledModelWriter.toString(), unmarshalledModelWriter.toString());
    }
    
    public static Model generateTestModel() throws MalformedURLException, IOException, URISyntaxException {
        Model model = new Model();

        /* Component Repository */

        ComponentRepository compRepo = new ComponentRepository();
        model.setComponentRepository(compRepo);

        Component bcSoap = new Component();
        bcSoap.setId("petals-bc-soap");
        bcSoap.setUrl(
                ZipUtils.createZipFromResourceDirectory("artifacts/petals-bc-soap-5.0.0").toURI().toURL().toString());
        compRepo.getComponent().add(bcSoap);

        /* Service Unit Model */

        ServiceUnitModel suModel = new ServiceUnitModel();
        model.setServiceUnitModel(suModel);

        List<ServiceUnit> serviceUnits = suModel.getServiceUnit();

        ServiceUnit suProv1 = new ServiceUnit();
        suProv1.setId("su-SOAP-Hello_Service1-provide");
        suProv1.setUrl(ZipUtils.createZipFromResourceDirectory("artifacts/sa-SOAP-Hello_Service1-provide").toURI()
                .toURL().toString());
        serviceUnits.add(suProv1);

        ServiceUnit suProv2 = new ServiceUnit();
        suProv2.setId("su-SOAP-Hello_Service2-provide");
        suProv2.setUrl(ZipUtils.createZipFromResourceDirectory("artifacts/sa-SOAP-Hello_Service2-provide").toURI()
                .toURL().toString());
        serviceUnits.add(suProv2);

        ServiceUnit suCons = new ServiceUnit();
        suCons.setId("su-SOAP-Hello_PortType-consume");
        suCons.setUrl(ZipUtils.createZipFromResourceDirectory("artifacts/sa-SOAP-Hello_PortType-consume").toURI()
                .toURL().toString());
        serviceUnits.add(suCons);

        /* Topology Model */

        TopologyModel topoModel = new TopologyModel();
        model.setTopologyModel(topoModel);

        Topology topo = new Topology();
        topo.setId("topo1");
        topoModel.getTopology().add(topo);

        Container cont = new Container();
        cont.setId(CONTAINER_NAME);
        cont.setDefaultJmxPort(CONTAINER_JMX_PORT);
        cont.setDefaultJmxUser(CONTAINER_USER);
        cont.setDefaultJmxPassword(CONTAINER_PWD);
        topo.getContainer().add(cont);

        /* Bus Model */

        BusModel busModel = new BusModel();
        model.setBusModel(busModel);

        ProvisionedMachine machine = new ProvisionedMachine();
        machine.setId("main");
        machine.setHostname("localhost");
        busModel.getMachine().add(machine);

        Bus bus = new Bus();
        bus.setTopologyRef(topo.getId());
        busModel.getBus().add(bus);

        ContainerInstance contInst = new ContainerInstance();
        contInst.setRef(cont.getId());
        contInst.setMachineRef(machine.getId());
        bus.getContainerInstance().add(contInst);

        ComponentInstance bcSoapInst = new ComponentInstance();
        bcSoapInst.setRef(bcSoap.getId());
        contInst.getComponentInstance().add(bcSoapInst);

        List<ServiceUnitInstance> suInstances = contInst.getServiceUnitInstance();

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
}
