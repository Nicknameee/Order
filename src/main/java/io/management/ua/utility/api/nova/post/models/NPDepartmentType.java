package io.management.ua.utility.api.nova.post.models;

import java.util.Map;
import java.util.UUID;

public enum NPDepartmentType {
    POS_TERMINAL, SMALL_DEPARTMENT, DEPARTMENT, CARGO_DEPARTMENT;

    private static final Map<UUID, NPDepartmentType> association = Map.of(
            UUID.fromString("6f8c7162-4b72-4b0a-88e5-906948c6a92f"), SMALL_DEPARTMENT,
            UUID.fromString("f9316480-5f2d-425d-bc2c-ac7cd29decf0"), POS_TERMINAL,
            UUID.fromString("9a68df70-0267-42a8-bb5c-37f427e36ee4"), CARGO_DEPARTMENT,
            UUID.fromString("841339c7-591a-42e2-8233-7a0a00f0ed6f"), DEPARTMENT);

    public static NPDepartmentType byId(UUID id) {
        return association.getOrDefault(id, DEPARTMENT);
    }
}
