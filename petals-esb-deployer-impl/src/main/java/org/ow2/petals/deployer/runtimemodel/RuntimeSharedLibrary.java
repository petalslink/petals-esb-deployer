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

import org.ow2.petals.deployer.runtimemodel.interfaces.Similar;

/**
 * @author Alexandre Lagane - Linagora
 */
public class RuntimeSharedLibrary implements Similar {

    private final String id;

    private final String version;

    private URL url;

    /**
     * 
     * @param id
     *            must not be {code null}
     * @param version
     *            must not be {code null}
     */
    public RuntimeSharedLibrary(final String id, final String version) {
        assert !id.contains(":") && !version.contains(":");
        this.id = id;
        this.version = version;
    }

    /**
     * 
     * @param id
     *            must not be {code null}
     * @param version
     *            must not be {code null}
     * @param url
     */
    public RuntimeSharedLibrary(final String id, final String version, final URL url) {
        this(id, version);
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(final URL url) {
        this.url = url;
    }

    @Override
    public boolean isSimilarTo(Object o) {
        RuntimeSharedLibrary rsl = ((RuntimeSharedLibrary) o);
        return o instanceof RuntimeSharedLibrary && this.getId().equals(rsl.getId())
                && this.getVersion().equals(rsl.getVersion());
    }
}
