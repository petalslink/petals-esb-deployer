package org.ow2.petals.deploymentmodel.topologymodel;

import java.util.List;

/**
 * Class describing a topology of a Petals ESB bus.
 *
 * @author alagane
 */
public class Topology {
    private String id;

    /**
     * Default domain name of the topology. Optional.
     */
    private String defaultDomainName;

    /**
     * Petals ESB containers of the topology. Their id must be unique in the topology. There must be at least one
     * container in the topology.
     */
    private List<Container> containers;

    /**
     * The registry used in the topology. Optional.
     */
    private Registry registry;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getDefaultDomainName() {
        return defaultDomainName;
    }

    public void setDefaultDomainName(final String defaultDomainName) {
        this.defaultDomainName = defaultDomainName;
    }

    public List<Container> getContainers() {
        return containers;
    }

    public void setContainers(final List<Container> containers) {
        this.containers = containers;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(final Registry registry) {
        this.registry = registry;
    }
}
