/**
 * Copyright (c) 2018-2023 Linagora
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
import org.ow2.petals.deployer.utils.ModelDeployer;
import org.ow2.petals.deployer.utils.exceptions.ModelValidationException;
import org.ow2.petals.jbi.descriptor.JBIDescriptorException;
import org.ow2.petals.jbi.descriptor.original.JBIDescriptorBuilder;
import org.ow2.petals.jbi.descriptor.original.generated.Jbi;

/**
 * @author Alexandre Lagane - Linagora
 */
public class RuntimeSharedLibrary implements Similar {

    private final String id;

    private final String version;

    private URL url;

    private transient Jbi jbiDescriptor = null;

    private transient Object jbiDescriptorLock = new Object();

    public RuntimeSharedLibrary(@NotNull final String id, @NotNull final String version) {
        assert id != null;
        assert version != null;
        this.id = id;
        this.version = version;
    }

    public RuntimeSharedLibrary(@NotNull final String id, @NotNull final String version, final URL url) {
        this(id, version);
        this.url = url;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getVersion() {
        return version;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(final URL url) {
        this.url = url;
    }

    public Jbi getJbiDescriptor() throws ModelValidationException {
        synchronized (this.jbiDescriptorLock) {
            if (this.jbiDescriptor == null) {
                try {
                    this.jbiDescriptor = JBIDescriptorBuilder.getInstance().buildJavaJBIDescriptorFromArchive(this.url,
                            ModelDeployer.CONNECTION_TIMEOUT, ModelDeployer.READ_TIMEOUT);
                } catch (final JBIDescriptorException e) {
                    throw new ModelValidationException(String.format(
                            "Unable to build the JBI descriptor of the shared library '%s' whith version '%s' from the ZIP archive located at '%s'",
                            this.id, this.version, this.url), e);
                }
            }
            return this.jbiDescriptor;
        }
    }

    @Override
    public boolean isSimilarTo(Object o) {
        RuntimeSharedLibrary rsl = ((RuntimeSharedLibrary) o);
        return o instanceof RuntimeSharedLibrary && this.getId().equals(rsl.getId())
                && this.getVersion().equals(rsl.getVersion());
    }

    public static class IdAndVersion {
        private final String id;

        private final String version;

        public IdAndVersion(@NotNull final String id, @NotNull final String version) {
            this.id = id;
            this.version = version;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            result = prime * result + ((version == null) ? 0 : version.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            IdAndVersion other = (IdAndVersion) obj;
            if (id == null) {
                if (other.id != null)
                    return false;
            } else if (!id.equals(other.id))
                return false;
            if (version == null) {
                if (other.version != null)
                    return false;
            } else if (!version.equals(other.version))
                return false;
            return true;
        }
    }
}
