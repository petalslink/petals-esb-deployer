/**
 * Copyright (c) 2018-2020 Linagora
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

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.ow2.petals.admin.junit.ArtifactLifecycleFactoryMock;
import org.ow2.petals.admin.junit.PetalsAdministrationApi;
import org.ow2.petals.admin.topology.Container.PortType;
import org.ow2.petals.admin.topology.Container.State;
import org.ow2.petals.deployer.runtimemodel.RuntimeComponent;
import org.ow2.petals.deployer.runtimemodel.RuntimeContainer;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel;
import org.ow2.petals.deployer.runtimemodel.RuntimeServiceUnit;
import org.ow2.petals.deployer.runtimemodel.RuntimeSharedLibrary;

/**
 * @author Alexandre Lagane - Linagora
 */
public class RuntimeModelDeployerTest {

    final public static State CONTAINER_STATE = State.REACHABLE;

    @Rule
    public PetalsAdministrationApi petalsAdminApiRule = new PetalsAdministrationApi();

    @BeforeClass
    public static void setupLogger() throws Exception {
        final Logger deployerLogger = Logger.getLogger(RuntimeModelDeployer.class.getName());
        deployerLogger.setLevel(Level.FINER);
        final Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINER);
        final Formatter formatter = new Formatter() {
            @Override
            public String format(final LogRecord record) {
                return record.getMessage() + "\n";
            }
        };
        consoleHandler.setFormatter(formatter);
        deployerLogger.addHandler(consoleHandler);
    }

    @Test
    public void deployRuntimeModel() throws Exception {
        petalsAdminApiRule.registerDomain();
        org.ow2.petals.admin.topology.Container cont = createContainerSample();
        petalsAdminApiRule.registerContainer(cont);
        final ArtifactLifecycleFactoryMock artifactLifecycleFactoryMock = new ArtifactLifecycleFactoryMock(cont);
        final RuntimeModelDeployer modelDeployer = new RuntimeModelDeployer(petalsAdminApiRule.getSingleton(),
                artifactLifecycleFactoryMock);

        final RuntimeModel model = generateRuntimeModel();
        modelDeployer.deployRuntimeModel(model);

        final RuntimeModelExporter modelExporter = new RuntimeModelExporter(petalsAdminApiRule.getSingleton());
        final RuntimeModel exportedModel = modelExporter.exportRuntimeModel(ModelUtils.CONTAINER_HOST,
                ModelUtils.CONTAINER_JMX_PORT, ModelUtils.CONTAINER_USER, ModelUtils.CONTAINER_PWD, null);

        assertTrue(model.isSimilarTo(exportedModel));
    }

    public static org.ow2.petals.admin.topology.Container createContainerSample() {
        final Map<PortType, Integer> ports = new HashMap<>();
        ports.put(PortType.JMX, ModelUtils.CONTAINER_JMX_PORT);
        return new org.ow2.petals.admin.topology.Container(ModelUtils.CONTAINER_NAME, ModelUtils.CONTAINER_HOST, ports,
                ModelUtils.CONTAINER_USER, ModelUtils.CONTAINER_PWD, CONTAINER_STATE);
    }

    @Test
    public void deployRuntimeModelWithSharedLibraries() throws Exception {
        petalsAdminApiRule.registerDomain();
        org.ow2.petals.admin.topology.Container cont = createContainerSample();
        petalsAdminApiRule.registerContainer(cont);
        final ArtifactLifecycleFactoryMock artifactLifecycleFactoryMock = new ArtifactLifecycleFactoryMock(cont);
        final RuntimeModelDeployer modelDeployer = new RuntimeModelDeployer(petalsAdminApiRule.getSingleton(),
                artifactLifecycleFactoryMock);

        final RuntimeModel model = generateRuntimeModelWithSharedLibraries();
        modelDeployer.deployRuntimeModel(model);

        final RuntimeModelExporter modelExporter = new RuntimeModelExporter(petalsAdminApiRule.getSingleton());
        final RuntimeModel exportedModel = modelExporter.exportRuntimeModel(ModelUtils.CONTAINER_HOST,
                ModelUtils.CONTAINER_JMX_PORT, ModelUtils.CONTAINER_USER, ModelUtils.CONTAINER_PWD, null);

        assertTrue(model.isSimilarTo(exportedModel));
    }

    @Test
    public void deployRuntimeModelWithParameters() throws Exception {
        petalsAdminApiRule.registerDomain();
        org.ow2.petals.admin.topology.Container cont = createContainerSample();
        petalsAdminApiRule.registerContainer(cont);
        final ArtifactLifecycleFactoryMock artifactLifecycleFactoryMock = new ArtifactLifecycleFactoryMock(cont);
        final RuntimeModelDeployer modelDeployer = new RuntimeModelDeployer(petalsAdminApiRule.getSingleton(),
                artifactLifecycleFactoryMock);

        final RuntimeModel model = generateRuntimeModelWithSharedLibraries();
        modelDeployer.deployRuntimeModel(model);

        final RuntimeModelExporter modelExporter = new RuntimeModelExporter(petalsAdminApiRule.getSingleton());
        final RuntimeModel exportedModel = modelExporter.exportRuntimeModel(ModelUtils.CONTAINER_HOST,
                ModelUtils.CONTAINER_JMX_PORT, ModelUtils.CONTAINER_USER, ModelUtils.CONTAINER_PWD, null);

        assertTrue(model.isSimilarTo(exportedModel));
    }

    public static RuntimeModel generateRuntimeModel() throws Exception {
        final RuntimeModel model = new RuntimeModel();
        final RuntimeContainer cont = new RuntimeContainer(ModelUtils.CONTAINER_NAME, ModelUtils.CONTAINER_JMX_PORT,
                ModelUtils.CONTAINER_USER, ModelUtils.CONTAINER_PWD, "localhost");
        model.addContainer(cont);
        cont.addComponent(new RuntimeComponent("petals-bc-soap",
                ZipUtils.createZipFromResourceDirectory("artifacts/petals-bc-soap-5.0.0").toURI().toURL()));
        cont.addServiceUnit(new RuntimeServiceUnit("su-SOAP-Hello_Service1-provide",
                ZipUtils.createZipFromResourceDirectory("artifacts/sa-SOAP-Hello_Service1-provide").toURI().toURL()));
        cont.addServiceUnit(new RuntimeServiceUnit("su-SOAP-Hello_Service2-provide",
                ZipUtils.createZipFromResourceDirectory("artifacts/sa-SOAP-Hello_Service2-provide").toURI().toURL()));
        cont.addServiceUnit(new RuntimeServiceUnit("su-SOAP-Hello_PortType-consume",
                ZipUtils.createZipFromResourceDirectory("artifacts/sa-SOAP-Hello_PortType-consume").toURI().toURL()));

        return model;
    }

    public static RuntimeModel generateRuntimeModelWithSharedLibraries() throws Exception {
        final RuntimeModel model = new RuntimeModel();
        final RuntimeContainer container = new RuntimeContainer(ModelUtils.CONTAINER_NAME,
                ModelUtils.CONTAINER_JMX_PORT, ModelUtils.CONTAINER_USER, ModelUtils.CONTAINER_PWD, "localhost");
        model.addContainer(container);
        container.addServiceUnit(new RuntimeServiceUnit("su-SQL",
                ZipUtils.createZipFromResourceDirectory("artifacts/sa-SQL").toURI().toURL()));

        final RuntimeComponent component = new RuntimeComponent("petals-bc-sql", ZipUtils
                .createZipFromResourceDirectory("artifacts/petals-bc-sql-with-shared-libraries").toURI().toURL());
        container.addComponent(component);

        RuntimeSharedLibrary sl1 = new RuntimeSharedLibrary("petals-sl-hsql", "1.8.0.10",
                ZipUtils.createZipFromResourceDirectory("artifacts/petals-sl-hsql-1.8.0.10").toURI().toURL());
        container.addSharedLibrary(sl1);
        component.addSharedLibrary(sl1);

        RuntimeSharedLibrary sl2 = new RuntimeSharedLibrary("petals-sl-sqlserver-6.1.0.jre7", "1.0.0-SNAPSHOT",
                ZipUtils.createZipFromResourceDirectory("artifacts/petals-sl-sqlserver-6.1.0.jre7-1.0.0-SNAPSHOT")
                        .toURI().toURL());
        container.addSharedLibrary(sl2);
        component.addSharedLibrary(sl2);

        return model;
    }

    public static RuntimeModel generateRuntimeModelWithParameters() throws Exception {
        final RuntimeModel model = new RuntimeModel();
        final RuntimeContainer cont = new RuntimeContainer(ModelUtils.CONTAINER_NAME, ModelUtils.CONTAINER_JMX_PORT,
                ModelUtils.CONTAINER_USER, ModelUtils.CONTAINER_PWD, "localhost");
        model.addContainer(cont);
        RuntimeComponent comp = new RuntimeComponent("petals-bc-soap",
                ZipUtils.createZipFromResourceDirectory("artifacts/petals-bc-soap-5.0.0").toURI().toURL());
        comp.setParameterValue("param1", "value1");
        comp.setParameterValue("param2", "value2");
        cont.addComponent(comp);
        cont.addServiceUnit(new RuntimeServiceUnit("su-SOAP-Hello_PortType-consume",
                ZipUtils.createZipFromResourceDirectory("artifacts/sa-SOAP-Hello_PortType-consume").toURI().toURL()));

        return model;
    }
}
