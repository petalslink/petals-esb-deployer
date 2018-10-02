package org.ow2.petals.deploymentmodel.topologymodel;

/**
 * Class for Petals ESB containers members of a topology.
 */
public class Container {
    /**
     * Identifier of the Petals ESB container as used as name in the file topology.xml.
     */
    private String id;

    /**
     * Default JMX port value. A deployment property can be used. Optional, if not set, the internal default value 7700
     * will be used.
     */
    private int defaultJmxPort;

    /**
     * Default JMX username value. A deployment property can be used. Optional, if not set, the internal default value
     * 'petals' will be used.
     */
    private String defaultJmxUser;

    /**
     * Default JMX password value. A deployment property can be used. Optional, if not set, the internal default value
     * 'petals' will be used.
     */
    private String defaultJmxPassword;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public int getDefaultJmxPort() {
        return defaultJmxPort;
    }

    public void setDefaultJmxPort(final int defaultJmxPort) {
        this.defaultJmxPort = defaultJmxPort;
    }

    public String getDefaultJmxUser() {
        return defaultJmxUser;
    }

    public void setDefaultJmxUser(final String defaultJmxUser) {
        this.defaultJmxUser = defaultJmxUser;
    }

    public String getDefaultJmxPassword() {
        return defaultJmxPassword;
    }

    public void setDefaultJmxPassword(final String defaultJmxPassword) {
        this.defaultJmxPassword = defaultJmxPassword;
    }
}
