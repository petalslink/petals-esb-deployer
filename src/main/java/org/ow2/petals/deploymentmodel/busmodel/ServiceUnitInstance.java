package org.ow2.petals.deploymentmodel.busmodel;

import java.awt.Container;

import org.ow2.petals.deploymentmodel.serviceunitmodel.ServiceUnit;

/**
 * Class for instance of a service unit running on a Petals ESB container
 *
 * @author alagane
 */
public class ServiceUnitInstance {
    /**
     * Reference to a service-unit of the service-units object model
     */
    private ServiceUnit reference;

    /**
     * Reference to a container of the topology object model
     */
    private Container containerReference;

    public ServiceUnit getReference() {
        return reference;
    }

    public void setReference(final ServiceUnit reference) {
        this.reference = reference;
    }

    public Container getContainerReference() {
        return containerReference;
    }

    public void setContainerReference(final Container containerReference) {
        this.containerReference = containerReference;
    }
}
