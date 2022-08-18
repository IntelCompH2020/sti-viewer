package gr.cite.intelcomp.stiviewer.service.keycloak;

import com.google.common.collect.Lists;
import gr.cite.commons.web.keycloak.api.KeycloakAdminRestApi;
import gr.cite.intelcomp.stiviewer.config.keycloak.KeycloakResourcesConfiguration;
import gr.cite.intelcomp.stiviewer.data.TenantEntity;
import gr.cite.tools.logging.LoggerService;
import org.jetbrains.annotations.NotNull;
import org.keycloak.representations.idm.GroupRepresentation;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class KeycloakServiceImpl implements KeycloakService {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(KeycloakServiceImpl.class));
    private final KeycloakAdminRestApi api;
    private final KeycloakResourcesConfiguration configuration;

    @Autowired
    public KeycloakServiceImpl(KeycloakAdminRestApi api, KeycloakResourcesConfiguration configuration) {
        this.api = api;
        this.configuration = configuration;
        logger.info("Keycloak service initialized. Tenant authorities configured -> {}", configuration.getProperties().getAuthorities().size());
    }

    @Override
    public HashMap<String,GroupRepresentation> createTenantGroups(TenantEntity tenant) {
        HashMap<String, GroupRepresentation> groups = new HashMap<>();

        configuration.getProperties().getAuthorities().keySet().forEach(key -> {
            GroupRepresentation group = new GroupRepresentation();
            group.setName(configuration.getGroupName(tenant.getCode(), tenant.getId().toString()));
            HashMap<String, List<String>> user_attributes = new HashMap<>();
            user_attributes.put("auth", Lists.newArrayList(configuration.getAuthorityName(tenant.getCode(), key)));
            group.setAttributes(user_attributes);
            groups.put(key, api.groups().addGroupWithParent(group, configuration.getProperties().getAuthorities().get(key).getParent()));
        });

        return groups;
    }

    @Override
    public void addUserToGroup(@NotNull UUID subjectId, String groupId) {
        api.users().removeUserFromGroup(subjectId.toString(), configuration.getProperties().getGuestsGroup());
        api.users().addUserToGroup(subjectId.toString(), groupId);
    }

    @Override
    public void removeUserFromGroup(@NotNull UUID subjectId, String groupId) {
        api.users().removeUserFromGroup(subjectId.toString(), groupId);
    }

    @Override
    public void addUserToAdministratorsGroup(@NotNull UUID subjectId) {
        api.users().removeUserFromGroup(subjectId.toString(), configuration.getProperties().getGuestsGroup());
        api.users().addUserToGroup(subjectId.toString(), configuration.getProperties().getAdministratorsGroup());
    }

    @Override
    public void removeUserFromAdministratorsGroup(@NotNull UUID subjectId) {
        api.users().removeUserFromGroup(subjectId.toString(), configuration.getProperties().getAdministratorsGroup());
    }

    @Override
    public void addUserToTenantAuthorityGroup(UUID subjectId, TenantEntity tenant, String key) {
        api.users().removeUserFromGroup(subjectId.toString(), configuration.getProperties().getGuestsGroup());
        GroupRepresentation group = api.groups().findGroupByPath(getTenantAuthorityParentPath(key) + "/" + configuration.getGroupName(tenant.getCode(), tenant.getId().toString()));
        addUserToGroup(subjectId, group.getId());
    }

    @Override
    public void removeUserFromTenantAuthorityGroup(UUID subjectId, TenantEntity tenant, String key) {
        GroupRepresentation group = api.groups().findGroupByPath(getTenantAuthorityParentPath(key) + "/" + configuration.getGroupName(tenant.getCode(), tenant.getId().toString()));
        removeUserFromGroup(subjectId, group.getId());
    }

    public List<GroupRepresentation> getUserGroups(UUID subjectId) {
        return api.users().getGroups(subjectId.toString());
    }

    private String getTenantAuthorityParentPath(String key) {
        GroupRepresentation parent = api.groups().findGroupById(configuration.getProperties().getAuthorities().get(key).getParent());
        return parent.getPath();
    }

}
