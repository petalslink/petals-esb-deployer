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

package org.ow2.petals.deployer.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import org.ow2.petals.deployer.model.bus.xml._1.Bus;
import org.ow2.petals.deployer.model.bus.xml._1.BusModel;
import org.ow2.petals.deployer.model.bus.xml._1.ComponentInstance;
import org.ow2.petals.deployer.model.bus.xml._1.ContainerInstance;
import org.ow2.petals.deployer.model.bus.xml._1.ParameterInstance;
import org.ow2.petals.deployer.model.bus.xml._1.PlaceholderInstance;
import org.ow2.petals.deployer.model.bus.xml._1.ProvisionedMachine;
import org.ow2.petals.deployer.model.bus.xml._1.ServiceUnitInstance;
import org.ow2.petals.deployer.model.component_repository.xml._1.Component;
import org.ow2.petals.deployer.model.component_repository.xml._1.ComponentRepository;
import org.ow2.petals.deployer.model.component_repository.xml._1.Parameter;
import org.ow2.petals.deployer.model.component_repository.xml._1.SharedLibrary;
import org.ow2.petals.deployer.model.component_repository.xml._1.SharedLibraryReference;
import org.ow2.petals.deployer.model.service_unit.xml._1.Placeholder;
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
        bcSoap.setUrl(ModelUtils.class.getResource("/artifacts/petals-bc-soap-5.0.0.zip").toURI().toURL().toString());
        compRepo.getComponentOrSharedLibrary().add(bcSoap);

        /* Service Unit Model */

        final ServiceUnitModel suModel = new ServiceUnitModel();
        model.setServiceUnitModel(suModel);

        final List<ServiceUnit> serviceUnits = suModel.getServiceUnit();

        final ServiceUnit suProv1 = new ServiceUnit();
        suProv1.setId("su-SOAP-Hello_Service1-provide");
        suProv1.setUrl(ModelUtils.class.getResource("/artifacts/sa-SOAP-Hello_Service1-provide.zip").toURI().toURL()
                .toString());
        serviceUnits.add(suProv1);

        final ServiceUnit suProv2 = new ServiceUnit();
        suProv2.setId("su-SOAP-Hello_Service2-provide");
        suProv2.setUrl(ModelUtils.class.getResource("/artifacts/sa-SOAP-Hello_Service2-provide.zip").toURI().toURL()
                .toString());
        serviceUnits.add(suProv2);

        final ServiceUnit suCons = new ServiceUnit();
        suCons.setId("su-SOAP-Hello_PortType-consume");
        suCons.setUrl(ModelUtils.class.getResource("/artifacts/sa-SOAP-Hello_PortType-consume.zip").toURI().toURL()
                .toString());
        serviceUnits.add(suCons);

        /* Topology Model */
        final String topoId = "topo1";
        final String contId = CONTAINER_NAME;
        model.setTopologyModel(generateTestTopologyModel(topoId, contId));

        /* Bus Model */

        final BusModel busModel = new BusModel();
        model.setBusModel(busModel);

        final String machineId = "main";
        busModel.getMachine().add(generateTestProvisionedMachine(machineId));

        final Bus bus = new Bus();
        bus.setTopologyRef(topoId);
        busModel.getBus().add(bus);

        final ContainerInstance contInst = new ContainerInstance();
        contInst.setRef(contId);
        contInst.setMachineRef(machineId);
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

    public static TopologyModel generateTestTopologyModel(final String topoId, final String contId) {
        final TopologyModel topoModel = new TopologyModel();

        final Topology topo = new Topology();
        topo.setId(topoId);
        topoModel.getTopology().add(topo);

        final Container cont = new Container();
        cont.setId(contId);
        cont.setDefaultJmxPort(CONTAINER_JMX_PORT);
        cont.setDefaultJmxUser(CONTAINER_USER);
        cont.setDefaultJmxPassword(CONTAINER_PWD);
        topo.getContainer().add(cont);

        return topoModel;
    }

    public static ProvisionedMachine generateTestProvisionedMachine(final String machineId) {
        final ProvisionedMachine machine = new ProvisionedMachine();
        machine.setId(machineId);
        machine.setHostname("localhost");
        return machine;
    }

    public static Model generateTestModelWithMavenUrl() throws MalformedURLException, IOException, URISyntaxException {
        final Model model = new Model();

        /* Component Repository */

        final ComponentRepository compRepo = new ComponentRepository();
        model.setComponentRepository(compRepo);

        final Component mavenComp = new Component();
        mavenComp.setId("petals-bc-rest");
        mavenComp.setUrl("mvn:org.ow2.petals/petals-bc-rest/2.0.0/zip");
        compRepo.getComponentOrSharedLibrary().add(mavenComp);

        /* Service Unit Model */

        final ServiceUnitModel suModel = new ServiceUnitModel();
        model.setServiceUnitModel(suModel);

        final List<ServiceUnit> serviceUnits = suModel.getServiceUnit();

        final ServiceUnit mavenSu = new ServiceUnit();
        mavenSu.setId("su-rest-edm-provide-1.3.0-1.0.0");
        mavenSu.setUrl("mvn:org.ow2.petals.samples.rest.edm/sa-proxy-rest-edm/1.3.0-1.0.0/zip");
        serviceUnits.add(mavenSu);

        /* Topology Model */
        final String topoId = "topo1";
        final String contId = CONTAINER_NAME;
        model.setTopologyModel(generateTestTopologyModel(topoId, contId));

        /* Bus Model */

        final BusModel busModel = new BusModel();
        model.setBusModel(busModel);

        final String machineId = "main";
        busModel.getMachine().add(generateTestProvisionedMachine(machineId));

        final Bus bus = new Bus();
        bus.setTopologyRef(topoId);
        busModel.getBus().add(bus);

        final ContainerInstance contInst = new ContainerInstance();
        contInst.setRef(contId);
        contInst.setMachineRef(machineId);
        bus.getContainerInstance().add(contInst);

        final ComponentInstance mavenCompInst = new ComponentInstance();
        mavenCompInst.setRef(mavenComp.getId());
        contInst.getComponentInstance().add(mavenCompInst);

        final List<ServiceUnitInstance> suInstances = contInst.getServiceUnitInstance();

        final ServiceUnitInstance mavenSuInst = new ServiceUnitInstance();
        mavenSuInst.setRef(mavenSu.getId());
        suInstances.add(mavenSuInst);

        return model;
    }

    public static Model generateTestModelWithSharedLibrary()
            throws MalformedURLException, IOException, URISyntaxException {
        final Model model = generateTestModel();

        final SharedLibrary sl = new SharedLibrary();
        sl.setId("petals-sl-hsql-1.8.0.10");
        sl.setVersion("1.0");
        sl.setUrl(ModelUtils.class.getResource("/artifacts/petals-sl-hsql-1.8.0.10.zip").toURI().toURL().toString());
        model.getComponentRepository().getComponentOrSharedLibrary().add(sl);

        final Component comp = new Component();
        comp.setId("petals-bc-sql-with-shared-libraries");
        comp.setUrl(ModelUtils.class.getResource("/artifacts/petals-bc-sql-with-shared-libraries.zip").toURI().toURL()
                .toString());
        final SharedLibraryReference slRef = new SharedLibraryReference();
        slRef.setRefId(sl.getId());
        slRef.setRefVersion(sl.getVersion());
        comp.getSharedLibraryReference().add(slRef);
        model.getComponentRepository().getComponentOrSharedLibrary().add(comp);

        ComponentInstance compInst = new ComponentInstance();
        compInst.setRef(comp.getId());
        model.getBusModel().getBus().get(0).getContainerInstance().get(0).getComponentInstance().add(compInst);

        return model;
    }

    public static Model generateTestModelWithParameter() throws MalformedURLException, IOException, URISyntaxException {
        final Model model = generateTestModel();

        final Object compObj = model.getComponentRepository().getComponentOrSharedLibrary().get(0);
        assertNotNull(compObj);
        assertTrue(compObj instanceof Component);
        final Component comp = (Component) compObj;
        final Parameter param = new Parameter();
        param.setName("param-with-default-value");
        param.setValue("default-value");
        comp.getParameter().add(param);

        final ComponentInstance compInst = model.getBusModel().getBus().get(0).getContainerInstance().get(0)
                .getComponentInstance().get(0);
        assertNotNull(compInst);
        ParameterInstance paramInst = new ParameterInstance();
        paramInst.setRef(param.getName());
        paramInst.setValue("overridden-value");
        compInst.getParameterInstance().add(paramInst);

        return model;
    }

    public static Model generateTestModelWithPlaceholder()
            throws MalformedURLException, IOException, URISyntaxException {
        final Model model = generateTestModel();

        final ServiceUnit su = model.getServiceUnitModel().getServiceUnit().get(0);
        assertEquals("su-SOAP-Hello_Service1-provide", su.getId());
        final Placeholder placeholder = new Placeholder();
        placeholder.setName("placeholder-with-default-value");
        placeholder.setValue("default-value");
        su.getPlaceholder().add(placeholder);

        final ServiceUnitInstance suInst = model.getBusModel().getBus().get(0).getContainerInstance().get(0)
                .getServiceUnitInstance().get(0);
        assertEquals("su-SOAP-Hello_Service1-provide", suInst.getRef());
        final PlaceholderInstance placeholderInst = new PlaceholderInstance();
        placeholderInst.setRef(placeholder.getName());
        placeholderInst.setValue("overridden-value");
        suInst.getPlaceholderInstance().add(placeholderInst);

        return model;
    }

    public static Model generateTestModelWithMissingPlaceholder()
            throws MalformedURLException, IOException, URISyntaxException {
        final Model model = generateTestModel();

        final ServiceUnit su = new ServiceUnit();
        su.setId("id-su-with-placeholder");
        su.setUrl("file:dummy-su-with-placeholder");
        model.getServiceUnitModel().getServiceUnit().add(su);

        final ServiceUnitInstance suInst = new ServiceUnitInstance();
        suInst.setRef(su.getId());
        final PlaceholderInstance placeholderInst = new PlaceholderInstance();
        placeholderInst.setRef("unexisting-placeholder");
        placeholderInst.setValue("overridden-value");
        suInst.getPlaceholderInstance().add(placeholderInst);
        model.getBusModel().getBus().get(0).getContainerInstance().get(0).getServiceUnitInstance().add(suInst);

        return model;
    }
}
