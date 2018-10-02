package org.ow2.petals.deploymentmodel.busmodel;

import java.util.List;
import java.util.Map;

/**
 * This model defines your Petals ESB bus placing Petals ESB components on their containers:
 * <ul>
 * <li>service-unit on its Petals ESB container</li>
 * <li>Petals ESB container on its machine</li>
 * <li>Petals ESB Hazelcast Registry member on its machine.</li>
 * </ul>
 * This model will be mainly written by the Petals ESB bus architect in agreement with operators.
 *
 * @author alagane
 */
public class BusModel {
    /**
     * The list of Petals ESB busses to deploy.
     */
    private List<Bus> buses;

    /**
     * The map of placeholder values required by all of the service units. The key of the map is the key of the
     * placeholder, and the value of the map is the value of the placeholder. Only keys declared in service unit models
     * can be used, and every key declared without a default value must have a value assigned in this map.
     */
    private Map<String, String> placeholderValues;

    /**
     * The machines on which parts of a Petals ESB bus will be running.
     */
    private List<Machine> machines;

    public List<Bus> getBuses() {
        return buses;
    }

    public void setBuses(final List<Bus> buses) {
        this.buses = buses;
    }

    public Map<String, String> getPlaceholderValues() {
        return placeholderValues;
    }

    public void setPlaceholderValues(final Map<String, String> placeholderValues) {
        this.placeholderValues = placeholderValues;
    }

    public List<Machine> getMachines() {
        return machines;
    }

    public void setMachines(final List<Machine> machines) {
        this.machines = machines;
    }
}
