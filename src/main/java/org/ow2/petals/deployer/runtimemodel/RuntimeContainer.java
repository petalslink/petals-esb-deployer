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

package org.ow2.petals.deployer.runtimemodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ow2.petals.deployer.runtimemodel.RuntimeModel.RuntimeModelException;

/**
 * @author Alexandre Lagane - Linagora
 */
public class RuntimeContainer {

    private final String id;

    private int port;

    private String user;

    private String password;

    private String hostname;

    private Map<String, RuntimeServiceUnit> serviceUnits = new HashMap<String, RuntimeServiceUnit>();

    private Map<String, RuntimeComponent> components = new HashMap<String, RuntimeComponent>();

    public RuntimeContainer(final String id, final int port, final String user, final String password,
            final String hostname) {
        this.id = id;
        this.port = port;
        this.user = user;
        this.password = password;
        this.hostname = hostname;
    }

    public String getId() {
        return id;
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }

    public RuntimeServiceUnit getServiceUnit(final String id) {
        return serviceUnits.get(id);
    }

    public void addServiceUnit(final RuntimeServiceUnit serviceUnit) throws RuntimeModelException {
        if (serviceUnits.put(serviceUnit.getId(), serviceUnit) != null) {
            throw new RuntimeModelException("Service unit " + serviceUnit.getId() + " is already in the list");
        }
    }

    public Collection<RuntimeServiceUnit> getServiceUnits() {
        return serviceUnits.values();
    }

    public void addComponent(final RuntimeComponent component) throws RuntimeModelException {
        if (components.put(component.getId(), component) != null) {
            throw new RuntimeModelException("Component " + component.getId() + " is already in the list");
        }
    }

    public RuntimeComponent getComponent(final String id) {
        return components.get(id);
    }

    public Collection<RuntimeComponent> getComponents() {
        return components.values();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RuntimeContainer)) {
            return false;
        }

        return id.equals(((RuntimeContainer) obj).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
