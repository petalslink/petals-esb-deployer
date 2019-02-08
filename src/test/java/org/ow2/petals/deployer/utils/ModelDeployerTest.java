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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.net.URL;

import org.junit.Test;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel;

public class ModelDeployerTest {

    private RuntimeModelDeployer runtimeModelDeployerMock = mock(RuntimeModelDeployer.class);

    private ModelDeployer modelDeployer = new ModelDeployer(runtimeModelDeployerMock);

    @Test
    public void deployModelFile() throws Exception {
        URL modelUrl = Thread.currentThread().getContextClassLoader().getResource("model.xml").toURI().toURL();
        modelDeployer.deployModel(modelUrl);

        verify(runtimeModelDeployerMock).deployRuntimeModel(any(RuntimeModel.class));
    }

    @Test
    public void deployModel() throws Exception {
        modelDeployer.deployModel(ModelUtils.generateTestModel());

        verify(runtimeModelDeployerMock).deployRuntimeModel(any(RuntimeModel.class));
    }
}