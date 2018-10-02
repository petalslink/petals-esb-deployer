package org.ow2.petals.deploymentmodel.componentrepository;

import java.util.List;

/**
 * This model defines a set of binding components, service engines and shared libraries that can be used to perform the
 * deployment of a Petals ESB bus. Such models are included in Petals ESB distribution packs, and you can write your
 * own.
 *
 * @author alagane
 */
public class ComponentRepository {
    /**
     * The list of components objects of this repository. Their id must be unique for all component repository models.
     */
    private List<Component> components;

    /**
     * The list of shared libraries of this repository. Optional. Their couple of id and version must be unique in the
     * model.
     */
    private List<SharedLibrary> sharedLibraries;

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(final List<Component> components) {
        this.components = components;
    }

    public List<SharedLibrary> getSharedLibraries() {
        return sharedLibraries;
    }

    public void setSharedLibraries(final List<SharedLibrary> sharedLibraries) {
        this.sharedLibraries = sharedLibraries;
    }
}
