package org.ow2.petals.deploymentmodel.busmodel;

/**
 * Superclass for different types of machines on which Petals ESB buses or Hazelcast registry members will be running.
 *
 * @author alagane
 */
public abstract class Machine {
    /**
     * Identifier of the machine.
     */
    private String id;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }
}
