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

package org.ow2.petals.deployer.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.ow2.petals.deployer.model.bus.xml._1.Bus;
import org.ow2.petals.deployer.model.bus.xml._1.BusModel;
import org.ow2.petals.deployer.model.bus.xml._1.ComponentInstance;
import org.ow2.petals.deployer.model.bus.xml._1.ContainerInstance;
import org.ow2.petals.deployer.model.bus.xml._1.PlaceholderInstance;
import org.ow2.petals.deployer.model.bus.xml._1.ServiceUnitInstance;
import org.ow2.petals.deployer.model.component_repository.xml._1.Component;
import org.ow2.petals.deployer.model.component_repository.xml._1.ComponentRepository;
import org.ow2.petals.deployer.model.component_repository.xml._1.SharedLibrary;
import org.ow2.petals.deployer.model.service_unit.xml._1.Placeholder;
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
import org.ow2.petals.deployer.runtimemodel.RuntimeSharedLibrary;
import org.ow2.petals.deployer.utils.exceptions.ModelValidationException;

/**
 * @author Alexandre Lagane - Linagora
 * @author Christophe DENEUX - Linagora
 */
public class ModelConverterTest {

    @Test
    public void convertModelToRuntimeModel() throws Exception {
        final Model model = ModelUtils.generateTestModel();

        final RuntimeModel runtimeModel = ModelConverter.convertModelToRuntimeModel(model);

        final Collection<RuntimeContainer> containers = runtimeModel.getContainers();
        assertEquals(1, containers.size());

        final RuntimeContainer cont = containers.iterator().next();
        assertEquals(ModelUtils.CONTAINER_NAME, cont.getId());
        assertEquals(ModelUtils.CONTAINER_HOST, cont.getHostname());
        assertEquals(ModelUtils.CONTAINER_JMX_PORT, cont.getPort());
        assertEquals(ModelUtils.CONTAINER_USER, cont.getUser());
        assertEquals(ModelUtils.CONTAINER_PWD, cont.getPassword());

        final Collection<RuntimeServiceUnit> serviceUnits = cont.getServiceUnits();
        assertEquals(3, serviceUnits.size());

        RuntimeServiceUnit su = cont.getServiceUnit("su-SOAP-Hello_Service1-provide");
        assertEquals("su-SOAP-Hello_Service1-provide", su.getId());
        assertEquals(ModelConverterTest.class.getResource("/artifacts/sa-SOAP-Hello_Service1-provide.zip").toString(),
                su.getUrl().toString());

        su = cont.getServiceUnit("su-SOAP-Hello_Service2-provide");
        assertEquals("su-SOAP-Hello_Service2-provide", su.getId());
        assertEquals(ModelConverterTest.class.getResource("/artifacts/sa-SOAP-Hello_Service2-provide.zip").toString(),
                su.getUrl().toString());

        su = cont.getServiceUnit("su-SOAP-Hello_PortType-consume");
        assertEquals("su-SOAP-Hello_PortType-consume", su.getId());
        assertEquals(ModelConverterTest.class.getResource("/artifacts/sa-SOAP-Hello_PortType-consume.zip").toString(),
                su.getUrl().toString());

        final Collection<RuntimeComponent> components = cont.getComponents();
        assertEquals(1, components.size());

        final RuntimeComponent comp = cont.getComponent("petals-bc-soap");
        assertEquals("petals-bc-soap", comp.getId());
        assertEquals(ModelConverterTest.class.getResource("/artifacts/petals-bc-soap-5.0.0.zip").toString(),
                comp.getUrl().toString());
    }

    @Test
    public void convertModelWithSharedLibraryToRuntimeModel() throws Exception {
        final Model model = ModelUtils.generateTestModelWithSharedLibrary();

        final RuntimeModel runtimeModel = ModelConverter.convertModelToRuntimeModel(model);

        final Collection<RuntimeContainer> containers = runtimeModel.getContainers();
        assertEquals(1, containers.size());

        final RuntimeContainer cont = containers.iterator().next();

        final Collection<RuntimeComponent> components = cont.getComponents();
        assertEquals(2, components.size());

        final RuntimeComponent comp = cont.getComponent("petals-bc-sql-with-shared-libraries");
        assertEquals("petals-bc-sql-with-shared-libraries", comp.getId());
        assertEquals(ModelUtils.class.getResource("/artifacts/petals-bc-sql-with-shared-libraries.zip").toURI().toURL()
                .toString(), comp.getUrl().toString());

        assertEquals(1, comp.getSharedLibraries().size());

        final RuntimeSharedLibrary slFromCont = cont.getSharedLibrary("petals-sl-hsql-1.8.0.10", "1.0");
        assertEquals("petals-sl-hsql-1.8.0.10", slFromCont.getId());
        assertEquals("1.0", slFromCont.getVersion());
        assertEquals(ModelUtils.class.getResource("/artifacts/petals-sl-hsql-1.8.0.10.zip").toURI().toURL().toString(),
                slFromCont.getUrl().toString());
    }

