package server.model;

import common.AccessPermissions;
import common.OperationPermissions;

public class FileDescriptor {
    private String name;
    private long size;
    private AccessPermissions accessPermissions;
    private OperationPermissions operationPermissions;
    private int owner;

    public FileDescriptor(String name, long size, AccessPermissions accessPermissions, OperationPermissions operationPermissions, int owner) {
        this.name = name;
        this.size = size;
        this.accessPermissions = accessPermissions;
        this.operationPermissions = operationPermissions;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public AccessPermissions getAccessPermissions() {
        return accessPermissions;
    }

    public void setAccessPermissions(AccessPermissions accessPermissions) {
        this.accessPermissions = accessPermissions;
    }

    public OperationPermissions getOperationPermissions() {
        return operationPermissions;
    }

    public void setOperationPermissions(OperationPermissions operationPermissions) {
        this.operationPermissions = operationPermissions;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }
}
