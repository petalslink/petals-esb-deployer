package org.ow2.petals.deploymentmodel.busmodel;

import java.util.List;

import org.ow2.petals.deploymentmodel.topologymodel.Topology;

/**
 * Class describing an instance of a topology in {@link org.ow2.petals.deploymentmodel.busmodel.BusModel}.
 *
 * @author alagane
 */
public class TopologyInstance {
    /**
     * The reference topology from {@link org.ow2.petals.deploymentmodel.topologymodel.TopologyModel}.
     */
    private Topology reference;

    /**
     * Domain name of the Petals ESB bus. A deployment property can be used. It must be set if no default value is set
     * in its reference topology.
     */
    private String domainName;

    /**
     * The instances of registry to use in Topology.
     */
    private RegistryInstance registryInstance;

    /**
     * The instances of Petals ESB containers forming the Petals ESB bus.
     */
    private List<ContainerInstance> containerInstances;

    public Topology getReference() {
        return reference;
    }

    public void setReference(final Topology reference) {
        this.reference = reference;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(final String domainName) {
        this.domainName = domainName;
    }

    public RegistryInstance getRegistryInstance() {
        return registryInstance;
    }

    public void setRegistryInstance(final RegistryInstance registryInstance) {
        this.registryInstance = registryInstance;
    }

    public List<ContainerInstance> getContainerInstances() {
        return containerInstances;
    }

    public void setContainerInstances(final List<ContainerInstance> containerInstances) {
        this.containerInstances = containerInstances;
    }
}
