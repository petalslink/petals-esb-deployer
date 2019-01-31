/**
 * Copyright (c) 2018-2019 Linagora
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
import org.ow2.petals.deployer.runtimemodel.RuntimeComponent;
import org.ow2.petals.deployer.runtimemodel.RuntimeContainer;
import org.ow2.petals.deployer.runtimemodel.RuntimeModel;
import org.ow2.petals.deployer.runtimemodel.RuntimeServiceUnit;
import org.ow2.petals.jbi.descriptor.original.JBIDescriptorBuilder;

/**
 * @author Alexandre Lagane - Linagora
 */
public class RuntimeModelDeployerTest {

    @Rule
    public PetalsAdministrationApi petalsAdminApiRule = new PetalsAdministrationApi();

    @BeforeClass
    public static void setupLogger() throws Exception {
        final Logger deployerLogger = Logger.getLogger(RuntimeModelDeployer.class.getName());
        deployerLogger.setLevel(Level.FINER);
        final Logger comparatorLogger = Logger.getLogger(RuntimeModelComparator.class.getName());
        comparatorLogger.setLevel(Level.FINER);
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
        comparatorLogger.addHandler(consoleHandler);
    }

    @Test
    public void deployDemoSoap() throws Exception {
        RuntimeModel model = new RuntimeModel();
        initializeRuntimeModel(model);

        petalsAdminApiRule.registerDomain();
        org.ow2.petals.admin.topology.Container cont = createContainerSample();
        petalsAdminApiRule.registerContainer(cont);
        ArtifactLifecycleFactoryMock artifactLifecycleFactoryMock = new ArtifactLifecycleFactoryMock(cont);
        RuntimeModelDeployer modelDeployer = new RuntimeModelDeployer(petalsAdminApiRule.getSingleton(),
                artifactLifecycleFactoryMock, JBIDescriptorBuilder.getInstance());
        modelDeployer.deployRuntimeModel(model);

        RuntimeModelExporter modelExporter = new RuntimeModelExporter();
        RuntimeModel exportedModel = modelExporter.exportRuntimeModel(ModelUtils.CONTAINER_HOST,
                ModelUtils.CONTAINER_JMX_PORT, ModelUtils.CONTAINER_USER, ModelUtils.CONTAINER_PWD, null);

        assertTrue(RuntimeModelComparator.compareRuntimeModels(model, exportedModel));
    }

    private org.ow2.petals.admin.topology.Container createContainerSample() {
        final Map<PortType, Integer> ports = new HashMap<>();
        ports.put(PortType.JMX, ModelUtils.CONTAINER_JMX_PORT);
        return new org.ow2.petals.admin.topology.Container(ModelUtils.CONTAINER_NAME, ModelUtils.CONTAINER_HOST, ports,
                ModelUtils.CONTAINER_USER, ModelUtils.CONTAINER_PWD, ModelUtils.CONTAINER_STATE);
    }

    private void initializeRuntimeModel(RuntimeModel model) throws Exception {

        RuntimeContainer cont = new RuntimeContainer(ModelUtils.CONTAINER_NAME, ModelUtils.CONTAINER_JMX_PORT,
                ModelUtils.CONTAINER_USER, ModelUtils.CONTAINER_PWD, "localhost");
        cont.addComponent(new RuntimeComponent("petals-bc-soap",
                ZipUtils.createZipFromResourceDirectory("artifacts/petals-bc-soap-5.0.0").toURI().toURL()));
        cont.addServiceUnit(new RuntimeServiceUnit("su-SOAP-Hello_Service1-provide",
                ZipUtils.createZipFromResourceDirectory("artifacts/sa-SOAP-Hello_Service1-provide").toURI().toURL()));
        cont.addServiceUnit(new RuntimeServiceUnit("su-SOAP-Hello_Service2-provide",
                ZipUtils.createZipFromResourceDirectory("artifacts/sa-SOAP-Hello_Service2-provide").toURI().toURL()));
        cont.addServiceUnit(new RuntimeServiceUnit("su-SOAP-Hello_PortType-consume",
                ZipUtils.createZipFromResourceDirectory("artifacts/sa-SOAP-Hello_PortType-consume").toURI().toURL()));

        model.addContainer(cont);
    }
}
