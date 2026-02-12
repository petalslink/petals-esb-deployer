/**
 * Copyright (c) 2018-2026 Linagora
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.ow2.petals.jbi.descriptor.JBIDescriptorException;
import org.ow2.petals.jbi.descriptor.extension.JBIDescriptorExtensionBuilder;

import jakarta.validation.constraints.NotNull;

/**
 * This is an utility class and should not be instantiated.
 * 
 * @author Alexandre Lagane - Linagora
 */
public class ModelConverter {

    private ModelConverter() {
    }

    /**
     * <p>
     * Convert a {@link Model} into {@link RuntimeModel} to prepare its deployment.
     * </p>
     * <p>
     * During conversion, validation rules are checked to prevent errors during deployment.
     * </p>
     * 
     * @param model
     *            The {@link Model} to convert
     * @return The model converted into {@link RuntimeModel}
     * @throws ModelValidationException
     *             A model validation rule is violated
     */
    @NotNull
    public static RuntimeModel convertModelToRuntimeModel(@NotNull final Model model) throws ModelValidationException {

        final Map<String, ServiceUnit> suById = new HashMap<>();
        convertServiceUnitModel(model, suById);

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
            convertContainerToRuntimeContainer(cont, model, runtimeModel, compById, slByIdAndVersion, suById);
        }

