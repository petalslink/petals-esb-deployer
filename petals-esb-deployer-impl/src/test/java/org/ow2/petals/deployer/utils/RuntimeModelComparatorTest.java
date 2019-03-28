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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.junit.Test;
import org.ow2.petals.deployer.runtimemodel.RuntimeComponent;
import org.ow2.petals.deployer.runtimemodel.RuntimeContainer;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel;
import org.ow2.petals.deployer.runtimemodel.RuntimeServiceUnit;

public class RuntimeModelComparatorTest {

    @Test
    public void equivalentRuntimeModels() throws Exception {
        final RuntimeModel model = RuntimeModelDeployerTest.generateRuntimeModel();

        final RuntimeModel similarModel = RuntimeModelDeployerTest.generateRuntimeModel();

        assertTrue(model.isSimilarTo(similarModel));
        assertTrue(similarModel.isSimilarTo(model));
    }

    @Test
    public void differentServiceUnitListsRuntimeModels() throws Exception {
        final RuntimeModel model = RuntimeModelDeployerTest.generateRuntimeModel();

        final RuntimeModel modelWithDifferentServiceUnitList = RuntimeModelDeployerTest.generateRuntimeModel();
        modelWithDifferentServiceUnitList.getContainers().iterator().next()
                .addServiceUnit(new RuntimeServiceUnit("my-test-su", new URL("file:/artifact/my-test-su.zip")));

        assertFalse(model.isSimilarTo(modelWithDifferentServiceUnitList));
        assertFalse(modelWithDifferentServiceUnitList.isSimilarTo(model));
    }

    @Test
    public void differentComponentListsRuntimeModels() throws Exception {
        final RuntimeModel model = RuntimeModelDeployerTest.generateRuntimeModel();

        final RuntimeModel modelWithDifferentComponentList = RuntimeModelDeployerTest.generateRuntimeModel();
        modelWithDifferentComponentList.getContainers().iterator().next()
                .addComponent(new RuntimeComponent("my-test-comp", new URL("file:/artifact/my-test-comp.zip")));

        assertFalse(model.isSimilarTo(modelWithDifferentComponentList));
        assertFalse(modelWithDifferentComponentList.isSimilarTo(model));
    }

    @Test
    public void differentContainersRuntimeModels() throws Exception {
        final RuntimeModel model = RuntimeModelDeployerTest.generateRuntimeModel();

        final RuntimeModel modelWithDifferentContainer = RuntimeModelDeployerTest.generateRuntimeModel();
        modelWithDifferentContainer.getContainers().clear();
        final RuntimeContainer differentCont = new RuntimeContainer("different-cont", 7700, "other", "azerty",
                "localhost");
        modelWithDifferentContainer.addContainer(differentCont);

        assertFalse(model.isSimilarTo(modelWithDifferentContainer));
        assertFalse(modelWithDifferentContainer.isSimilarTo(model));
    }
}
