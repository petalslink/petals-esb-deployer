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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
import org.ow2.petals.deployer.model.xml._1.Model;
import org.ow2.petals.deployer.runtimemodel.RuntimeComponent;
import org.ow2.petals.deployer.runtimemodel.RuntimeContainer;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel.RuntimeModelException;
import org.ow2.petals.deployer.runtimemodel.RuntimeServiceUnit;

/**
 * @author alagane
 */
public class ModelConverter {
    public static RuntimeModel convertModelToRuntimeModel(Model model)
            throws MalformedURLException, RuntimeModelException {
        RuntimeModel runtimeModel = new RuntimeModel();

        Container cont = model.getTopologyModel().getTopology().get(0).getContainer().get(0);

        BusModel busModel = model.getBusModel();

        ContainerInstance contInst = busModel.getBus().get(0).getContainerInstance().get(0);

        String contId = cont.getId();
        String hostname = ((ProvisionedMachine) busModel.getMachine().get(0)).getHostname();
        Integer contPort = contInst.getJmxPort();
        if (contPort == null) {
            contPort = cont.getDefaultJmxPort();
        }
        String contUser = contInst.getJmxUser();
        if (contUser == null) {
            contUser = cont.getDefaultJmxUser();
        }
        String contPassword = contInst.getJmxPassword();
        if (contPassword == null) {
            contPassword = cont.getDefaultJmxPassword();
        }

        RuntimeContainer runtimeCont = new RuntimeContainer(contId, contPort, contUser, contPassword, hostname);
        runtimeModel.addContainer(runtimeCont);

        ComponentRepository compRepo = model.getComponentRepository();
        Map<String, Component> compById = new HashMap<String, Component>();
        for (Component comp : compRepo.getComponent()) {
            compById.put(comp.getId(), comp);
        }
        for (ComponentInstance compInst : contInst.getComponentInstance()) {
            String compId = compInst.getRef();
            Component compRef = compById.get(compId);
            runtimeCont.addComponent(new RuntimeComponent(compId, new URL(compRef.getUrl())));
        }

        ServiceUnitModel suModel = model.getServiceUnitModel();
        Map<String, ServiceUnit> suById = new HashMap<String, ServiceUnit>();
        for (ServiceUnit su : suModel.getServiceUnit()) {
            suById.put(su.getId(), su);
        }
        for (ServiceUnitInstance suInst : contInst.getServiceUnitInstance()) {
            String suId = suInst.getRef();
            ServiceUnit suRef = suById.get(suId);
            runtimeCont.addServiceUnit(new RuntimeServiceUnit(suId, new URL(suRef.getUrl())));
        }

        return runtimeModel;
    }
}
