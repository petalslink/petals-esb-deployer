/**
 * Copyright (c) 2019-2026 Linagora
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

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Alexandre Lagane - Linagora
 */
public class ModelDeployerFactoryMockTest extends ModelDeployerFactoryMock {

    @Test
    public void test() throws Exception {
        final String factoryClassName = ModelDeployerFactory.class.getName();
        final String factoryMockClassName = ModelDeployerFactoryMock.class.getName();

        restoreSystemProperties(() -> {
            System.setProperty(factoryClassName, factoryMockClassName);
            assertEquals(factoryMockClassName, ModelDeployerFactory.getInstance().getClass().getName());
        });
    }
}
