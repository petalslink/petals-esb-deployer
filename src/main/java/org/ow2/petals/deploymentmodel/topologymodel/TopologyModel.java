package org.ow2.petals.deploymentmodel.topologymodel;

import java.util.List;

/**
 * This model defines a set of topologies, used by Petals ESB buses. This model will be mainly written by the Petals ESB
 * bus architect.
 *
 * @author alagane
 */
public class TopologyModel {
    /**
     * The list of topologies in the model. Their id must be unique in the model. There must be at least one topology in
     * the model.
     */
    private List<Topology> topologies;

    public List<Topology> getTopologies() {
        return topologies;
    }

    public void setTopologies(final List<Topology> topologies) {
        this.topologies = topologies;
    }
}
