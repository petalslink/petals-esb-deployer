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

/**
 * @author Alexandre Lagane - Linagora
 */
public class RuntimeServiceUnit {
    private final String id;

    private URL url;

    /**
     * 
     * @param id
     *            must not be {code null}
     */
    public RuntimeServiceUnit(final String id) {
        this.id = id;
    }

    /**
     * 
     * @param id
     *            must not be {code null}
     */
    public RuntimeServiceUnit(final String id, final URL url) {
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

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof RuntimeServiceUnit && id.equals(((RuntimeServiceUnit) obj).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
