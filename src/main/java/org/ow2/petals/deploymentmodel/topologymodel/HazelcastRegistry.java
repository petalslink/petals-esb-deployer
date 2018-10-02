package org.ow2.petals.deploymentmodel.topologymodel;

import java.util.List;

/**
 * Default implementation for a distributed topology.
 *
 * @author alagane
 */
public class HazelcastRegistry extends Registry {
    /**
     * Default group name value. A deployment property can be used. Optional, if not set, the internal default value
     * 'default-sample' will be used.
     */
    private String defaultGroupName;

    /**
     * Default password value. A deployment property can be used. Optional, if not set, the internal default value
     * 's3cr3t' will be used.
     */
    private String defaultGroupPassword;

    /**
     * The members of the Hazelcast registry.
     */
    private List<HazelcastRegistryMember> members;

    public String getDefaultGroupName() {
        return defaultGroupName;
    }

    public void setDefaultGroupName(final String defaultGroupName) {
        this.defaultGroupName = defaultGroupName;
    }

    public String getDefaultGroupPassword() {
        return defaultGroupPassword;
    }

    public void setDefaultGroupPassword(final String defaultGroupPassword) {
        this.defaultGroupPassword = defaultGroupPassword;
    }

    public List<HazelcastRegistryMember> getMembers() {
        return members;
    }

    public void setMembers(final List<HazelcastRegistryMember> members) {
        this.members = members;
    }
}
