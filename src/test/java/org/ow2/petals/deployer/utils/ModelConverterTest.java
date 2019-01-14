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

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;
import org.ow2.petals.deployer.model.xml._1.Model;
import org.ow2.petals.deployer.runtimemodel.RuntimeComponent;
import org.ow2.petals.deployer.runtimemodel.RuntimeContainer;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel;
import org.ow2.petals.deployer.runtimemodel.RuntimeServiceUnit;

public class ModelConverterTest {

    @Test
    public void convertModelToRuntimeModel() throws Exception {
        Model model = ParseModelTest.generateTestModel();

        ModelConverter converter = new ModelConverter();
        RuntimeModel runtimeModel = converter.convertModelToRuntimeModel(model);

        Collection<RuntimeContainer> containers = runtimeModel.getContainers();
        assertEquals(1, containers.size());

        RuntimeContainer cont = containers.iterator().next();
        assertEquals(ParseModelTest.CONTAINER_NAME, cont.getId());
        assertEquals(ParseModelTest.CONTAINER_HOST, cont.getHostname());
        assertEquals(ParseModelTest.CONTAINER_JMX_PORT, cont.getPort());
        assertEquals(ParseModelTest.CONTAINER_USER, cont.getUser());
        assertEquals(ParseModelTest.CONTAINER_PWD, cont.getPassword());

        Collection<RuntimeServiceUnit> serviceUnits = cont.getServiceUnits();
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

        Collection<RuntimeComponent> components = cont.getComponents();
        assertEquals(1, components.size());

        RuntimeComponent comp = cont.getComponent("petals-bc-soap");
        assertEquals("petals-bc-soap", comp.getId());
        assertEquals("file:/artifacts/petals-bc-soap-5.0.0", comp.getUrl().toString());
    }

}
