/**
 * Copyright (c) 2018-2022 Linagora
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
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

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
import org.ow2.petals.deployer.model.topology.xml._1.TopologyModel;
import org.ow2.petals.deployer.model.xml._1.Model;
import org.ow2.petals.deployer.runtimemodel.RuntimeComponent;
import org.ow2.petals.deployer.runtimemodel.RuntimeContainer;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel;
import org.ow2.petals.deployer.runtimemodel.RuntimeServiceUnit;
import org.ow2.petals.deployer.runtimemodel.RuntimeSharedLibrary;
import org.ow2.petals.deployer.runtimemodel.exceptions.DuplicatedComponentException;
import org.ow2.petals.deployer.runtimemodel.exceptions.DuplicatedContainerException;
import org.ow2.petals.deployer.runtimemodel.exceptions.DuplicatedServiceUnitException;
import org.ow2.petals.deployer.runtimemodel.exceptions.DuplicatedSharedLibraryException;
import org.ow2.petals.deployer.utils.exceptions.ModelValidationException;

/**
 * This is an utility class and should not be instanciated.
 * 
 * @author Alexandre Lagane - Linagora
 */
public class ModelConverter {

    private ModelConverter() {
    }

    @NotNull
    public static RuntimeModel convertModelToRuntimeModel(@NotNull final Model model)
            throws ModelValidationException {

        final Map<String, Component> compById = new HashMap<>();
        final Map<RuntimeSharedLibrary.IdAndVersion, SharedLibrary> slByIdAndVersion = new HashMap<>();
        convertComponentAndShareLibraryRepository(model, compById, slByIdAndVersion);

        final RuntimeModel runtimeModel = new RuntimeModel();
        final TopologyModel topologyModel = model.getTopologyModel();
        if (topologyModel == null) {
            throw new ModelValidationException("The topology model is missing in the model.");
        } else if (topologyModel.getTopology().isEmpty()) {
            throw new ModelValidationException("The topology model is empty in the model.");
        }
        final List<Container> containers = topologyModel.getTopology().get(0).getContainer();
        if (!containers.isEmpty()) {
            final Container cont = containers.get(0);
            convertContainerToRuntimeContainer(cont, model, runtimeModel, compById, slByIdAndVersion);
        }

        return runtimeModel;
    }

    private static void convertComponentAndShareLibraryRepository(@NotNull final Model model,
            @NotNull final Map<String, Component> compById,
            @NotNull final Map<RuntimeSharedLibrary.IdAndVersion, SharedLibrary> slByIdAndVersion)
            throws ModelValidationException {
        final ComponentRepository compRepo = model.getComponentRepository();
        if (compRepo == null) {
            throw new ModelValidationException("The component repository definition is missing in the model.");
        }

        for (final Object compOrSl : compRepo.getComponentOrSharedLibrary()) {
            if (compOrSl instanceof Component) {
                Component comp = (Component) compOrSl;
                compById.put(comp.getId(), comp);
            }
            if (compOrSl instanceof SharedLibrary) {
                SharedLibrary sl = (SharedLibrary) compOrSl;
                slByIdAndVersion.put(new RuntimeSharedLibrary.IdAndVersion(sl.getId(), sl.getVersion()), sl);
            }
        }
    }

    private static void convertContainerToRuntimeContainer(@NotNull final Container cont, @NotNull final Model model,
            @NotNull final RuntimeModel runtimeModel, @NotNull final Map<String, Component> compById,
            @NotNull final Map<RuntimeSharedLibrary.IdAndVersion, SharedLibrary> slByIdAndVersion)
            throws ModelValidationException {

        final BusModel busModel = model.getBusModel();
        if (busModel == null) {
            throw new ModelValidationException("The bus model definition is missing in the model.");
        }

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

        try {
            final RuntimeContainer runtimeCont = new RuntimeContainer(contId, contPort, contUser, contPassword, hostname);
            runtimeModel.addContainer(runtimeCont);
    
            for (final ComponentInstance compInst : contInst.getComponentInstance()) {
                convertComponentToRuntimeComponent(compInst, runtimeCont, compById, slByIdAndVersion);
            }

            convertServiceUnitToRuntimeServiceUnit(model, contInst, runtimeCont);

        } catch (final DuplicatedContainerException e) {
            throw new ModelValidationException(e);
        }

    }

    private static void convertComponentToRuntimeComponent(@NotNull final ComponentInstance compInst,
            @NotNull final RuntimeContainer runtimeCont, @NotNull final Map<String, Component> compById,
            @NotNull final Map<RuntimeSharedLibrary.IdAndVersion, SharedLibrary> slByIdAndVersion)
            throws ModelValidationException {
        final String referencedCompId = compInst.getRef();
        final Component referencedComp = compById.get(referencedCompId);
        if (referencedComp == null) {
            throw new ModelValidationException(String.format(
                    "Component reference '%s' of a component instance has no definition in the component repository of the model",
                    referencedCompId));
        }
        try {
            final RuntimeComponent runtimeComp = new RuntimeComponent(referencedCompId,
                    new URL(referencedComp.getUrl()));

            for (final Parameter param : referencedComp.getParameter()) {
                runtimeComp.setParameterValue(param.getName(), param.getValue());
            }
            for (final ParameterInstance paramInst : compInst.getParameterInstance()) {
                final String ref = paramInst.getRef();
                if (runtimeComp.getParameterValue(ref) == null) {
                    throw new ModelValidationException(
                            String.format("Parameter '%s' is not defined in component '%s'", ref, referencedCompId));
                }
                runtimeComp.setParameterValue(paramInst.getRef(), paramInst.getValue());
            }

            for (final SharedLibraryReference slRef : referencedComp.getSharedLibraryReference()) {
                final String id = slRef.getRefId();
                final String version = slRef.getRefVersion();
                RuntimeSharedLibrary runtimeSl = runtimeCont.getSharedLibrary(id, version);
                if (runtimeSl == null) {
                    final SharedLibrary sl = slByIdAndVersion.get(new RuntimeSharedLibrary.IdAndVersion(id, version));
                    runtimeSl = new RuntimeSharedLibrary(id, version, new URL(sl.getUrl()));
                    runtimeCont.addSharedLibrary(runtimeSl);
                }
                runtimeComp.addSharedLibrary(runtimeSl);
            }
            runtimeCont.addComponent(runtimeComp);
        } catch (final MalformedURLException | DuplicatedSharedLibraryException | DuplicatedComponentException e) {
            throw new ModelValidationException(e);
        }
    }

    private static void convertServiceUnitToRuntimeServiceUnit(@NotNull final Model model,
            @NotNull final ContainerInstance contInst, @NotNull final RuntimeContainer runtimeCont)
            throws ModelValidationException {

        final ServiceUnitModel suModel = model.getServiceUnitModel();
        final Map<String, ServiceUnit> suById = new HashMap<>();
        for (final ServiceUnit su : suModel.getServiceUnit()) {
            suById.put(su.getId(), su);
        }
        for (final ServiceUnitInstance suInst : contInst.getServiceUnitInstance()) {
            final String suId = suInst.getRef();
            final ServiceUnit suRef = suById.get(suId);
            if (suRef == null) {
                throw new ModelValidationException(String.format(
                        "Service unit reference '%s' of a service unit instance has no definition in the service unit model",
                        suId));
            }
            try {
                runtimeCont.addServiceUnit(new RuntimeServiceUnit(suId, new URL(suRef.getUrl())));
            } catch (final MalformedURLException | DuplicatedServiceUnitException e) {
                throw new ModelValidationException(e);
            }
        }
    }
}
