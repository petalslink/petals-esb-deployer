/**
 * Copyright (c) 2019-2020 Linagora
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

import javax.validation.constraints.NotNull;

import org.ow2.petals.deployer.model.xml._1.Model;
import org.ow2.petals.deployer.utils.exceptions.ModelDeploymentException;

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
     * Download and deploy the model at the url. The model must be an XML model with XSD {code model.xsd} in resources
     * directory.
     * 
     * @param url
     * @throws ModelDeploymentException
     */
    public void deployModel(@NotNull final URL url) throws ModelDeploymentException;

    public void deployModel(@NotNull Model model) throws ModelDeploymentException;
}
