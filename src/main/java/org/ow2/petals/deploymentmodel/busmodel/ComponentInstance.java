package org.ow2.petals.deploymentmodel.busmodel;

import java.util.List;
import java.util.Map;

import org.ow2.petals.deploymentmodel.componentrepository.Component;
import org.ow2.petals.deploymentmodel.componentrepository.SharedLibrary;

/**
 * Class describing an instance of a component defined in
 * {@link org.ow2.petals.deploymentmodel.componentrepository.ComponentRepository}.
 *
 * @author alagane
 */
public class ComponentInstance {
    private String id;

    /**
     * Reference to a component from {@link org.ow2.petals.deploymentmodel.componentrepository.ComponentRepository}.
     */
    private Component reference;

    /**
     * The map of parameter values required by all of the service units. The key of the map is the name of the
     * parameter, and the value of the map is the value of the parameter. Only names declared in reference component can
     * be used, and every name declared without a default value must have a value assigned in this map.
     */
    private Map<String, String> placeholderValues;

    /**
     * List of shared libraries in addition to these of the referenced component.
     */
    private List<SharedLibrary> sharedLibraryReferences;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Component getReference() {
        return reference;
    }

    public void setReference(final Component reference) {
        this.reference = reference;
    }

    public Map<String, String> getPlaceholderValues() {
        return placeholderValues;
    }

    public void setPlaceholderValues(final Map<String, String> placeholderValues) {
        this.placeholderValues = placeholderValues;
    }

    public List<SharedLibrary> getSharedLibraryReferences() {
        return sharedLibraryReferences;
    }

    public void setSharedLibraryReferences(final List<SharedLibrary> sharedLibraryReferences) {
        this.sharedLibraryReferences = sharedLibraryReferences;
    }
}