    @Test
    public void convertModelWithParametersToRuntimeModel() throws Exception {
        final Model model = ModelUtils.generateTestModelWithParameter();

        final RuntimeModel runtimeModel = ModelConverter.convertModelToRuntimeModel(model);

        final Collection<RuntimeContainer> containers = runtimeModel.getContainers();
        assertEquals(1, containers.size());

        final RuntimeContainer cont = containers.iterator().next();

        final Collection<RuntimeComponent> components = cont.getComponents();
        assertEquals(1, components.size());

        final RuntimeComponent comp = cont.getComponent("petals-bc-soap");
        assertEquals("petals-bc-soap", comp.getId());
        assertEquals(
                ModelConverterTest.class.getResource("/artifacts/petals-bc-soap-5.0.0.zip").toURI().toURL().toString(),
                comp.getUrl().toString());

        Map<String, String> parameters = comp.getParameters();
        assertEquals(1, parameters.size());
        Entry<String, String> param = parameters.entrySet().iterator().next();
        assertEquals("param-with-default-value", param.getKey());
        assertEquals("overridden-value", param.getValue());

    }

    @Test
    public void convertModelWithPlaceholdersToRuntimeModel() throws Exception {
        final Model model = ModelUtils.generateTestModelWithPlaceholder();

        final RuntimeModel runtimeModel = ModelConverter.convertModelToRuntimeModel(model);

        final Collection<RuntimeContainer> containers = runtimeModel.getContainers();
        assertEquals(1, containers.size());

        final RuntimeContainer cont = containers.iterator().next();

        final Collection<RuntimeServiceUnit> serviceunits = cont.getServiceUnits();
        assertEquals(3, serviceunits.size());

        final RuntimeServiceUnit su = cont.getServiceUnit("su-SOAP-Hello_Service1-provide");
        assertEquals("su-SOAP-Hello_Service1-provide", su.getId());

        final Map<String, String> placeholders = su.getPlaceholders();
        assertEquals(1, placeholders.size());
        final Entry<String, String> placeholder = placeholders.entrySet().iterator().next();
        assertEquals("placeholder-with-default-value", placeholder.getKey());
        assertEquals("overridden-value", placeholder.getValue());
    }

    /**
     * <p>
     * Try to convert a model containing:
     * </p>
     * <ul>
     * <li>a service unit instance defines a placeholder instance referring an unexisting placeholder definition.</li>
     * </ul>
     * <p>
     * Expected results: an error is thrown because of unexisting placeholder definition.
     * </p>
     */
    @Test
    public void error_missingPlacholder() throws Exception {
        final Model model = ModelUtils.generateTestModelWithMissingPlaceholder();

        final Exception exception = assertThrows(ModelValidationException.class, () -> {
            ModelConverter.convertModelToRuntimeModel(model);
        });

        assertEquals("Placeholder 'unexisting-placeholder' is not defined in service unit 'id-su-with-placeholder'",
                exception.getMessage());
    }

    /**
     * <p>
     * Try to convert a model containing:
     * </p>
     * <ul>
     * <li>a service unit definition defines several placeholder with the same identifier.</li>
     * </ul>
     * <p>
     * Expected results: an error is thrown because of placeholder duplication.
     * </p>
     */
    @Test
    public void error_duplicatedPlacholder() throws Exception {
        final Model model = ModelUtils.generateTestModelWithPlaceholder();

        final ServiceUnit su = model.getServiceUnitModel().getServiceUnit().get(0);
        assertEquals("su-SOAP-Hello_Service1-provide", su.getId());
        final Placeholder placeholder = new Placeholder();
        placeholder.setName("placeholder-with-default-value");
        placeholder.setValue("my-value");
        su.getPlaceholder().add(placeholder);

        final Exception exception = assertThrows(ModelValidationException.class, () -> {
            ModelConverter.convertModelToRuntimeModel(model);
        });

        assertEquals(String.format("Placeholder '%s' is duplicated in service unit definition '%s'",
                placeholder.getName(), su.getId()), exception.getMessage());
    }

