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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.ow2.petals.deployer.runtimemodel.exceptions.DuplicatedComponentException;
import org.ow2.petals.deployer.runtimemodel.exceptions.DuplicatedServiceUnitException;
import org.ow2.petals.deployer.runtimemodel.exceptions.DuplicatedSharedLibraryException;
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

    private final Map<RuntimeSharedLibrary.IdAndVersion, RuntimeSharedLibrary> sharedLibraries = new HashMap<>();

    public RuntimeContainer(@NotNull final String id, @NotNull final int port, @NotNull final String user,
            @NotNull final String password, @NotNull final String hostname) {
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

    @NotNull
    public String getId() {
        return id;
    }

    public int getPort() {
        return port;
    }

    @NotNull
    public String getUser() {
        return user;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    @NotNull
    public String getHostname() {
        return hostname;
    }

    public void setHostname(@NotNull final String hostname) {
        assert hostname != null;
        this.hostname = hostname;
    }

    public RuntimeServiceUnit getServiceUnit(@NotNull final String id) {
        return serviceUnits.get(id);
    }

    /**
     * @throws DuplicatedServiceUnitException
     *             Service unit is already in the list and was not added
     */
    public void addServiceUnit(@NotNull final RuntimeServiceUnit serviceUnit) throws DuplicatedServiceUnitException {
        assert serviceUnit != null;
        String id = serviceUnit.getId();
        if (serviceUnits.containsKey(id)) {
            throw new DuplicatedServiceUnitException("Service unit " + serviceUnit.getId() + " is already in the list");
        }
        serviceUnits.put(id, serviceUnit);
    }

    /**
     * Adding or removing from the returned collection does not affect the container.
     */
    @NotNull
    public Collection<RuntimeServiceUnit> getServiceUnits() {
        return Collections.unmodifiableCollection(serviceUnits.values());
    }

    /**
     * @throws DuplicatedComponentException
     *             Component is already in the list
     */
    public void addComponent(@NotNull final RuntimeComponent component) throws DuplicatedComponentException {
        assert component != null;
        if (components.put(component.getId(), component) != null) {
            throw new DuplicatedComponentException("Component " + component.getId() + " is already in the list");
        }
    }

    public RuntimeComponent getComponent(@NotNull final String id) {
        return components.get(id);
    }

    /**
     * Adding or removing from the returned collection does not affect component.
     */
    @NotNull
    public Collection<RuntimeComponent> getComponents() {
        return Collections.unmodifiableCollection(components.values());
    }

    /**
     * @return the shared library if found, else null
     */
    public RuntimeSharedLibrary getSharedLibrary(@NotNull final String id, @NotNull final String version) {
        return sharedLibraries.get(new RuntimeSharedLibrary.IdAndVersion(id, version));
    }

    /**
     * @throws DuplicatedSharedLibraryException
     *             shared library is already in the list and was not added
     */
    public void addSharedLibrary(@NotNull final RuntimeSharedLibrary sharedLibrary)
            throws DuplicatedSharedLibraryException {
        assert sharedLibrary != null;
        String id = sharedLibrary.getId();
        String version = sharedLibrary.getVersion();
        RuntimeSharedLibrary.IdAndVersion idAndVersion = new RuntimeSharedLibrary.IdAndVersion(id, version);
        if (sharedLibraries.containsKey(idAndVersion)) {
            throw new DuplicatedSharedLibraryException(
                    "Shared library with id " + id + " and version " + version + " is already in the list");
        }
        sharedLibraries.put(idAndVersion, sharedLibrary);
    }

    /**
     * Adding or removing from the returned collection does not affect container.
     */
    @NotNull
    public Collection<RuntimeSharedLibrary> getSharedLibraries() {
        return Collections.unmodifiableCollection(sharedLibraries.values());
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
                && compareRuntimeServiceUnitMaps(otherContainer) && compareRuntimeComponentMaps(otherContainer)
                && compareRuntimeSharedLibraryMaps(otherContainer);
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

    private boolean compareRuntimeSharedLibraryMaps(final RuntimeContainer otherCont) {
        for (final RuntimeSharedLibrary thisContSl : this.getSharedLibraries()) {
            final RuntimeSharedLibrary otherContSl = otherCont.getSharedLibrary(thisContSl.getId(),
                    thisContSl.getVersion());
            if (otherContSl == null || !otherContSl.isSimilarTo(otherContSl)) {
                return false;
            }
        }

        for (final RuntimeSharedLibrary otherContSl : otherCont.getSharedLibraries()) {
            if (this.getSharedLibrary(otherContSl.getId(), otherContSl.getVersion()) == null) {
                return false;
            }
        }

        return true;
    }
}
