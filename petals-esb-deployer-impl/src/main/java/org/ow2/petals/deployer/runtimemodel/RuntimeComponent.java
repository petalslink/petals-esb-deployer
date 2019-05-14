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

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.ow2.petals.deployer.runtimemodel.exceptions.DuplicatedSharedLibraryException;
import org.ow2.petals.deployer.runtimemodel.interfaces.Similar;

/**
 * @author Alexandre Lagane - Linagora
 */
public class RuntimeComponent implements Similar {

    private final String id;

    private URL url;

    private Map<String, RuntimeSharedLibrary> sharedLibraries = new HashMap<>();

    /**
     * 
     * @param id
     *            must not be {code null}
     */
    public RuntimeComponent(final String id) {
        this.id = id;
    }

    /**
     * 
     * @param id
     *            must not be {code null}
     * @param url
     */
    public RuntimeComponent(final String id, final URL url) {
        this(id);
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(final URL url) {
        this.url = url;
    }

    /**
     * Return the RuntimeSharedLibrary with id and version from idAndVersion as <code>id:version</code>, or {code null}
     * if not in the container.
     * 
     * @param idAndVersion
     *            id:version
     * @return the shared library if found, else null
     */
    public RuntimeSharedLibrary getSharedLibrary(final String idAndVersion) {
        return sharedLibraries.get(idAndVersion);
    }

    /**
     * @param sharedLibrary
     *            must not be {code null}
     * @throws DuplicatedSharedLibraryException
     *             shared library is already in the list and was not added
     */
    public void addSharedLibrary(final RuntimeSharedLibrary sharedLibrary) throws DuplicatedSharedLibraryException {
        assert sharedLibrary != null;
        String idAndVersion = sharedLibrary.getId() + ":" + sharedLibrary.getVersion();
        if (sharedLibraries.containsKey(idAndVersion)) {
            throw new DuplicatedSharedLibraryException("Shared library " + idAndVersion + " is already in the list");
        }
        sharedLibraries.put(idAndVersion, sharedLibrary);
    }

    /**
     * Adding or removing from the returned collection does not affect component.
     */
    public Collection<RuntimeSharedLibrary> getSharedLibraries() {
        return new HashSet<>(sharedLibraries.values());
    }

    @Override
    public boolean isSimilarTo(Object o) {
        if (!(o instanceof RuntimeComponent)) {
            return false;
        }
        RuntimeComponent otherComp = (RuntimeComponent) o;

        return this.getId().equals(otherComp.getId()) && compareRuntimeSharedLibraryMaps(otherComp);
    }

    private boolean compareRuntimeSharedLibraryMaps(final RuntimeComponent otherComp) {
        for (final RuntimeSharedLibrary thisCompSl : this.getSharedLibraries()) {
            final RuntimeSharedLibrary otherCompSl = otherComp
                    .getSharedLibrary(thisCompSl.getId() + ":" + thisCompSl.getVersion());
            if (otherCompSl == null || !thisCompSl.isSimilarTo(otherCompSl)) {
                return false;
            }
        }

        for (final RuntimeSharedLibrary otherCompSl : otherComp.getSharedLibraries()) {
            if (this.getSharedLibrary(otherCompSl.getId() + ":" + otherCompSl.getVersion()) == null) {
                return false;
            }
        }

        return true;
    }
}