    /**
     * <p>
     * Try to convert a model containing:
     * </p>
     * <ul>
     * <li>a component,</li>
     * <li>several service unit running on the given component,</li>
     * <li>service units defines the same placeholder with different values.</li>
     * </ul>
     * <p>
     * Expected results: an error is thrown because the placeholder has different value for the same component.
     * </p>
     */
    @Test
    public void error_placeholderWithDifferentValue() throws Exception {
        final Model model = ModelUtils.generateTestModelWithPlaceholder();

        final ServiceUnit su = model.getServiceUnitModel().getServiceUnit().get(1);
        assertEquals("su-SOAP-Hello_Service2-provide", su.getId());
        final Placeholder placeholder = new Placeholder();
        placeholder.setName("placeholder-with-default-value");
        placeholder.setValue("default-value");
        su.getPlaceholder().add(placeholder);

        final ServiceUnitInstance suInst = model.getBusModel().getBus().get(0).getContainerInstance().get(0)
                .getServiceUnitInstance().get(1);
        assertEquals("su-SOAP-Hello_Service2-provide", suInst.getRef());
        final PlaceholderInstance placeholderInst = new PlaceholderInstance();
        placeholderInst.setRef(placeholder.getName());
        placeholderInst.setValue("another value");
        suInst.getPlaceholderInstance().add(placeholderInst);

        final Exception exception = assertThrows(ModelValidationException.class, () -> {
            ModelConverter.convertModelToRuntimeModel(model);
        });

        assertEquals(String.format(
                "The placeholder '%s' has several different values for the component '%s' on container '%s'",
                placeholder.getName(), "petals-bc-soap", "sample-0"), exception.getMessage());
    }

    /**
     * <p>
     * Try to convert a model containing:
     * </p>
     * <ul>
     * <li>a component,</li>
     * <li>several service unit running on the given component,</li>
     * <li>service units defines the same placeholder with the same values.</li>
     * </ul>
     * <p>
     * Expected results: No error.
     * </p>
     */
    @Test
    public void error_placeholderWithSameValue() throws Exception {
        final Model model = ModelUtils.generateTestModelWithPlaceholder();

        final ServiceUnit su = model.getServiceUnitModel().getServiceUnit().get(1);
        assertEquals("su-SOAP-Hello_Service2-provide", su.getId());
        final Placeholder placeholder = new Placeholder();
        placeholder.setName("placeholder-with-default-value");
        placeholder.setValue("default-value");
        su.getPlaceholder().add(placeholder);

        final ServiceUnitInstance suInst = model.getBusModel().getBus().get(0).getContainerInstance().get(0)
                .getServiceUnitInstance().get(1);
        assertEquals("su-SOAP-Hello_Service2-provide", suInst.getRef());
        final PlaceholderInstance placeholderInst = new PlaceholderInstance();
        placeholderInst.setRef(placeholder.getName());
        placeholderInst.setValue("overridden-value");
        suInst.getPlaceholderInstance().add(placeholderInst);

        ModelConverter.convertModelToRuntimeModel(model);

    }

    /**
     * <p>
     * Try to convert a model containing:
     * </p>
     * <ul>
     * <li>a service unit instance defines several placeholder instances referring a same placeholder definition.</li>
     * </ul>
     * <p>
     * Expected results: an error is thrown because of placeholder duplication.
     * </p>
     */
    @Test
    public void error_duplicatedPlacholderInstance() throws Exception {
        final Model model = ModelUtils.generateTestModelWithPlaceholder();

        final ServiceUnitInstance suInst = model.getBusModel().getBus().get(0).getContainerInstance().get(0)
                .getServiceUnitInstance().get(0);
        assertEquals("su-SOAP-Hello_Service1-provide", suInst.getRef());
        final PlaceholderInstance placeholderInst = new PlaceholderInstance();
        placeholderInst.setRef("placeholder-with-default-value");
        placeholderInst.setValue("my-value");
        suInst.getPlaceholderInstance().add(placeholderInst);

        final Exception exception = assertThrows(ModelValidationException.class, () -> {
            ModelConverter.convertModelToRuntimeModel(model);
        });

        assertEquals(String.format(
                "Placeholder '%s' is duplicated in service unit instance referring to service unit definition '%s'",
                placeholderInst.getRef(), suInst.getRef()), exception.getMessage());
    }

    /**
     * <p>
     * Try to convert a model where the component repository part is missing.
     * </p>
     * <p>
     * Expected results: an error is thrown because of the missing component repository.
     * </p>
     */
    @Test
    public void error_componentRepositoryMissing() throws Exception {
        final Model model = new Model();
        model.setServiceUnitModel(new ServiceUnitModel());
        // component repository missing
        model.setTopologyModel(new TopologyModel());
        model.setBusModel(new BusModel());

        final Exception exception = assertThrows(ModelValidationException.class, () -> {
            ModelConverter.convertModelToRuntimeModel(model);
        });

        assertEquals("The component repository definition is missing in the model.", exception.getMessage());
    }

