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
import org.ow2.petals.deployer.model.component_repository.xml._1.SharedLibrary;
import org.ow2.petals.deployer.model.component_repository.xml._1.SharedLibraryReference;
import org.ow2.petals.deployer.model.service_unit.xml._1.ServiceUnit;
import org.ow2.petals.deployer.model.service_unit.xml._1.ServiceUnitModel;
import org.ow2.petals.deployer.model.topology.xml._1.Container;
import org.ow2.petals.deployer.model.xml._1.Model;
import org.ow2.petals.deployer.runtimemodel.RuntimeComponent;
import org.ow2.petals.deployer.runtimemodel.RuntimeContainer;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel;
import org.ow2.petals.deployer.runtimemodel.RuntimeServiceUnit;
import org.ow2.petals.deployer.runtimemodel.RuntimeSharedLibrary;
import org.ow2.petals.deployer.runtimemodel.exceptions.RuntimeModelException;

/**
 * This is an utility class and should not be instanciated.
 * 
 * @author Alexandre Lagane - Linagora
 */
public class ModelConverter {

    private ModelConverter() {
    }

    public static RuntimeModel convertModelToRuntimeModel(final Model model)
            throws MalformedURLException, RuntimeModelException {
        final RuntimeModel runtimeModel = new RuntimeModel();

        final Container cont = model.getTopologyModel().getTopology().get(0).getContainer().get(0);

        final BusModel busModel = model.getBusModel();

        final ContainerInstance contInst = busModel.getBus().get(0).getContainerInstance().get(0);

        final String contId = cont.getId();
        final String hostname = ((ProvisionedMachine) busModel.getMachine().get(0)).getHostname();
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

        final RuntimeContainer runtimeCont = new RuntimeContainer(contId, contPort, contUser, contPassword, hostname);
        runtimeModel.addContainer(runtimeCont);

        final ComponentRepository compRepo = model.getComponentRepository();

        final Map<String, Component> compById = new HashMap<>();
        for (final Component comp : compRepo.getComponent()) {
            compById.put(comp.getId(), comp);
        }

        final Map<String, SharedLibrary> slByIdAndVersion = new HashMap<>();
        for (final SharedLibrary sl : compRepo.getSharedLibrary()) {
            slByIdAndVersion.put(sl.getId() + ":" + sl.getVersion(), sl);
        }

        for (final ComponentInstance compInst : contInst.getComponentInstance()) {
            final String compId = compInst.getRef();
            final Component compRef = compById.get(compId);
            RuntimeComponent runtimeComp = new RuntimeComponent(compId, new URL(compRef.getUrl()));
            for (final SharedLibraryReference slRef : compRef.getSharedLibraryReference()) {
                String idAndVersion = slRef.getRef();
                RuntimeSharedLibrary runtimeSl = runtimeCont.getSharedLibrary(idAndVersion);
                if (runtimeSl == null) {
                    SharedLibrary sl = slByIdAndVersion.get(idAndVersion);
                    runtimeSl = new RuntimeSharedLibrary(sl.getId(), sl.getVersion(), new URL(sl.getUrl()));
                    runtimeCont.addSharedLibrary(runtimeSl);
                }
                runtimeComp.addSharedLibrary(runtimeSl);
            }
            runtimeCont.addComponent(runtimeComp);

        }

        final ServiceUnitModel suModel = model.getServiceUnitModel();
        final Map<String, ServiceUnit> suById = new HashMap<>();
        for (final ServiceUnit su : suModel.getServiceUnit()) {
            suById.put(su.getId(), su);
        }
        for (final ServiceUnitInstance suInst : contInst.getServiceUnitInstance()) {
            final String suId = suInst.getRef();
            final ServiceUnit suRef = suById.get(suId);
            runtimeCont.addServiceUnit(new RuntimeServiceUnit(suId, new URL(suRef.getUrl())));
        }

        return runtimeModel;
    }
}
