/**
 * Copyright (c) 2019-2024 Linagora
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

package org.ow2.petals.deployer.runtimemodel.interfaces;

/**
 * @author Alexandre Lagane - Linagora
 */
public interface Similar {

    /**
     * Return true if current object and o are similar.
     * 
     * RuntimeModel deployed to Petals, and RuntimeModel exported from the deployed Petals will return {code true} when
     * compared using isSimilarTo method. {code o1.isSimilarTo(o2)} will always yield the same result as {code
     * o2.isSimilarTo(o1)}.
     * 
     * @param o
     *            object compared to current object
     * @return true if similar
     */
    public boolean isSimilarTo(Object o);
}