    /**
     * <p>
     * Try to convert a model where the topology model part is missing.
     * </p>
     * <p>
     * Expected results: an error is thrown because of the missing topology model.
     * </p>
     */
    @Test
    public void error_topologyModelMissing() throws Exception {
        final Model model = new Model();
        model.setServiceUnitModel(new ServiceUnitModel());
        model.setComponentRepository(new ComponentRepository());
        // Topology model missing
        model.setBusModel(new BusModel());

        final Exception exception = assertThrows(ModelValidationException.class, () -> {
            ModelConverter.convertModelToRuntimeModel(model);
        });

        assertEquals("The topology model is missing in the model.", exception.getMessage());
    }

    /**
     * <p>
     * Try to convert a model where the topology model part is empty.
     * </p>
     * <p>
     * Expected results: an error is thrown because of the empty topology model.
     * </p>
     */
    @Test
    public void error_topologyModelEmpty() throws Exception {
        final Model model = new Model();
        model.setServiceUnitModel(new ServiceUnitModel());
        model.setComponentRepository(new ComponentRepository());
        model.setTopologyModel(new TopologyModel());
        model.setBusModel(new BusModel());

        final Exception exception = assertThrows(ModelValidationException.class, () -> {
            ModelConverter.convertModelToRuntimeModel(model);
        });

        assertEquals("The topology model is empty in the model.", exception.getMessage());
    }

    /**
     * <p>
     * Try to convert a model where the bus model part is missing.
     * </p>
     * <p>
     * Expected results: an error is thrown because of the missing bus model.
     * </p>
     */
    @Test
    public void error_busModelMissing() throws Exception {
        final Model model = new Model();
        model.setServiceUnitModel(new ServiceUnitModel());
        model.setComponentRepository(new ComponentRepository());
        final TopologyModel topologyModel = new TopologyModel();
        final Topology topology = new Topology();
        topologyModel.getTopology().add(topology);
        topology.getContainer().add(new Container());
        model.setTopologyModel(topologyModel);
        // Bus model missing

        final Exception exception = assertThrows(ModelValidationException.class, () -> {
            ModelConverter.convertModelToRuntimeModel(model);
        });

        assertEquals("The bus model definition is missing in the model.", exception.getMessage());
    }

    /**
     * <p>
     * Try to convert a model containing:
     * </p>
     * <ul>
     * <li>a shared library definition with an URL locating a ZIP archive that is not a JBI shared library archive.</li>
     * </ul>
     * <p>
     * Expected results: an error is thrown because of the invalid ZIP archive.
     * </p>
     */
    @Test
    public void error_notSharedLibraryArchive() throws Exception {

        final Model model = ModelUtils.generateTestModelWithSharedLibrary();

        final SharedLibrary sl = (SharedLibrary) model.getComponentRepository().getComponentOrSharedLibrary().get(1);
        assertEquals("petals-sl-hsql-1.8.0.10", sl.getId());
        sl.setUrl(ModelUtils.class.getResource("/artifacts/sa-SQL.zip").toURI().toURL().toString());

        final Exception exception = assertThrows(ModelValidationException.class, () -> {
            ModelConverter.convertModelToRuntimeModel(model);
        });

        assertEquals(
                String.format("The ZIP archive located at '%s' is not a JBI shared library ZIP archive", sl.getUrl()),
                exception.getMessage());
    }

    /**
     * <p>
     * Try to convert a model containing:
     * </p>
     * <ul>
     * <li>a component instance referring to an unexisting component.</li>
     * </ul>
     * <p>
     * Expected results: an error is thrown because of the unexisting component definition.
     * </p>
     */
    @Test
    public void error_componentMissing() throws Exception {
        final Model model = new Model();
        model.setServiceUnitModel(new ServiceUnitModel());
        model.setComponentRepository(new ComponentRepository());
        model.setTopologyModel(ModelUtils.generateTestTopologyModel("my-topo", "my-cont"));

        final String topoId = "my-topo";
        final String contId = "my-cont";
        model.setTopologyModel(ModelUtils.generateTestTopologyModel(topoId, contId));
        final BusModel busModel = new BusModel();
        model.setBusModel(busModel);
        final String machineId = "my-machine";
        busModel.getMachine().add(ModelUtils.generateTestProvisionedMachine(machineId));

        final Bus bus = new Bus();
        bus.setTopologyRef(topoId);
        busModel.getBus().add(bus);

        final ContainerInstance contInst = new ContainerInstance();
        contInst.setRef(contId);
        contInst.setMachineRef(machineId);
        bus.getContainerInstance().add(contInst);

        final ComponentInstance compInst = new ComponentInstance();
        compInst.setRef("unexisting-comp");
        contInst.getComponentInstance().add(compInst);

        final Exception exception = assertThrows(ModelValidationException.class, () -> {
            ModelConverter.convertModelToRuntimeModel(model);
        });

        assertEquals(String.format(
                "Component reference '%s' of a component instance has no definition in the component repository of the model",
                "unexisting-comp"), exception.getMessage());
    }

