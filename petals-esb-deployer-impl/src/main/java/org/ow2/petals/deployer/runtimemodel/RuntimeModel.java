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
import java.util.HashSet;
import java.util.Map;

import org.ow2.petals.deployer.runtimemodel.exceptions.DuplicatedContainerException;
import org.ow2.petals.deployer.runtimemodel.interfaces.Similar;

/**
 * @author Alexandre Lagane - Linagora
 */
public class RuntimeModel implements Similar {

    private final Map<String, RuntimeContainer> containers = new HashMap<>();

    /**
     * @param container
     *            must not be {code null}
     * @throws DuplicatedContainerException
     *             container is already in the list and was not added
     */
    public void addContainer(final RuntimeContainer container) throws DuplicatedContainerException {
        assert container != null;
        String id = container.getId();
        if (containers.containsKey(id)) {
            throw new DuplicatedContainerException("Container " + id + " is already in the list");
        }
        containers.put(id, container);
    }

    /**
     * Return the RuntimeContainer with identifier id, or {code null} if not in the model.
     * 
     * @param id
     * @return RuntimeContainer or null
     */
    public RuntimeContainer getContainer(final String id) {
        return containers.get(id);
    }

    /**
     * Adding or removing from the returned collection does not affect the model.
     */
    public Collection<RuntimeContainer> getContainers() {
        return new HashSet<>(containers.values());
    }

    @Override
    public boolean isSimilarTo(Object o) {
        if (!(o instanceof RuntimeModel)) {
            return false;
        }
        RuntimeModel otherModel = (RuntimeModel) o;

        final Collection<RuntimeContainer> thisModelContainers = this.getContainers();
        final Collection<RuntimeContainer> otherModelContainers = otherModel.getContainers();

        for (final RuntimeContainer thisModelCont : thisModelContainers) {
            final RuntimeContainer otherModelCont = otherModel.getContainer(thisModelCont.getId());
            if (otherModelCont == null || !thisModelCont.isSimilarTo(otherModelCont)) {
                return false;
            }
        }

        for (final RuntimeContainer otherModelCont : otherModelContainers) {
            if (this.getContainer(otherModelCont.getId()) == null) {
                return false;
            }
        }

        return true;
    }
}
