package gr.cite.intelcomp.stiviewer.web.controllers;

import gr.cite.intelcomp.stiviewer.audit.AuditableAction;
import gr.cite.intelcomp.stiviewer.authorization.AuthorizationFlags;
import gr.cite.intelcomp.stiviewer.common.types.indicatorgroup.IndicatorGroupEntity;
import gr.cite.intelcomp.stiviewer.model.IndicatorGroup;
import gr.cite.intelcomp.stiviewer.model.builder.IndicatorGroupBuilder;
import gr.cite.intelcomp.stiviewer.model.censorship.IndicatorGroupCensor;
import gr.cite.intelcomp.stiviewer.service.indicatorgroup.IndicatorGroupService;
import gr.cite.tools.auditing.AuditService;
import gr.cite.tools.data.builder.BuilderFactory;
import gr.cite.tools.data.censor.CensorFactory;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.tools.logging.LoggerService;
import gr.cite.tools.logging.MapLogEntry;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping(path = "api/indicator-group")
public class IndicatorGroupController {

    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(IndicatorGroupController.class));

    private final BuilderFactory builderFactory;
    private final AuditService auditService;
    private final IndicatorGroupService indicatorGroupService;
    private final CensorFactory censorFactory;
    private final MessageSource messageSource;

    @Autowired
    public IndicatorGroupController(
            BuilderFactory builderFactory,
            AuditService auditService,
            IndicatorGroupService indicatorGroupService,
            CensorFactory censorFactory,
            MessageSource messageSource

    ) {
        this.builderFactory = builderFactory;
        this.auditService = auditService;
        this.indicatorGroupService = indicatorGroupService;
        this.censorFactory = censorFactory;
        this.messageSource = messageSource;
    }

    @GetMapping("get-all")
    public List<IndicatorGroup> getGroupItems(FieldSet fieldSet) throws MyApplicationException, MyForbiddenException {
        logger.debug(new MapLogEntry("retrieving" + IndicatorGroupEntity.class.getSimpleName()).And("fields", fieldSet));

        this.censorFactory.censor(IndicatorGroupCensor.class).censor(fieldSet, null);
        List<IndicatorGroupEntity> items = this.indicatorGroupService.getIndicatorGroups();
        List<IndicatorGroup> models = this.builderFactory.builder(IndicatorGroupBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(fieldSet, items);

        this.auditService.track(AuditableAction.Indicator_Group_Get_All, "fieldSet", fieldSet);
        //this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

        return models;
    }

    @GetMapping("{id}")
    public IndicatorGroup Get(@PathVariable("id") UUID id, FieldSet fieldSet, Locale locale) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
        logger.debug(new MapLogEntry("retrieving" + IndicatorGroup.class.getSimpleName()).And("id", id).And("fields", fieldSet));

        this.censorFactory.censor(IndicatorGroupCensor.class).censor(fieldSet, null);

        IndicatorGroupEntity item = this.indicatorGroupService.getIndicatorGroups().stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
        if (item == null)
            throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{id, IndicatorGroup.class.getSimpleName()}, LocaleContextHolder.getLocale()));
        IndicatorGroup model = this.builderFactory.builder(IndicatorGroupBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(fieldSet, item);

        this.auditService.track(AuditableAction.Indicator_Group_Lookup, Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("id", id),
                new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
        ));
        //this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

        return model;
    }

    @GetMapping("by-code/{code}")
    public IndicatorGroup Get(@PathVariable("code") String code, FieldSet fieldSet, Locale locale) throws MyApplicationException, MyForbiddenException, MyNotFoundException {
        logger.debug(new MapLogEntry("retrieving" + IndicatorGroup.class.getSimpleName()).And("code", code).And("fields", fieldSet));

        this.censorFactory.censor(IndicatorGroupCensor.class).censor(fieldSet, null);

        IndicatorGroupEntity item = this.indicatorGroupService.getIndicatorGroups().stream().filter(x -> x.getCode().equals(code)).findFirst().orElse(null);
        if (item == null)
            throw new MyNotFoundException(messageSource.getMessage("General_ItemNotFound", new Object[]{code, IndicatorGroup.class.getSimpleName()}, LocaleContextHolder.getLocale()));
        IndicatorGroup model = this.builderFactory.builder(IndicatorGroupBuilder.class).authorize(AuthorizationFlags.OwnerOrPermissionOrIndicator).build(fieldSet, item);

        this.auditService.track(AuditableAction.Indicator_Group_LookupByCode, Map.ofEntries(
                new AbstractMap.SimpleEntry<String, Object>("code", code),
                new AbstractMap.SimpleEntry<String, Object>("fields", fieldSet)
        ));
        //this.auditService.trackIdentity(AuditableAction.IdentityTracking_Action);

        return model;
    }
}