    /**
     * <p>
     * Try to convert a model containing:
     * </p>
     * <ul>
     * <li>a component definition is duplicated in the component repository.</li>
     * </ul>
     * <p>
     * Expected results: an error is thrown because of the component duplication.
     * </p>
     */
    @Test
    public void error_duplicatedComponent() throws Exception {
        final Model model = ModelUtils.generateTestModel();

        final Component comp = new Component();
        comp.setId("petals-bc-soap");
        comp.setUrl(ModelUtils.class.getResource("/artifacts/petals-bc-soap-5.0.0.zip").toURI().toURL().toString());
        model.getComponentRepository().getComponentOrSharedLibrary().add(comp);

        final Exception exception = assertThrows(ModelValidationException.class, () -> {
            ModelConverter.convertModelToRuntimeModel(model);
        });

        assertEquals(String.format("Duplicated component definition '%s' in the component repository", comp.getId()),
                exception.getMessage());
    }

    /**
     * <p>
     * Try to convert a model containing:
     * </p>
     * <ul>
     * <li>a component instance is duplicated in the container model.</li>
     * </ul>
     * <p>
     * Expected results: an error is thrown because of the component duplication.
     * </p>
     */
    @Test
    public void error_duplicatedComponentInstance() throws Exception {
        final Model model = ModelUtils.generateTestModel();

        final ComponentInstance duplicatedCompInst = new ComponentInstance();
        duplicatedCompInst.setRef("petals-bc-soap");
        model.getBusModel().getBus().get(0).getContainerInstance().get(0).getComponentInstance()
                .add(duplicatedCompInst);

        final Exception exception = assertThrows(ModelValidationException.class, () -> {
            ModelConverter.convertModelToRuntimeModel(model);
        });

        assertEquals(String.format("Duplicated component instance referring to '%s'", duplicatedCompInst.getRef()),
                exception.getMessage());
    }

    /**
     * <p>
     * Try to convert a model containing:
     * </p>
     * <ul>
     * <li>a component definition with an URL locating a ZIP archive that is not a JBI component archive.</li>
     * </ul>
     * <p>
     * Expected results: an error is thrown because of the invalid ZIP archive.
     * </p>
     */
    @Test
    public void error_notComponentArchive() throws Exception {
        final Model model = new Model();
        model.setServiceUnitModel(new ServiceUnitModel());
        final ComponentRepository compRepo = new ComponentRepository();
        model.setComponentRepository(compRepo);
        model.setTopologyModel(ModelUtils.generateTestTopologyModel("my-topo", "my-cont"));

        final Component bcSoap = new Component();
        bcSoap.setId("petals-bc-soap");
        bcSoap.setUrl(ModelUtils.class.getResource("/artifacts/sa-SQL.zip").toURI().toURL().toString());
        compRepo.getComponentOrSharedLibrary().add(bcSoap);

        final String topoId = "my-topo";
        final String contId = "my-cont";
        model.setTopologyModel(ModelUtils.generateTestTopologyModel(topoId, contId));
        final BusModel busModel = new BusModel();
        model.setBusModel(busModel);
        final String machineId = "my-machine";
        busModel.getMachine().add(ModelUtils.generateTestProvisionedMachine(machineId));

        final Bus bus = new Bus();
        bus.setTopologyRef(topoId);
        busModel.getBus().add(bus);

        final ContainerInstance contInst = new ContainerInstance();
        contInst.setRef(contId);
        contInst.setMachineRef(machineId);
        bus.getContainerInstance().add(contInst);

        final ComponentInstance compInst = new ComponentInstance();
        compInst.setRef(bcSoap.getId());
        contInst.getComponentInstance().add(compInst);

        final Exception exception = assertThrows(ModelValidationException.class, () -> {
            ModelConverter.convertModelToRuntimeModel(model);
        });

        assertEquals(
                String.format("The ZIP archive located at '%s' is not a JBI component ZIP archive", bcSoap.getUrl()),
                exception.getMessage());
    }

    /**
     * <p>
     * Try to convert a model containing:
     * </p>
     * <ul>
     * <li>a shared library definition is duplicated in the component repository.</li>
     * </ul>
     * <p>
     * Expected results: an error is thrown because of the shared library duplication.
     * </p>
     */
    @Test
    public void error_duplicatedSharedLibrary() throws Exception {
        final Model model = ModelUtils.generateTestModelWithSharedLibrary();

        final SharedLibrary sl = new SharedLibrary();
        sl.setId("petals-sl-hsql-1.8.0.10");
        sl.setVersion("1.0");
        sl.setUrl(ModelUtils.class.getResource("/artifacts/petals-sl-hsql-1.8.0.10.zip").toURI().toURL().toString());
        model.getComponentRepository().getComponentOrSharedLibrary().add(sl);

        final Exception exception = assertThrows(ModelValidationException.class, () -> {
            ModelConverter.convertModelToRuntimeModel(model);
        });

        assertEquals(String.format(
                "Duplicated shared library definition '%s' with version '1.0' in the component repository", sl.getId(),
                sl.getVersion()), exception.getMessage());
    }

