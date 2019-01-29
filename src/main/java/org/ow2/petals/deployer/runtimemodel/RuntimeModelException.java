package org.ow2.petals.deployer.runtimemodel;

public class RuntimeModelException extends Exception {
    public static class DuplicatedServiceUnitException extends RuntimeModelException {
        RuntimeModelException.DuplicatedServiceUnitException e;
    }
}