        return runtimeModel;
    }

    private static void convertServiceUnitModel(@NotNull final Model model,
            @NotNull final Map<String, ServiceUnit> suById) {
        final ServiceUnitModel suModel = model.getServiceUnitModel();
        for (final ServiceUnit su : suModel.getServiceUnit()) {
            suById.put(su.getId(), su);
        }
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
                final Component comp = (Component) compOrSl;
                if (!compById.containsKey(comp.getId())) {
                    compById.put(comp.getId(), comp);
                } else {
                    throw new ModelValidationException(String
                            .format("Duplicated component definition '%s' in the component repository", comp.getId()));
                }
            }
            if (compOrSl instanceof SharedLibrary) {
                final SharedLibrary sl = (SharedLibrary) compOrSl;
                if (!slByIdAndVersion.containsKey(new RuntimeSharedLibrary.IdAndVersion(sl.getId(), sl.getVersion()))) {
                    slByIdAndVersion.put(new RuntimeSharedLibrary.IdAndVersion(sl.getId(), sl.getVersion()), sl);
                } else {
                    throw new ModelValidationException(String.format(
                            "Duplicated shared library definition '%s' with version '%s' in the component repository",
                            sl.getId(), sl.getVersion()));
                }
            }
        }
    }

    private static void convertContainerToRuntimeContainer(@NotNull final Container cont, @NotNull final Model model,
            @NotNull final RuntimeModel runtimeModel, @NotNull final Map<String, Component> compById,
            @NotNull final Map<RuntimeSharedLibrary.IdAndVersion, SharedLibrary> slByIdAndVersion,
            @NotNull final Map<String, ServiceUnit> suById) throws ModelValidationException {

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
            final RuntimeContainer runtimeCont = new RuntimeContainer(contId, contPort, contUser, contPassword,
                    hostname);
            runtimeModel.addContainer(runtimeCont);

            for (final ComponentInstance compInst : contInst.getComponentInstance()) {
                convertComponentToRuntimeComponent(compInst, runtimeCont, compById, slByIdAndVersion);
            }

            final Map<String, List<RuntimeServiceUnit>> suByComp = new HashMap<>();
            convertServiceUnitsToRuntimeServiceUnits(contInst, runtimeCont, suById, suByComp);

            checkRuntimeContainerGlobalConfiguration(runtimeCont, suByComp);

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
                checkRuntimeSharedLibraryGlobalConfiguration(runtimeSl);
                runtimeComp.addSharedLibrary(runtimeSl);
            }

            checkRuntimeComponentGlobalConfiguration(runtimeComp);
            try {
                runtimeCont.addComponent(runtimeComp);
            } catch (final DuplicatedComponentException e) {
                throw new ModelValidationException(
                        String.format("Duplicated component instance referring to '%s'", runtimeComp.getId()));
            }
        } catch (final MalformedURLException | DuplicatedSharedLibraryException e) {
            throw new ModelValidationException(e);
        }
    }

    private static void convertServiceUnitsToRuntimeServiceUnits(@NotNull final ContainerInstance contInst,
            @NotNull final RuntimeContainer runtimeCont, @NotNull final Map<String, ServiceUnit> suById,
            final Map<String, List<RuntimeServiceUnit>> suByComp) throws ModelValidationException {

        for (final ServiceUnitInstance suInst : contInst.getServiceUnitInstance()) {
            convertServiceUnitToRuntimeServiceUnit(suInst, runtimeCont, suById, suByComp);
        }
    }

    private static void convertServiceUnitToRuntimeServiceUnit(@NotNull final ServiceUnitInstance suInst,
            @NotNull final RuntimeContainer runtimeCont, @NotNull final Map<String, ServiceUnit> suById,
            final Map<String, List<RuntimeServiceUnit>> suByComp) throws ModelValidationException {

        final String referencedSuId = suInst.getRef();
        final ServiceUnit referencedSu = suById.get(referencedSuId);
        if (referencedSu == null) {
            throw new ModelValidationException(String.format(
                    "Service unit reference '%s' of a service unit instance has no definition in the service unit model",
                    referencedSuId));
        }
        try {
            final RuntimeServiceUnit runtimeSu = new RuntimeServiceUnit(referencedSuId, new URL(referencedSu.getUrl()));

            final List<String> placeholderOfReferencedSu = new ArrayList<>();
            for (final Placeholder placeholder : referencedSu.getPlaceholder()) {
                if (!placeholderOfReferencedSu.contains(placeholder.getName())) {
                    placeholderOfReferencedSu.add(placeholder.getName());
                } else {
                    throw new ModelValidationException(
                            String.format("Placeholder '%s' is duplicated in service unit definition '%s'",
                                    placeholder.getName(), referencedSuId));
                }
                runtimeSu.setPlaceholderValue(placeholder.getName(), placeholder.getValue());
            }

            final List<String> placeholderOfSuInst = new ArrayList<>();
            for (final PlaceholderInstance placeholderInst : suInst.getPlaceholderInstance()) {
                final String ref = placeholderInst.getRef();
                if (!placeholderOfSuInst.contains(ref)) {
                    placeholderOfSuInst.add(ref);
                } else {
                    throw new ModelValidationException(String.format(
                            "Placeholder '%s' is duplicated in service unit instance referring to service unit definition '%s'",
                            ref, referencedSuId));
                }

                if (runtimeSu.getPlaceholderValue(ref) == null) {
                    throw new ModelValidationException(
                            String.format("Placeholder '%s' is not defined in service unit '%s'", ref, referencedSuId));
                }
                runtimeSu.setPlaceholderValue(ref, placeholderInst.getValue());
            }
            checkRuntimeServiceUnitGlobalConfiguration(runtimeSu, runtimeCont, suByComp);
            runtimeCont.addServiceUnit(runtimeSu);
        } catch (final MalformedURLException | DuplicatedServiceUnitException e) {
            throw new ModelValidationException(e);
        }
    }

    /**
     * <p>
     * Check the runtime component configuration in a global point of view:
     * </p>
     * <ul>
     * <li>The ZIP archive given by the URL must be a JBI component ZIP archive,</li>
     * <li>TODO: Check that all parameters given are defined at JBI component ZIP archive level.</li>
     * </ul>
     * 
     * @throws ModelValidationException
     *             A model validation rule is violated.
     */
    private static void checkRuntimeComponentGlobalConfiguration(final RuntimeComponent runtimeComp)
            throws ModelValidationException {

        // The ZIP archive must be a JBI component ZIP archive
        if (runtimeComp.getJbiDescriptor().getComponent() == null) {
            throw new ModelValidationException(
                    String.format("The ZIP archive located at '%s' is not a JBI component ZIP archive",
                            runtimeComp.getUrl().toString()));
        }
    }

    /**
     * <p>
     * Check the shared library configuration in a global point of view:
     * </p>
     * <ul>
     * <li>The ZIP archive given by the URL must be a JBI shared library ZIP archive.</li>
     * </ul>
     * 
     * @throws ModelValidationException
     *             A model validation rule is violated.
     */
    private static void checkRuntimeSharedLibraryGlobalConfiguration(final RuntimeSharedLibrary runtimeSl)
            throws ModelValidationException {

        // The ZIP archive must be a JBI shared library ZIP archive
        if (runtimeSl.getJbiDescriptor().getSharedLibrary() == null) {
            throw new ModelValidationException(
                    String.format("The ZIP archive located at '%s' is not a JBI shared library ZIP archive",
                            runtimeSl.getUrl().toString()));
        }
    }

    /**
     * <p>
     * Check the runtime service unit configuration in a global point of view:
     * </p>
     * <ul>
     * <li>The ZIP archive given by the URL must be a JBI service assembly ZIP archive or a auto-deployable JBI service
     * unit ZIP archive,</li>
     * <li>The service unit id in the model must match the service unit identifier in ZIP archive,</li>
     * <li>The target component must exist in the model definition of the container running the service unit.</li>
     * </ul>
     * 
     * @param runtimeServiceUnit
     *            The runtime service unit to validate
     * @param runtimeCont
     *            The runtime container on which the runtime service will run
     * @throws ModelValidationException
     *             A model validation rule is violated.
     */
    private static void checkRuntimeServiceUnitGlobalConfiguration(final RuntimeServiceUnit runtimeServiceUnit,
            final RuntimeContainer runtimeCont, final Map<String, List<RuntimeServiceUnit>> suByComp)
            throws ModelValidationException {

        boolean isAutodeployableSu = false;
        try {
            JBIDescriptorExtensionBuilder.getInstance()
                    .getDeployableServiceUnitIndentification(runtimeServiceUnit.getJbiDescriptor());
            isAutodeployableSu = true;
        } catch (final JBIDescriptorException e) {
            isAutodeployableSu = false;
        }

        // The ZIP archive must be a JBI service assembly ZIP archive or a auto-deployable JBI service unit ZIP archive
        if (runtimeServiceUnit.getJbiDescriptor().getServiceAssembly() == null && !isAutodeployableSu) {
            throw new ModelValidationException(
                    String.format("The ZIP archive located at '%s' is not a JBI service unit ZIP archive",
                            runtimeServiceUnit.getUrl().toString()));
        }

        // The service unit id in the model must match the service unit identifier in ZIP archive
        String targetComponent = null;
        try {
            if (isAutodeployableSu) {
                // Service unit provided through a auto-deploayble service unit
                if (runtimeServiceUnit.getId().equals(JBIDescriptorExtensionBuilder.getInstance()
                        .getDeployableServiceUnitIndentification(runtimeServiceUnit.getJbiDescriptor()).getName())) {
                    targetComponent = JBIDescriptorExtensionBuilder.getInstance()
                            .getDeployableServiceUnitTargetComponent(runtimeServiceUnit.getJbiDescriptor());
                } else {
                    throw new ModelValidationException(
                            String.format("The service unit '%s' is not defined in the given ZIP archive '%s'",
                                    runtimeServiceUnit.getId(), runtimeServiceUnit.getUrl().toString()));
                }
            } else {
                // Service unit provided through a service assembly
                boolean serviceUnitFound = false;
                for (final org.ow2.petals.jbi.descriptor.original.generated.ServiceUnit serviceUnit : runtimeServiceUnit
                        .getJbiDescriptor().getServiceAssembly().getServiceUnit()) {
                    if (runtimeServiceUnit.getId().equals(serviceUnit.getIdentification().getName())) {
                        serviceUnitFound = true;
                        targetComponent = serviceUnit.getTarget().getComponentName();
                        break;
                    }
                }
                if (!serviceUnitFound) {
                    throw new ModelValidationException(
                            String.format("The service unit '%s' is not defined in the given ZIP archive '%s'",
                                    runtimeServiceUnit.getId(), runtimeServiceUnit.getUrl().toString()));
                }
            }
        } catch (final JBIDescriptorException e) {
            throw new ModelValidationException(String.format(
                    "Unexpected error because it should have happened sooner checking if the service unit '%s' is auto-deployable.",
                    runtimeServiceUnit.getId()));
        }

        if (targetComponent == null) {
            // This error should not occur because the target component should be set previously
            throw new ModelValidationException(String.format(
                    "The target component of the service unit '%s' located at '%s' is not identified in ZIP archive",
                    runtimeServiceUnit.getId(), runtimeServiceUnit.getUrl().toString()));
        }

        // The target component exists in the model definition of the container running the service unit
        boolean targetCompFound = false;
        for (final RuntimeComponent runtimeComp : runtimeCont.getComponents()) {
            assert runtimeComp.getJbiDescriptor().getComponent() != null;
            assert runtimeComp.getJbiDescriptor().getComponent().getIdentification() != null;
            assert runtimeComp.getJbiDescriptor().getComponent().getIdentification().getName() != null;

            if (targetComponent.equals(runtimeComp.getJbiDescriptor().getComponent().getIdentification().getName())) {
                targetCompFound = true;
                break;
            }
        }
        if (!targetCompFound) {
            throw new ModelValidationException(String.format(
                    "The target component of the service unit '%s' located at '%s' is not defined to be deployed on container '%s'",
                    runtimeServiceUnit.getId(), runtimeServiceUnit.getUrl().toString(), runtimeCont.getId()));
        }

        // Add the current SU to the list of SU by component for next validations
        final List<RuntimeServiceUnit> suList = suByComp.computeIfAbsent(targetComponent, comp -> {
            return new ArrayList<>();
        });
        suList.add(runtimeServiceUnit);

    }

    /**
     * <p>
     * Check the runtime container configuration is global point of view:
     * </p>
     * <ul>
     * <li>Check that placeholders have only one value defined for each component of this container.</li>
     * </ul>
     * 
     * @param runtimeCont
     *            The runtime container on which the runtime service will run
     * @throws ModelValidationException
     *             A model validation rule is violated.
     */
    private static void checkRuntimeContainerGlobalConfiguration(final RuntimeContainer runtimeCont,
            final Map<String, List<RuntimeServiceUnit>> suByComp) throws ModelValidationException {
        // Placeholders must have only one value defined for each component
        for (final RuntimeComponent comp : runtimeCont.getComponents()) {
            final Map<String, String> compPlaceholders = new HashMap<>();
            final List<RuntimeServiceUnit> sus = suByComp.get(comp.getId());
            if (sus != null) {
                for (final RuntimeServiceUnit su : sus) {
                    for (final Entry<String, String> placeholder : su.getPlaceholders().entrySet()) {
                        final String compPlaceholderValue = compPlaceholders.get(placeholder.getKey());
                        if (compPlaceholderValue == null) {
                            compPlaceholders.put(placeholder.getKey(), placeholder.getValue());
                        } else if (!compPlaceholderValue.equals(placeholder.getValue())) {
                            throw new ModelValidationException(String.format(
                                    "The placeholder '%s' has several different values for the component '%s' on container '%s'",
                                    placeholder.getKey(), comp.getId(), runtimeCont.getId()));
                        }
                    }
                }
            }
        }
    }
}
