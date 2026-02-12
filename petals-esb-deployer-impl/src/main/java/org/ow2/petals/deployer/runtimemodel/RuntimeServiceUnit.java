/**
 * Copyright (c) 2018-2026 Linagora
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
public class RuntimeServiceUnit implements Similar {

    private final String id;

    private final Map<String, String> placholders;

    private URL url;

    private transient Jbi jbiDescriptor = null;

    private transient Object jbiDescriptorLock = new Object();

    public RuntimeServiceUnit(@NotNull final String id) {
        assert id != null;
        this.id = id;
        this.placholders = new HashMap<>();
    }

    public RuntimeServiceUnit(@NotNull final String id, final URL url) {
        this(id);
        this.url = url;
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    public URL getUrl() {
        return this.url;
    }

    public void setUrl(final URL url) {
        this.url = url;
    }

    /**
     * @return the placeholder value if set, else null
     */
    public String getPlaceholderValue(@NotNull final String name) {
        return this.placholders.get(name);
    }

    public void setPlaceholderValue(@NotNull final String name, @NotNull final String value) {
        assert name != null && value != null;
        this.placholders.put(name, value);
    }

    /**
     * Adding or removing from the returned map does not affect the service unit.
     */
    @NotNull
    public Map<String, String> getPlaceholders() {
        return Collections.unmodifiableMap(this.placholders);
    }

    public Jbi getJbiDescriptor() throws ModelValidationException {
        synchronized (this.jbiDescriptorLock) {
            if (this.jbiDescriptor == null) {
                try {
                    this.jbiDescriptor = JBIDescriptorBuilder.getInstance().buildJavaJBIDescriptorFromArchive(this.url,
                            ModelDeployer.CONNECTION_TIMEOUT, ModelDeployer.READ_TIMEOUT);
                } catch (final JBIDescriptorException e) {
                    throw new ModelValidationException(String.format(
                            "Unable to build the JBI descriptor of the service unit '%s' from the ZIP archive located at '%s'",
                            this.id, this.url));
                }
            }
            return this.jbiDescriptor;
        }
    }

    @Override
    public boolean isSimilarTo(Object o) {
        if (!(o instanceof RuntimeServiceUnit)) {
            return false;
        }
        final RuntimeServiceUnit otherSu = (RuntimeServiceUnit) o;

        return this.getId().equals(otherSu.getId()) && this.getPlaceholders().equals(otherSu.getPlaceholders());
    }
}
