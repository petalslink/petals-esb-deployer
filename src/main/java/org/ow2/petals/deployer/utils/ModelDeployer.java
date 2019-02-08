/**
 * Copyright (c) 2019 Linagora
 *
 * This program/library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This program/library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program/library; If not, see http://www.gnu.org/licenses/
 * for the GNU Lesser General Public License version 2.1.
 */

package org.ow2.petals.deployer.utils;

import java.net.URL;
import java.util.logging.Logger;

import org.ow2.petals.deployer.model.xml._1.Model;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel;
import org.ow2.petals.deployer.utils.exceptions.ModelDeploymentException;

/**
 * The main class used for deploying XML models.
 * 
 * @author Alexandre Lagane - Linagora
 */
public class ModelDeployer {

    /**
     * Connection timeout in milliseconds to get model or artifacts from URL.
     */
    public static final int CONNECTION_TIMEOUT = 5000;

    /**
     * Reading timeout in milliseconds to read model or artifacts from URL.
     */
    public static final int READ_TIMEOUT = 5000;

    private final static Logger LOG = Logger.getLogger(ModelDeployer.class.getName());

    private final RuntimeModelDeployer runtimeModelDeployer;

    private static ModelDeployer instance;

    public ModelDeployer() {
        this(null);
    }

    /**
     * Used only for testing purposes, to mock runtimeModelDeployer.
     * 
     * @param runtimeModelDeployer
     */
    protected ModelDeployer(RuntimeModelDeployer runtimeModelDeployer) {
        this.runtimeModelDeployer = runtimeModelDeployer != null ? runtimeModelDeployer : new RuntimeModelDeployer();
        instance = this;
    }

    /**
     * Download and deploy the model at the url. The model must be an XML model with XSD {code model.xsd} in resources
     * directory.
     * 
     * @param url
     * @throws ModelDeploymentException
     */
    public void deployModel(final URL url) throws ModelDeploymentException {
        final Model model = XmlModelBuilder.readModelFromUrl(url);

        deployModel(model);
    }

    public void deployModel(Model model) throws ModelDeploymentException {
        RuntimeModel runtimeModel;
        try {
            runtimeModel = ModelConverter.convertModelToRuntimeModel(model);

            runtimeModelDeployer.deployRuntimeModel(runtimeModel);
        } catch (Exception e) {
            throw new ModelDeploymentException(e);
        }

    }

    public static ModelDeployer getInstance() {
        return instance != null ? instance : new ModelDeployer();
    }
}
