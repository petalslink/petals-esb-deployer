package org.ow2.petals.deploymentmodel.topologymodel;

/**
 * Superclass for registry types.
 *
 * @author alagane
 */
public abstract class Registry {
    /**
     * Used to identify the registry across the model. It must be unique in the model.
     */
    private String id;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }
}
