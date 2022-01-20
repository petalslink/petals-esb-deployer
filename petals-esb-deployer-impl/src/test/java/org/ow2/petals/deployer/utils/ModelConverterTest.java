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

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.ow2.petals.deployer.model.xml._1.Model;
import org.ow2.petals.deployer.runtimemodel.RuntimeComponent;
import org.ow2.petals.deployer.runtimemodel.RuntimeContainer;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel;
import org.ow2.petals.deployer.runtimemodel.RuntimeServiceUnit;
import org.ow2.petals.deployer.runtimemodel.RuntimeSharedLibrary;

/**
 * @author Alexandre Lagane - Linagora
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
        assertEquals("file:/artifacts/sa-SOAP-Hello_Service1-provide", su.getUrl().toString());

        su = cont.getServiceUnit("su-SOAP-Hello_Service2-provide");
        assertEquals("su-SOAP-Hello_Service2-provide", su.getId());
        assertEquals("file:/artifacts/sa-SOAP-Hello_Service2-provide", su.getUrl().toString());

        su = cont.getServiceUnit("su-SOAP-Hello_PortType-consume");
        assertEquals("su-SOAP-Hello_PortType-consume", su.getId());
        assertEquals("file:/artifacts/sa-SOAP-Hello_PortType-consume", su.getUrl().toString());

        final Collection<RuntimeComponent> components = cont.getComponents();
        assertEquals(1, components.size());

        final RuntimeComponent comp = cont.getComponent("petals-bc-soap");
        assertEquals("petals-bc-soap", comp.getId());
        assertEquals("file:/artifacts/petals-bc-soap-5.0.0", comp.getUrl().toString());
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

        final RuntimeComponent comp = cont.getComponent("id-comp-with-shared-library");
        assertEquals("id-comp-with-shared-library", comp.getId());
        assertEquals("file:dummy-comp-with-sl-file", comp.getUrl().toString());

        assertEquals(1, comp.getSharedLibraries().size());

        final RuntimeSharedLibrary slFromCont = cont.getSharedLibrary("id-shared-library", "1.0");
        assertEquals("id-shared-library", slFromCont.getId());
        assertEquals("1.0", slFromCont.getVersion());
        assertEquals("file:dummy-sl-file", slFromCont.getUrl().toString());

        assertEquals(1, cont.getSharedLibraries().size());

        final RuntimeSharedLibrary slFromComp = cont.getSharedLibrary("id-shared-library", "1.0");
        assertEquals("id-shared-library", slFromComp.getId());
        assertEquals("1.0", slFromComp.getVersion());
        assertEquals("file:dummy-sl-file", slFromComp.getUrl().toString());
    }

    @Test
    public void convertModelWithParametersToRuntimeModel() throws Exception {
        final Model model = ModelUtils.generateTestModelWithParameter();

        final RuntimeModel runtimeModel = ModelConverter.convertModelToRuntimeModel(model);

        final Collection<RuntimeContainer> containers = runtimeModel.getContainers();
        assertEquals(1, containers.size());

        final RuntimeContainer cont = containers.iterator().next();

        final Collection<RuntimeComponent> components = cont.getComponents();
        assertEquals(2, components.size());

        final RuntimeComponent comp = cont.getComponent("id-comp-with-parameter");
        assertEquals("id-comp-with-parameter", comp.getId());
        assertEquals("file:dummy-comp-with-parameter", comp.getUrl().toString());

        Map<String, String> parameters = comp.getParameters();
        assertEquals(1, parameters.size());
        Entry<String, String> param = parameters.entrySet().iterator().next();
        assertEquals("param-with-default-value", param.getKey());
        assertEquals("overridden-value", param.getValue());

    }
}
