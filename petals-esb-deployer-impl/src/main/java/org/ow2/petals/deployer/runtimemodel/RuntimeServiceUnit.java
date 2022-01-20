/**
 * Copyright (c) 2018-2022 Linagora
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

import javax.validation.constraints.NotNull;

import org.ow2.petals.deployer.runtimemodel.interfaces.Similar;

/**
 * @author Alexandre Lagane - Linagora
 */
public class RuntimeServiceUnit implements Similar {

    private final String id;

    private URL url;

    public RuntimeServiceUnit(@NotNull final String id) {
        assert id != null;
        this.id = id;
    }

    public RuntimeServiceUnit(@NotNull final String id, final URL url) {
        this(id);
        this.url = url;
    }

    @NotNull
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
    public boolean isSimilarTo(Object o) {
        return o instanceof RuntimeServiceUnit && this.getId().equals(((RuntimeServiceUnit) o).getId());
    }
}
