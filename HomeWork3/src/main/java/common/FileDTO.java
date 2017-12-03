package common;

import java.io.Serializable;

public class FileDTO implements Serializable {
    private String name;
    private long size;
    private AccessPermissions accessPermissions;
    private OperationPermissions operationPermissions;

    public FileDTO(String name, long size, AccessPermissions accessPermissions, OperationPermissions operationPermissions, boolean norifyable) {
        this.name = name;
        this.size = size;
        this.accessPermissions = accessPermissions;
        this.operationPermissions = operationPermissions;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public AccessPermissions getAccessPermissions() {
        return accessPermissions;
    }

    public OperationPermissions getOperationPermissions() {
        return operationPermissions;
    }
}
