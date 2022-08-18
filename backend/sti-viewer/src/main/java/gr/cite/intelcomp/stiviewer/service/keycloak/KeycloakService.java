package gr.cite.intelcomp.stiviewer.service.keycloak;

import gr.cite.intelcomp.stiviewer.data.TenantEntity;
import org.jetbrains.annotations.NotNull;
import org.keycloak.representations.idm.GroupRepresentation;

import java.util.HashMap;
import java.util.UUID;

public interface KeycloakService {

    HashMap<String,GroupRepresentation> createTenantGroups(TenantEntity tenant);
    void addUserToGroup(UUID subjectId, String groupId);
    void removeUserFromGroup(@NotNull UUID subjectId, String groupId);
    void addUserToAdministratorsGroup(UUID subjectId);
    void removeUserFromAdministratorsGroup(@NotNull UUID subjectId);
    void addUserToTenantAuthorityGroup(UUID subjectId, TenantEntity tenant, String key);
    void removeUserFromTenantAuthorityGroup(UUID subjectId, TenantEntity tenant, String key);

}