    /**
     * <p>
     * Try to convert a model containing:
     * </p>
     * <ul>
     * <li>a service unit instance referring to an unexisting service unit.</li>
     * </ul>
     * <p>
     * Expected results: an error is thrown because of the unexisting service unit definition.
     * </p>
     */
    @Test
    public void error_serviceUnitDefinitionMissing() throws Exception {
        final Model model = new Model();
        model.setServiceUnitModel(new ServiceUnitModel());
        model.setComponentRepository(new ComponentRepository());
        final String topoId = "my-topo";
        final String contId = "my-cont";
        model.setTopologyModel(ModelUtils.generateTestTopologyModel(topoId, contId));
        final BusModel busModel = new BusModel();
        model.setBusModel(busModel);
        final String machineId = "my-machine";
        busModel.getMachine().add(ModelUtils.generateTestProvisionedMachine(machineId));

        final Bus bus = new Bus();
        bus.setTopologyRef(topoId);
        busModel.getBus().add(bus);

        final ContainerInstance contInst = new ContainerInstance();
        contInst.setRef(contId);
        contInst.setMachineRef(machineId);
        bus.getContainerInstance().add(contInst);

        final String suId = "unexisting-su-definition";
        final ServiceUnitInstance suInst = new ServiceUnitInstance();
        suInst.setRef(suId);
        contInst.getServiceUnitInstance().add(suInst);

        final Exception exception = assertThrows(ModelValidationException.class, () -> {
            ModelConverter.convertModelToRuntimeModel(model);
        });

        assertEquals(
                "Service unit reference '" + suId
                        + "' of a service unit instance has no definition in the service unit model",
                exception.getMessage());
    }

    /**
     * <p>
     * Try to convert a model containing:
     * </p>
     * <ul>
     * <li>a service unit definition with an URL locating a ZIP archive that is not a JBI service assembly archive.</li>
     * </ul>
     * <p>
     * Expected results: an error is thrown because of the invalid ZIP archive.
     * </p>
     */
    @Test
    public void error_notServiceUnitArchiveAsServiceAssembly() throws Exception {
        final Model model = new Model();
        final ServiceUnitModel suModel = new ServiceUnitModel();

        final ServiceUnit su = new ServiceUnit();
        su.setId("my-su");
        su.setUrl(ModelUtils.class.getResource("/artifacts/petals-bc-soap-5.0.0.zip").toURI().toURL().toString());
        suModel.getServiceUnit().add(su);

        model.setServiceUnitModel(suModel);
        model.setComponentRepository(new ComponentRepository());
        final String topoId = "my-topo";
        final String contId = "my-cont";
        model.setTopologyModel(ModelUtils.generateTestTopologyModel(topoId, contId));
        final BusModel busModel = new BusModel();
        model.setBusModel(busModel);
        final String machineId = "my-machine";
        busModel.getMachine().add(ModelUtils.generateTestProvisionedMachine(machineId));

        final Bus bus = new Bus();
        bus.setTopologyRef(topoId);
        busModel.getBus().add(bus);

        final ContainerInstance contInst = new ContainerInstance();
        contInst.setRef(contId);
        contInst.setMachineRef(machineId);
        bus.getContainerInstance().add(contInst);

        final ServiceUnitInstance suInst = new ServiceUnitInstance();
        suInst.setRef(su.getId());
        contInst.getServiceUnitInstance().add(suInst);

        final Exception exception = assertThrows(ModelValidationException.class, () -> {
            ModelConverter.convertModelToRuntimeModel(model);
        });

        assertEquals(
                String.format("The ZIP archive located at '%s' is not a JBI service unit ZIP archive", su.getUrl()),
                exception.getMessage());
    }

