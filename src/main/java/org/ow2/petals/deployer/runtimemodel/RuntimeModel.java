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

public class RuntimeModel {

    private Map<String, RuntimeContainer> containers = new HashMap<String, RuntimeContainer>();

    public void addContainer(final RuntimeContainer container) throws RuntimeModelException {
        if (containers.put(container.getId(), container) != null) {
            throw new RuntimeModelException("Container " + container.getId() + " is already in the list");
        }
    }

    public RuntimeContainer getContainer(final String id) {
        return containers.get(id);
    }

    public Collection<RuntimeContainer> getContainers() {
        return containers.values();
    }

    public static class RuntimeModelException extends Exception {
        private static final long serialVersionUID = -4775138643228668670L;

        public RuntimeModelException(String message) {
            super(message);
        }
    }
}
