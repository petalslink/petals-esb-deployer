package org.ow2.petals.deploymentmodel.busmodel;

/**
 * Class describing physical or virtual machine.
 *
 * @author alagane
 */
public class ProvisionedMachine extends Machine {
    /**
     * Hostname, IP address or deployment property name of the provisioned machine.
     */
    private String hostname;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }
}