    /**
     * <p>
     * Try to convert a model containing:
     * </p>
     * <ul>
     * <li>a service unit definition with an URL locating a ZIP archive that is a JBI service unit archive but not
     * autodeployable.</li>
     * </ul>
     * <p>
     * Expected results: an error is thrown because of the invalid ZIP archive.
     * </p>
     */
    @Test
    public void error_notServiceUnitArchiveAsAutodeployable() throws Exception {
        final Model model = new Model();
        final ServiceUnitModel suModel = new ServiceUnitModel();

        final ServiceUnit su = new ServiceUnit();
        su.setId("my-su");
        su.setUrl(ModelUtils.class.getResource("/artifacts/su-SOAP-Hello_PortType-consume.zip").toURI().toURL()
                .toString());
        suModel.getServiceUnit().add(su);

        model.setServiceUnitModel(suModel);
        model.setComponentRepository(new ComponentRepository());
        final String topoId = "my-topo";
        final String contId = "my-cont";
        model.setTopologyModel(ModelUtils.generateTestTopologyModel(topoId, contId));
        final BusModel busModel = new BusModel();
        model.setBusModel(busModel);
        final String machineId = "my-machine";
        busModel.getMachine().add(ModelUtils.generateTestProvisionedMachine(machineId));

        final Bus bus = new Bus();
        bus.setTopologyRef(topoId);
        busModel.getBus().add(bus);

        final ContainerInstance contInst = new ContainerInstance();
        contInst.setRef(contId);
        contInst.setMachineRef(machineId);
        bus.getContainerInstance().add(contInst);

        final ServiceUnitInstance suInst = new ServiceUnitInstance();
        suInst.setRef(su.getId());
        contInst.getServiceUnitInstance().add(suInst);

        final Exception exception = assertThrows(ModelValidationException.class, () -> {
            ModelConverter.convertModelToRuntimeModel(model);
        });

        assertEquals(
                String.format("The ZIP archive located at '%s' is not a JBI service unit ZIP archive", su.getUrl()),
                exception.getMessage());
    }

    /**
     * <p>
     * Try to convert a model containing:
     * </p>
     * <ul>
     * <li>a service unit definition with an URL locating a ZIP service assembly archive,</li>
     * <li>no service unit with the given identifier exists in the ZIP archive.</li>
     * </ul>
     * <p>
     * Expected results: an error is thrown because of the invalid service unit identifier.
     * </p>
     */
    @Test
    public void error_invalidServiceUnitIdAsServiceAssembly() throws Exception {
        final Model model = new Model();
        final ServiceUnitModel suModel = new ServiceUnitModel();

        final ServiceUnit su = new ServiceUnit();
        su.setId("my-su");
        su.setUrl(ModelUtils.class.getResource("/artifacts/sa-SQL.zip").toURI().toURL().toString());
        suModel.getServiceUnit().add(su);

        model.setServiceUnitModel(suModel);
        model.setComponentRepository(new ComponentRepository());
        final String topoId = "my-topo";
        final String contId = "my-cont";
        model.setTopologyModel(ModelUtils.generateTestTopologyModel(topoId, contId));
        final BusModel busModel = new BusModel();
        model.setBusModel(busModel);
        final String machineId = "my-machine";
        busModel.getMachine().add(ModelUtils.generateTestProvisionedMachine(machineId));

        final Bus bus = new Bus();
        bus.setTopologyRef(topoId);
        busModel.getBus().add(bus);

        final ContainerInstance contInst = new ContainerInstance();
        contInst.setRef(contId);
        contInst.setMachineRef(machineId);
        bus.getContainerInstance().add(contInst);

        final ServiceUnitInstance suInst = new ServiceUnitInstance();
        suInst.setRef(su.getId());
        contInst.getServiceUnitInstance().add(suInst);

        final Exception exception = assertThrows(ModelValidationException.class, () -> {
            ModelConverter.convertModelToRuntimeModel(model);
        });

        assertEquals(String.format("The service unit '%s' is not defined in the given ZIP archive '%s'", su.getId(),
                su.getUrl()), exception.getMessage());
    }

    /**
     * <p>
     * Try to convert a model containing:
     * </p>
     * <ul>
     * <li>a service unit definition with an URL locating a ZIP audeployable service unit archive,</li>
     * <li>the service unit identifier of the ZIP archive does not match the service unit identifier of the model.</li>
     * </ul>
     * <p>
     * Expected results: an error is thrown because of the invalid service unit identifier.
     * </p>
     */
    @Test
    public void error_invalidServiceUnitIdAsAutodeployable() throws Exception {
        final Model model = new Model();
        final ServiceUnitModel suModel = new ServiceUnitModel();

        final ServiceUnit su = new ServiceUnit();
        su.setId("my-su");
        su.setUrl(ModelUtils.class.getResource("/artifacts/su-SOAP-Hello_Service1-provide.zip").toURI().toURL()
                .toString());
        suModel.getServiceUnit().add(su);

        model.setServiceUnitModel(suModel);
        model.setComponentRepository(new ComponentRepository());
        final String topoId = "my-topo";
        final String contId = "my-cont";
        model.setTopologyModel(ModelUtils.generateTestTopologyModel(topoId, contId));
        final BusModel busModel = new BusModel();
        model.setBusModel(busModel);
        final String machineId = "my-machine";
        busModel.getMachine().add(ModelUtils.generateTestProvisionedMachine(machineId));

        final Bus bus = new Bus();
        bus.setTopologyRef(topoId);
        busModel.getBus().add(bus);

        final ContainerInstance contInst = new ContainerInstance();
        contInst.setRef(contId);
        contInst.setMachineRef(machineId);
        bus.getContainerInstance().add(contInst);

        final ServiceUnitInstance suInst = new ServiceUnitInstance();
        suInst.setRef(su.getId());
        contInst.getServiceUnitInstance().add(suInst);

        final Exception exception = assertThrows(ModelValidationException.class, () -> {
            ModelConverter.convertModelToRuntimeModel(model);
        });

        assertEquals(String.format(String.format("The service unit '%s' is not defined in the given ZIP archive '%s'",
                su.getId(), su.getUrl())), exception.getMessage());
    }

