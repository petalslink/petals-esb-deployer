package org.ow2.petals.deploymentmodel.componentrepository;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Class describing a component artifact.
 *
 * @author alagane
 */
public class Component {
    /**
     * The identifier of this component in the model. Must be the same than the one defined in the JBI descriptor of the
     * component.
     */
    private String id;

    /**
     * The URL of the associated archive.
     */
    private URI url;

    /**
     * The map of parameters required by the component. The key of the map is the name of the parameter, and the value
     * of the map is the default value of the parameter. If there is no default value, the value must be null.
     */
    private Map<String, String> parameters;

    /**
     * A set of shared library references that the component requires. They reference shared libraries defined in the
     * model by their id. Optional.
     */
    private List<SharedLibrary> sharedLibraryReferences;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public URI getUrl() {
        return url;
    }

    public void setUrl(final URI url) {
        this.url = url;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(final Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public List<SharedLibrary> getSharedLibraryReferences() {
        return sharedLibraryReferences;
    }

    public void setSharedLibraryReferences(final List<SharedLibrary> sharedLibraryReferences) {
        this.sharedLibraryReferences = sharedLibraryReferences;
    }
}
