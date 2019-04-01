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

import org.ow2.petals.deployer.runtimemodel.exceptions.DuplicatedComponentException;
import org.ow2.petals.deployer.runtimemodel.exceptions.DuplicatedServiceUnitException;
import org.ow2.petals.deployer.runtimemodel.interfaces.Similar;

/**
 * @author Alexandre Lagane - Linagora
 */
public class RuntimeContainer implements Similar {

    private final String id;

    private int port;

    private String user;

    private String password;

    private String hostname;

    private final Map<String, RuntimeServiceUnit> serviceUnits = new HashMap<>();

    private final Map<String, RuntimeComponent> components = new HashMap<>();

    /**
     * 
     * @param id
     *            must not be {code null}
     * @param port
     * @param user
     *            must not be {code null}
     * @param password
     *            must not be {code null}
     * @param hostname
     *            must not be {code null}
     */
    public RuntimeContainer(final String id, final int port, final String user, final String password,
            final String hostname) {
        assert id != null;
        assert user != null;
        assert password != null;
        assert hostname != null;
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

    /**
     * 
     * @param hostname
     *            must not be {code null}
     */
    public void setHostname(final String hostname) {
        assert hostname != null;
        this.hostname = hostname;
    }

    public RuntimeServiceUnit getServiceUnit(final String id) {
        return serviceUnits.get(id);
    }

    /**
     * 
     * @param serviceUnit
     *            must not be {code null}
     * @throws DuplicatedServiceUnitException
     *             Service unit is already in the list
     */
    public void addServiceUnit(final RuntimeServiceUnit serviceUnit) throws DuplicatedServiceUnitException {
        assert serviceUnit != null;
        if (serviceUnits.put(serviceUnit.getId(), serviceUnit) != null) {
            throw new DuplicatedServiceUnitException("Service unit " + serviceUnit.getId() + " is already in the list");
        }
    }

    public Collection<RuntimeServiceUnit> getServiceUnits() {
        return serviceUnits.values();
    }

    /**
     * 
     * @param component
     *            must not be {code null}
     * @throws DuplicatedComponentException
     *             Component is already in the list
     */
    public void addComponent(final RuntimeComponent component) throws DuplicatedComponentException {
        assert component != null;
        if (components.put(component.getId(), component) != null) {
            throw new DuplicatedComponentException("Component " + component.getId() + " is already in the list");
        }
    }

    public RuntimeComponent getComponent(final String id) {
        return components.get(id);
    }

    public Collection<RuntimeComponent> getComponents() {
        return components.values();
    }

    @Override
    public boolean isSimilarTo(Object o) {
        if (!(o instanceof RuntimeContainer)) {
            return false;
        }
        RuntimeContainer otherContainer = (RuntimeContainer) o;

        return this.getId().equals(otherContainer.getId()) && this.getPort() == otherContainer.getPort()
                && this.getUser().equals(otherContainer.getUser())
                && this.getPassword().equals(otherContainer.getPassword())
                && this.getHostname().equals(otherContainer.getHostname())
                && compareRuntimeServiceUnitMaps(otherContainer) && compareRuntimeComponentMaps(otherContainer);
    }

    private boolean compareRuntimeServiceUnitMaps(final RuntimeContainer otherCont) {
        for (final RuntimeServiceUnit thisContSu : this.getServiceUnits()) {
            final RuntimeServiceUnit otherContSu = otherCont.getServiceUnit(thisContSu.getId());
            if (thisContSu == null || !thisContSu.isSimilarTo(otherContSu)) {
                return false;
            }
        }

        for (final RuntimeServiceUnit otherContSu : otherCont.getServiceUnits()) {
            if (this.getServiceUnit(otherContSu.getId()) == null) {
                return false;
            }
        }

        return true;
    }

    private boolean compareRuntimeComponentMaps(final RuntimeContainer otherCont) {
        for (final RuntimeComponent thisContComp : this.getComponents()) {
            final RuntimeComponent otherContComp = otherCont.getComponent(thisContComp.getId());
            if (otherContComp == null || !thisContComp.isSimilarTo(otherContComp)) {
                return false;
            }
        }

        for (final RuntimeComponent otherContComp : otherCont.getComponents()) {
            if (this.getComponent(otherContComp.getId()) == null) {
                return false;
            }
        }

        return true;
    }
}
