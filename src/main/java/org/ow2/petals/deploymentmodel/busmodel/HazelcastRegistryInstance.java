package org.ow2.petals.deploymentmodel.busmodel;

import java.util.List;

import org.ow2.petals.deploymentmodel.topologymodel.HazelcastRegistry;

/**
 * Class describing an Hazelcast Registry instance of a Petals ESB bus.
 *
 * @author alagane
 */
public class HazelcastRegistryInstance extends RegistryInstance {
    /**
     * The reference of the Petals ESB Hazelcast Registry in
     * {@link org.ow2.petals.deploymentmodel.topologymodel.TopologyModel}.
     */
    private HazelcastRegistry reference;

    /**
     * Name identifying the Petals ESB Hazelcast Registry instance inside the Hazelcast cluster. A deployment property
     * can be used. Optional, if not set, the value defined by the referenced HazelcastRegistry will be used.
     */
    private String groupName;

    /**
     * Password identifying the Petals ESB Hazelcast Registry instance inside the Hazelcast cluster. A deployment
     * property can be used. Optional, if not set, the value defined by the referenced HazelcastRegistry will be used.
     */
    private String groupPassword;

    /**
     * List of member instances composing the current Petals ESB Hazelcast Registry instance.
     */
    private List<HazelcastRegistryMemberInstance> memberInstances;

    public HazelcastRegistry getReference() {
        return reference;
    }

    public void setReference(final HazelcastRegistry reference) {
        this.reference = reference;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }

    public String getGroupPassword() {
        return groupPassword;
    }

    public void setGroupPassword(final String groupPassword) {
        this.groupPassword = groupPassword;
    }

    public List<HazelcastRegistryMemberInstance> getMemberInstances() {
        return memberInstances;
    }

    public void setMemberInstances(final List<HazelcastRegistryMemberInstance> memberInstances) {
        this.memberInstances = memberInstances;
    }
}