    /**
     * <p>
     * Try to convert a model containing:
     * </p>
     * <ul>
     * <li>a service unit definition with an URL locating a ZIP service assembly archive,</li>
     * <li>the service unit target component is not referenced by the current container to be deployed.</li>
     * </ul>
     * <p>
     * Expected results: an error is thrown because of the missing component required by the service unit.
     * </p>
     */
    @Test
    public void error_unexistingTargetComponentAsServiceAssembly() throws Exception {
        final Model model = new Model();
        final ServiceUnitModel suModel = new ServiceUnitModel();

        final ServiceUnit su = new ServiceUnit();
        su.setId("su-SQL");
        su.setUrl(ModelUtils.class.getResource("/artifacts/sa-SQL.zip").toURI().toURL().toString());
        suModel.getServiceUnit().add(su);

        model.setServiceUnitModel(suModel);
        model.setComponentRepository(new ComponentRepository());
        final String topoId = "my-topo";
        final String contId = "my-cont";
        model.setTopologyModel(ModelUtils.generateTestTopologyModel(topoId, contId));
        final BusModel busModel = new BusModel();
        model.setBusModel(busModel);
        final String machineId = "my-machine";
        busModel.getMachine().add(ModelUtils.generateTestProvisionedMachine(machineId));

        final Bus bus = new Bus();
        bus.setTopologyRef(topoId);
        busModel.getBus().add(bus);

        final ContainerInstance contInst = new ContainerInstance();
        contInst.setRef(contId);
        contInst.setMachineRef(machineId);
        bus.getContainerInstance().add(contInst);

        final ServiceUnitInstance suInst = new ServiceUnitInstance();
        suInst.setRef(su.getId());
        contInst.getServiceUnitInstance().add(suInst);

        final Exception exception = assertThrows(ModelValidationException.class, () -> {
            ModelConverter.convertModelToRuntimeModel(model);
        });

        assertEquals(String.format(
                "The target component of the service unit '%s' located at '%s' is not defined to be deployed on container '%s'",
                su.getId(), su.getUrl(), contId), exception.getMessage());
    }

    /**
     * <p>
     * Try to convert a model containing:
     * </p>
     * <ul>
     * <li>a service unit definition with an URL locating a ZIP audeployable service unit archive,</li>
     * <li>the service unit target component is not referenced by the current container to be deployed.</li>
     * </ul>
     * <p>
     * Expected results: an error is thrown because of the missing component required by the service unit.
     * </p>
     */
    @Test
    public void error_unexistingTargetComponentAsAutodeployable() throws Exception {
        final Model model = new Model();
        final ServiceUnitModel suModel = new ServiceUnitModel();

        final ServiceUnit su = new ServiceUnit();
        su.setId("su-SQL");
        su.setUrl(ModelUtils.class.getResource("/artifacts/sa-SQL.zip").toURI().toURL().toString());
        suModel.getServiceUnit().add(su);

        model.setServiceUnitModel(suModel);
        model.setComponentRepository(new ComponentRepository());
        final String topoId = "my-topo";
        final String contId = "my-cont";
        model.setTopologyModel(ModelUtils.generateTestTopologyModel(topoId, contId));
        final BusModel busModel = new BusModel();
        model.setBusModel(busModel);
        final String machineId = "my-machine";
        busModel.getMachine().add(ModelUtils.generateTestProvisionedMachine(machineId));

        final Bus bus = new Bus();
        bus.setTopologyRef(topoId);
        busModel.getBus().add(bus);

        final ContainerInstance contInst = new ContainerInstance();
        contInst.setRef(contId);
        contInst.setMachineRef(machineId);
        bus.getContainerInstance().add(contInst);

        final ServiceUnitInstance suInst = new ServiceUnitInstance();
        suInst.setRef(su.getId());
        contInst.getServiceUnitInstance().add(suInst);

        final Exception exception = assertThrows(ModelValidationException.class, () -> {
            ModelConverter.convertModelToRuntimeModel(model);
        });

        assertEquals(String.format(
                "The target component of the service unit '%s' located at '%s' is not defined to be deployed on container '%s'",
                su.getId(), su.getUrl(), contId), exception.getMessage());
    }
}
