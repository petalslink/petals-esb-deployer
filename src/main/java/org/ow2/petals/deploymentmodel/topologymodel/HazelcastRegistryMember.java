package org.ow2.petals.deploymentmodel.topologymodel;

/**
 * Class describing members of the Petals ESB Hazelcast Registry.
 *
 * @author alagane
 */
public class HazelcastRegistryMember {
    /**
     * Identifier used as name in the file cluster.xml.
     */
    private String id;

    /**
     * Default communication port value. A deployment property can be used. Optional, if not set, the internal default
     * value '7900' will be used.
     */
    private String defaultPort;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getDefaultPort() {
        return defaultPort;
    }

    public void setDefaultPort(final String defaultPort) {
        this.defaultPort = defaultPort;
    }
}
