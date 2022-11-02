/**
 * Copyright (c) 2019-2022 Linagora
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

import javax.validation.constraints.NotNull;

import org.ow2.petals.deployer.model.xml._1.Model;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel;
import org.ow2.petals.deployer.utils.exceptions.ModelDeploymentException;
import org.ow2.petals.deployer.utils.exceptions.ModelDeploymentExecutionException;

/**
 * The main class used for deploying XML models.
 * 
 * @author Alexandre Lagane - Linagora
 */
public class ModelDeployerImpl implements ModelDeployer {

    private final static Logger LOG = Logger.getLogger(ModelDeployerImpl.class.getName());

    private final RuntimeModelDeployer runtimeModelDeployer;

    private static ModelDeployerImpl instance;

    public ModelDeployerImpl() {
        this(null);
    }

    /**
     * Used only for testing purposes, to mock runtimeModelDeployer.
     * 
     * @param runtimeModelDeployer
     */
    protected ModelDeployerImpl(final RuntimeModelDeployer runtimeModelDeployer) {
        this.runtimeModelDeployer = runtimeModelDeployer != null ? runtimeModelDeployer : new RuntimeModelDeployer();
        instance = this;
    }

    @Override
    public void deployModel(@NotNull Model model) throws ModelDeploymentException {
        RuntimeModel runtimeModel;
        try {
            runtimeModel = ModelConverter.convertModelToRuntimeModel(model);

            runtimeModelDeployer.deployRuntimeModel(runtimeModel);
        } catch (Exception e) {
            throw new ModelDeploymentExecutionException(e);
        }

    }

    @NotNull
    public static ModelDeployerImpl getInstance() {
        return instance != null ? instance : new ModelDeployerImpl();
    }
}
