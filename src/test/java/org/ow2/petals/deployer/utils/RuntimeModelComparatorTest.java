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
        RuntimeModel model = ModelUtils.generateTestRuntimeModel();

        RuntimeModel equivalentModel = ModelUtils.generateTestRuntimeModel();

        assertTrue(RuntimeModelComparator.compareRuntimeModels(model, equivalentModel));
        assertTrue(RuntimeModelComparator.compareRuntimeModels(equivalentModel, model));
    }

    @Test
    public void differentServiceUnitListsRuntimeModels() throws Exception {
        RuntimeModel model = ModelUtils.generateTestRuntimeModel();

        RuntimeModel modelWithDifferentServiceUnitList = ModelUtils.generateTestRuntimeModel();
        modelWithDifferentServiceUnitList.getContainers().iterator().next()
                .addServiceUnit(new RuntimeServiceUnit("my-test-su", new URL("file:/artifact/my-test-su.zip")));

        assertFalse(RuntimeModelComparator.compareRuntimeModels(model, modelWithDifferentServiceUnitList));
        assertFalse(RuntimeModelComparator.compareRuntimeModels(modelWithDifferentServiceUnitList, model));
    }

    @Test
    public void differentComponentListsRuntimeModels() throws Exception {
        RuntimeModel model = ModelUtils.generateTestRuntimeModel();

        RuntimeModel modelWithDifferentComponentList = ModelUtils.generateTestRuntimeModel();
        modelWithDifferentComponentList.getContainers().iterator().next()
                .addComponent(new RuntimeComponent("my-test-comp", new URL("file:/artifact/my-test-comp.zip")));

        assertFalse(RuntimeModelComparator.compareRuntimeModels(model, modelWithDifferentComponentList));
        assertFalse(RuntimeModelComparator.compareRuntimeModels(modelWithDifferentComponentList, model));
    }

    @Test
    public void differentContainersRuntimeModels() throws Exception {
        RuntimeModel model = ModelUtils.generateTestRuntimeModel();

        RuntimeModel modelWithDifferentContainer = ModelUtils.generateTestRuntimeModel();
        modelWithDifferentContainer.getContainers().clear();
        RuntimeContainer differentCont = new RuntimeContainer("different-cont", 7700, "other", "azerty", "localhost");
        modelWithDifferentContainer.addContainer(differentCont);

        assertFalse(RuntimeModelComparator.compareRuntimeModels(model, modelWithDifferentContainer));
        assertFalse(RuntimeModelComparator.compareRuntimeModels(modelWithDifferentContainer, model));
    }
}
