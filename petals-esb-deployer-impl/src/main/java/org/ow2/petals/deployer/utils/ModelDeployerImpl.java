/**
 * Copyright (c) 2019-2024 Linagora
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

import java.util.logging.Logger;

import org.ow2.petals.deployer.model.xml._1.Model;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel;
import org.ow2.petals.deployer.utils.exceptions.ModelDeploymentException;

import jakarta.validation.constraints.NotNull;

/**
 * The main class used for deploying XML models.
 * 
 * @author Alexandre Lagane - Linagora
 */
public class ModelDeployerImpl implements ModelDeployer {

    private final static Logger LOG = Logger.getLogger(ModelDeployerImpl.class.getName());

    private final RuntimeModelDeployer runtimeModelDeployer;

    /**
     * @param runtimeModelDeployer
     */
    protected ModelDeployerImpl(final RuntimeModelDeployer runtimeModelDeployer) {
        this.runtimeModelDeployer = runtimeModelDeployer;
    }

    @Override
    public void deployModel(@NotNull final Model model) throws ModelDeploymentException {

        final RuntimeModel runtimeModel = ModelConverter.convertModelToRuntimeModel(model);
        this.runtimeModelDeployer.deployRuntimeModel(runtimeModel);
    }
}
