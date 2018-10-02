package org.ow2.petals.deploymentmodel.serviceunitmodel;

import java.util.List;

/**
 * This model defines a set of service units to deploy on your Petals ESB bus. This model will be mainly written by
 * development teams because they have the knowledge of service units to deploy.
 *
 * @author alagane
 */
public class ServiceUnitModel {
    /**
     * The list of service units to deploy. Their id must be unique in the model. There must be at least one service
     * unit in the model.
     */
    private List<ServiceUnit> serviceUnits;

    public List<ServiceUnit> getServiceUnits() {
        return serviceUnits;
    }

    public void setServiceUnits(final List<ServiceUnit> serviceUnits) {
        this.serviceUnits = serviceUnits;
    }
}
