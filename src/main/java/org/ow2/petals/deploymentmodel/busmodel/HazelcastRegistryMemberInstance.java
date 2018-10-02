package org.ow2.petals.deploymentmodel.busmodel;

import org.ow2.petals.deploymentmodel.topologymodel.HazelcastRegistryMember;

/**
 * Class describing a member of a Petals ESB Hazelcast Registry instance.
 *
 * @author alagane
 */
public class HazelcastRegistryMemberInstance {
    /**
     * The reference of the member of the Petals ESB Hazelcast Registry in
     * {@link org.ow2.petals.deploymentmodel.topologymodel.TopologyModel}.
     */
    private HazelcastRegistryMember reference;

    /**
     * The communication port of the member instance. A deployment property can be used. Optional, if not set, the value
     * defined by the referenced {@link HazelcastRegistryMember} will be used.
     */
    private String port;

    /**
     * The reference of the machine on which the current Petals ESB Hazelcast Registry is running.
     */
    private Machine machineReference;

    public HazelcastRegistryMember getReference() {
        return reference;
    }

    public void setReference(final HazelcastRegistryMember reference) {
        this.reference = reference;
    }

    public String getPort() {
        return port;
    }

    public void setPort(final String port) {
        this.port = port;
    }

    public Machine getMachineReference() {
        return machineReference;
    }

    public void setMachineReference(final Machine machineReference) {
        this.machineReference = machineReference;
    }
}
