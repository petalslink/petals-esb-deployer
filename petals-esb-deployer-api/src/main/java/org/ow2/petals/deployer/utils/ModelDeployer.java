/**
 * Copyright (c) 2019-2025 Linagora
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

import org.ow2.petals.deployer.model.xml._1.Model;
import org.ow2.petals.deployer.utils.exceptions.ModelDeploymentException;
import org.ow2.petals.deployer.utils.exceptions.ModelDeploymentExecutionException;
import org.ow2.petals.deployer.utils.exceptions.ModelParsingException;
import org.ow2.petals.deployer.utils.exceptions.ModelValidationException;

import jakarta.validation.constraints.NotNull;

/**
 * The main class used for deploying XML models.
 * 
 * @author Alexandre Lagane - Linagora
 */
public interface ModelDeployer {

    /**
     * Connection timeout in milliseconds to get model or artifacts from URL.
     */
    public static final int CONNECTION_TIMEOUT = 5000;

    /**
     * Reading timeout in milliseconds to read model or artifacts from URL.
     */
    public static final int READ_TIMEOUT = 5000;

    /**
     * <p>
     * Deploy the model available at the given URL.
     * </p>
     * <p>
     * Only XML resources compliant with the XSD {code model.xsd} in resources directory are supported.
     * </p>
     * 
     * @param url
     * @throws ModelParsingException
     *             An error occurs parsing or unmarshalling the resource given by the URL.
     * @throws ModelValidationException
     *             An error occurs validating the deployment model available at the URL.
     * @throws ModelDeploymentExecutionException
     *             An error occurs deploying the deployment model available at the URL.
     */
    public default void deployModel(@NotNull final URL url) throws ModelDeploymentException {

        final Model model = XmlModelBuilder.readModelFromUrl(url);

        deployModel(model);
    }

    /**
     * Deploy the given deployment model.
     * 
     * @param model
     *            The deployment model to deploy
     * @throws ModelValidationException
     *             An error occurs validating the deployment model available at the URL.
     * @throws ModelDeploymentExecutionException
     *             An error occurs deploying the deployment model available at the URL.
     */
    public void deployModel(@NotNull Model model) throws ModelDeploymentException;
}
