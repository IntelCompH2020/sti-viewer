package gr.cite.intelcomp.stiviewer.web.controllers.notification;

import gr.cite.intelcomp.stiviewer.common.JsonHandlingService;
import gr.cite.intelcomp.stiviewer.common.enums.ContactInfoType;
import gr.cite.intelcomp.stiviewer.common.enums.notification.NotificationContactType;
import gr.cite.intelcomp.stiviewer.common.scope.tenant.TenantScope;
import gr.cite.intelcomp.stiviewer.common.scope.user.UserScope;
import gr.cite.intelcomp.stiviewer.common.types.notification.*;
import gr.cite.intelcomp.stiviewer.integrationevent.outbox.notification.NotificationIntegrationEvent;
import gr.cite.intelcomp.stiviewer.integrationevent.outbox.notification.NotificationIntegrationEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.InvalidApplicationException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/notification/send", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationController {

    private final NotificationIntegrationEventHandler eventHandler;
    private final TenantScope tenantScope;
    private final UserScope userScope;
    private final JsonHandlingService jsonHandlingService;

    @Autowired
    public NotificationController(NotificationIntegrationEventHandler eventHandler, TenantScope tenantScope, UserScope userScope, JsonHandlingService jsonHandlingService) {
        this.eventHandler = eventHandler;
        this.tenantScope = tenantScope;
        this.userScope = userScope;
        this.jsonHandlingService = jsonHandlingService;
    }

    @GetMapping("email")
    @Transactional
    public void sendEmail() throws InvalidApplicationException {
        NotificationIntegrationEvent event = new NotificationIntegrationEvent();
        event.setTenant(tenantScope.getTenant());
        //Get the current user, temporary
        event.setUserId(userScope.getUserIdSafe());
        List<ContactPair> contactPairs = new ArrayList<>();
        contactPairs.add(new ContactPair(ContactInfoType.Email, "thgiannos@cite.gr"));
        NotificationContactData contactData = new NotificationContactData(contactPairs, null, null);
        event.setContactHint(jsonHandlingService.toJsonSafe(contactData));
        event.setContactTypeHint(NotificationContactType.EMAIL);
        event.setNotificationType(UUID.fromString("ce5bb8b5-3e00-4cc7-bccf-dbcf40d19cab"));
        NotificationFieldData data = new NotificationFieldData();
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        FieldInfo i1 = new FieldInfo();
        i1.setKey("{installation-url}");
        i1.setValue("localhost:8085");
        i1.setType(DataType.String);
        fieldInfoList.add(i1);
        FieldInfo i2 = new FieldInfo();
        i2.setKey("{file-id}");
        i2.setValue(UUID.randomUUID().toString());
        i2.setType(DataType.String);
        fieldInfoList.add(i2);
        FieldInfo i3 = new FieldInfo();
        i3.setKey("{tenant-code}");
        i3.setValue("new-tenant-1");
        i3.setType(DataType.String);
        fieldInfoList.add(i3);
        data.setFields(fieldInfoList);
        event.setData(jsonHandlingService.toJsonSafe(data));
        eventHandler.handle(event);
    }

    @GetMapping("in-app")
    @Transactional
    public void sendInApp() throws InvalidApplicationException {
        NotificationIntegrationEvent event = new NotificationIntegrationEvent();
        event.setTenant(tenantScope.getTenant());
        //Get the current user, temporary
        event.setUserId(userScope.getUserIdSafe());
        List<ContactPair> contactPairs = new ArrayList<>();
        NotificationContactData contactData = new NotificationContactData(contactPairs, null, null);
        event.setContactHint(jsonHandlingService.toJsonSafe(contactData));
        event.setContactTypeHint(NotificationContactType.IN_APP);
        event.setNotificationType(UUID.fromString("065deecd-21bb-44af-9983-e660fdf24bc4"));
        NotificationFieldData data = new NotificationFieldData();
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        data.setFields(fieldInfoList);
        event.setData(jsonHandlingService.toJsonSafe(data));
        eventHandler.handle(event);
    }

}
