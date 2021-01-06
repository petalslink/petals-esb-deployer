/**
 * Copyright (c) 2019-2021 Linagora
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

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.ow2.petals.deployer.utils.exceptions.DuplicatedServiceException;
import org.ow2.petals.deployer.utils.exceptions.MissingServiceException;

import com.ebmwebsourcing.easycommons.lang.reflect.ReflectionHelper;

/**
 * @author Alexandre Lagane - Linagora
 *
 */
public abstract class ModelDeployerFactory {
    /**
     * Name of the property used to select the implementation of Petals ESB Deployer.
     */
    public final static String PROPERTY_NAME = ModelDeployerFactory.class.getName();

    /**
     * Lock to initialize {@link #API_INSTANCE}
     */
    private final static Object INITIALIZATION_LOCK = new Object();

    private static ModelDeployerFactory API_INSTANCE = null;

    /**
     * <p>
     * Create a new instance of Petals ESB Deployer Factory using an implementation defined according to the following
     * rule:
     * </p>
     * <ul>
     * <li>first, if the property {@link #PROPERTY_NAME} is set, its value is used to look for the service
     * implementation,</li>
     * <li>otherwise the service implementation is looking for in the current classloader.</li>
     * </ul>
     * 
     * @throws DuplicatedServiceException
     *             The service to look for is duplicated.
     * @throws MissingServiceException
     *             Unable to find the service.
     */
    public static final ModelDeployerFactory getInstance()
            throws DuplicatedServiceException, MissingServiceException {
        synchronized (INITIALIZATION_LOCK) {
            if (API_INSTANCE == null) {
                final String overridenImplementation = System.getProperty(PROPERTY_NAME);
                if (overridenImplementation == null || overridenImplementation.trim().isEmpty()) {
                    final ServiceLoader<ModelDeployerFactory> serviceLoader = ServiceLoader
                            .load(ModelDeployerFactory.class);
                    final Iterator<ModelDeployerFactory> servicesIterator = serviceLoader.iterator();
                    try {
                        if (!servicesIterator.hasNext()) {
                            throw new MissingServiceException(ModelDeployerFactory.class);
                        }
                        final ModelDeployerFactory serviceLoaded = servicesIterator.next();
                        if (servicesIterator.hasNext()) {
                            throw new DuplicatedServiceException(ModelDeployerFactory.class);
                        }
                        API_INSTANCE = serviceLoaded;
                    } catch (final ServiceConfigurationError e) {
                        throw new MissingServiceException(ModelDeployerFactory.class, e);
                    }
                } else {
                    try {
                        API_INSTANCE = ReflectionHelper.newInstance(overridenImplementation, null);
                    } catch (final InvocationTargetException e) {
                        throw new MissingServiceException(ModelDeployerFactory.class, e);
                    }
                }
            }

            return API_INSTANCE;
        }
    }

    /**
     * Free resources allocated by the Petals ESB Deployer instance
     */
    public static final void close() {
        synchronized (INITIALIZATION_LOCK) {
            API_INSTANCE = null;
        }
    }

    /**
     * Create a new instance of the Petals ESB Deployer
     * 
     * @return a new instance of Petals ESB Deployer.
     */
    public abstract ModelDeployer getModelDeployer();
}
