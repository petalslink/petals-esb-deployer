package org.ow2.petals.deploymentmodel.busmodel;

import org.ow2.petals.deploymentmodel.topologymodel.StandaloneRegistry;

/**
 * Instance of {@link org.ow2.petals.deploymentmodel.topologymodel.StandaloneRegistry}.
 *
 * @author alagane
 */
public class StandaloneRegistryInstance extends RegistryInstance {
    private StandaloneRegistry reference;

    public StandaloneRegistry getReference() {
        return reference;
    }

    public void setReference(final StandaloneRegistry reference) {
        this.reference = reference;
    }
}
