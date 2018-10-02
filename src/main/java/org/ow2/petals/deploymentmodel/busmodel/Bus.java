package org.ow2.petals.deploymentmodel.busmodel;

import java.util.List;

/**
 * Class for Petals ESB bus to deploy
 *
 * @author alagane
 */
public class Bus {
    /**
     * The service unit instances to deploy
     */
    private List<ServiceUnitInstance> serviceUnitInstances;

    /**
     * The component instances required by a service unit
     */
    private List<ComponentInstance> componentInstances;

    /**
     * The topology instance associated to a topology defined in {@link org.ow2.petals.deploymentmodel.topologymodel.TopologyModel}.
     */
    private TopologyInstance topologyInstance;

    public List<ServiceUnitInstance> getServiceUnitInstances() {
        return serviceUnitInstances;
    }

    public void setServiceUnitInstances(final List<ServiceUnitInstance> serviceUnitInstances) {
        this.serviceUnitInstances = serviceUnitInstances;
    }

    public List<ComponentInstance> getComponentInstances() {
        return componentInstances;
    }

    public void setComponentInstances(final List<ComponentInstance> componentInstances) {
        this.componentInstances = componentInstances;
    }

    public TopologyInstance getTopologyInstance() {
        return topologyInstance;
    }

    public void setTopologyInstance(final TopologyInstance topologyInstance) {
        this.topologyInstance = topologyInstance;
    }
}
