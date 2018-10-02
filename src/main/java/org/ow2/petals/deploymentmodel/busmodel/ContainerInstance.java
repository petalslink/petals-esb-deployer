package org.ow2.petals.deploymentmodel.busmodel;

import org.ow2.petals.deploymentmodel.topologymodel.Container;

/**
 * Class describing an instance of a container defined in
 * {@link org.ow2.petals.deploymentmodel.topologymodel.TopologyModel}.
 *
 * @author alagane
 */
public class ContainerInstance {
    /**
     * Reference to a container from {@link org.ow2.petals.deploymentmodel.topologymodel.TopologyModel}.
     */
    private Container reference;

    /**
     * JMX port of the current Petals ESB container. A deployment property can be used. Each container (from topology
     * model) without a default JMX port must have one defined in this model.
     */
    private int jmxPort;

    /**
     * JMX username of the current Petals ESB container. A deployment property can be used. Each container (from
     * topology model) without a default JMX username must have one defined in this model.
     */
    private String jmxUser;

    /**
     * JMX password of the current Petals ESB container. A deployment property can be used. Each container (from
     * topology model) without a default JMX password must have one defined in this model.
     */
    private String jmxPassword;

    /**
     * The reference of the machine on which the container is running.
     */
    private Machine machineReference;

    public Container getReference() {
        return reference;
    }

    public void setReference(final Container reference) {
        this.reference = reference;
    }

    public int getJmxPort() {
        return jmxPort;
    }

    public void setJmxPort(final int jmxPort) {
        this.jmxPort = jmxPort;
    }

    public String getJmxUser() {
        return jmxUser;
    }

    public void setJmxUser(final String jmxUser) {
        this.jmxUser = jmxUser;
    }

    public String getJmxPassword() {
        return jmxPassword;
    }

    public void setJmxPassword(final String jmxPassword) {
        this.jmxPassword = jmxPassword;
    }

    public Machine getMachineReference() {
        return machineReference;
    }

    public void setMachineReference(final Machine machineReference) {
        this.machineReference = machineReference;
    }
}
