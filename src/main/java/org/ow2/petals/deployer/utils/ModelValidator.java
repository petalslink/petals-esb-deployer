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

package org.ow2.petals.deployer.utils;

public class ModelValidator {

    // public void validateServiceUnitModel(ServiceUnitModel model) throws ModelValidationException {
    //
    // List<ServiceUnit> sus = model.getServiceUnits();
    // if (sus.size() < 1) {
    // throw new ModelValidationException("ServiceUnitModel must have 1 ServiceUnit minimum");
    // }
    //
    // HashSet<String> suIds = new HashSet<String>();
    // Map<String, String> placeholders = new HashMap<String, String>();
    // for (ServiceUnit su : sus) {
    // for (Map.Entry<String, String> suPh : su.getPlaceholders().entrySet()) {
    // if (placeholders.containsKey(suPh.getKey())) {
    // String phDefaultValue = placeholders.get(suPh.getKey());
    // if (suPh.getValue() == null && phDefaultValue != null
    // || suPh.getValue() != null && !suPh.getValue().equals(phDefaultValue)) {
    // throw new ModelValidationException("Placeholder with key " + suPh.getKey()
    // + " is used on different ServiceUnit but its defaultValue differs.");
    // }
    // } else {
    // placeholders.put(suPh.getKey(), suPh.getValue());
    // }
    //
    // }
    // if (!suIds.add(su.getId())) {
    // throw new ModelValidationException("ServiceUnit id " + su.getId() + " is not unique");
    // }
    // }
    // }
}