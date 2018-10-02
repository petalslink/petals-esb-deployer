package org.ow2.petals.deploymentmodel.componentrepository;

import java.net.URI;

/**
 * Class describing a shared library of this repository.
 *
 * @author alagane
 */
public class SharedLibrary {
    /**
     * The identifier of this shared library in the model. Must be the same than the one defined in the JBI descriptor
     * of the shared library.
     */
    private String id;

    /**
     * The version of this shared library in the model. Must be the same than the one defined in the JBI descriptor of
     * the shared library.
     */
    private String version;

    /**
     * The URL of the associated archive.
     */
    private URI url;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public URI getUrl() {
        return url;
    }

    public void setUrl(final URI url) {
        this.url = url;
    }
}
