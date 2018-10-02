package org.ow2.petals.deploymentmodel.serviceunitmodel;

import java.net.URI;
import java.util.Map;

import org.ow2.petals.deploymentmodel.busmodel.ComponentInstance;

/**
 * Class describing a service unit to deploy.
 *
 * @author alagane
 */
public class ServiceUnit {
    /**
     * The unique identifier of this service unit in the model. Can be different from the service unit name.
     */
    private String id;

    /**
     * The URL of the associated archive that can be a service assembly or a deployable service unit.
     */
    private URI url;

    /**
     * The map of placeholder required by the service unit. The key of the map is the key of the placeholder, and the
     * value of the map is the default value of the placeholder. If there is no default value, the value must be null.
     */
    private Map<String, String> placeholders;

    /**
     * The identifier of the component instance on which this service unit must be deployed. Optional for deployable
     * service unit and service unit embedded in a service assembly, required for other service units.
     */
    private ComponentInstance componentInstanceId;

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

    public ComponentInstance getComponentInstanceId() {
        return componentInstanceId;
    }

    public void setComponentInstanceId(final ComponentInstance componentInstanceId) {
        this.componentInstanceId = componentInstanceId;
    }

    public Map<String, String> getPlaceholders() {
        return placeholders;
    }

    public void setPlaceholders(final Map<String, String> placeholders) {
        this.placeholders = placeholders;
    }
}
