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
package org.ow2.petals.deployer.utils.exceptions;

/**
 * A service is duplicated.
 * 
 * @author Alexandre Lagane - Linagora
 */
public class DuplicatedServiceException extends RuntimeException {

    private static final long serialVersionUID = -4120278224118043642L;

    /**
     * The duplicated service.
     */
    private final Class<?> service;

    public DuplicatedServiceException(final Class<?> service) {
        super("Duplicated service: " + service.getName());
        this.service = service;
    }

    /**
     * @return The duplicated service.
     */
    public final Class<?> getService() {
        return this.service;
    }
}
