/**
 * Copyright (c) 2018-2024 Linagora
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.ow2.petals.deployer.runtimemodel.exceptions.DuplicatedSharedLibraryException;
import org.ow2.petals.deployer.runtimemodel.interfaces.Similar;
import org.ow2.petals.deployer.utils.ModelDeployer;
import org.ow2.petals.deployer.utils.exceptions.ModelValidationException;
import org.ow2.petals.jbi.descriptor.JBIDescriptorException;
import org.ow2.petals.jbi.descriptor.original.JBIDescriptorBuilder;
import org.ow2.petals.jbi.descriptor.original.generated.Jbi;

import jakarta.validation.constraints.NotNull;

/**
 * @author Alexandre Lagane - Linagora
 */
public class RuntimeComponent implements Similar {

    private final String id;

    private URL url;

    private final Map<String, String> parameters;

    private final Map<RuntimeSharedLibrary.IdAndVersion, RuntimeSharedLibrary> sharedLibraries = new HashMap<>();

    private transient Jbi jbiDescriptor = null;

    private transient Object jbiDescriptorLock = new Object();

    public RuntimeComponent(@NotNull final String id) {
        assert id != null;
        this.id = id;
        this.parameters = new HashMap<>();
    }

    public RuntimeComponent(@NotNull final String id, @NotNull final URL url) {
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
     * @return the parameter value if set, else null
     */
    public String getParameterValue(@NotNull final String name) {
        return parameters.get(name);
    }

    public void setParameterValue(@NotNull final String name, @NotNull final String value) {
        assert name != null && value != null;
        parameters.put(name, value);
    }

    /**
     * Adding or removing from the returned map does not affect the component.
     */
    @NotNull
    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    /**
     * @return the shared library if found, else null
     */
    public RuntimeSharedLibrary getSharedLibrary(@NotNull final String id, @NotNull final String version) {
        return this.sharedLibraries.get(new RuntimeSharedLibrary.IdAndVersion(id, version));
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
        if (this.sharedLibraries.containsKey(idAndVersion)) {
            throw new DuplicatedSharedLibraryException(
                    "Shared library with id " + id + " and version " + version + " is already in the list");
        }
        this.sharedLibraries.put(idAndVersion, sharedLibrary);
    }

    /**
     * Adding or removing from the returned collection does not affect the component.
     */
    @NotNull
    public Collection<RuntimeSharedLibrary> getSharedLibraries() {
        return Collections.unmodifiableCollection(this.sharedLibraries.values());
    }

    public Jbi getJbiDescriptor() throws ModelValidationException {
        synchronized (this.jbiDescriptorLock) {
            if (this.jbiDescriptor == null) {
                try {
                    this.jbiDescriptor = JBIDescriptorBuilder.getInstance().buildJavaJBIDescriptorFromArchive(this.url,
                            ModelDeployer.CONNECTION_TIMEOUT, ModelDeployer.READ_TIMEOUT);
                } catch (final JBIDescriptorException e) {
                    throw new ModelValidationException(String.format(
                            "Unable to build the JBI descriptor of the component '%s' from the ZIP archive located at '%s'",
                            this.id, this.url), e);
                }
            }
            return this.jbiDescriptor;
        }
    }

    @Override
    public boolean isSimilarTo(Object o) {
        if (!(o instanceof RuntimeComponent)) {
            return false;
        }
        RuntimeComponent otherComp = (RuntimeComponent) o;

        return this.getId().equals(otherComp.getId()) && this.getParameters().equals(otherComp.getParameters()) && compareRuntimeSharedLibraryMaps(otherComp);
    }

    private boolean compareRuntimeSharedLibraryMaps(final RuntimeComponent otherComp) {
        for (final RuntimeSharedLibrary thisCompSl : this.getSharedLibraries()) {
            final RuntimeSharedLibrary otherCompSl = otherComp.getSharedLibrary(thisCompSl.getId(),
                    thisCompSl.getVersion());
            if (otherCompSl == null || !thisCompSl.isSimilarTo(otherCompSl)) {
                return false;
            }
        }

        for (final RuntimeSharedLibrary otherCompSl : otherComp.getSharedLibraries()) {
            if (this.getSharedLibrary(otherCompSl.getId(), otherCompSl.getVersion()) == null) {
                return false;
            }
        }

        return true;
    }
}
