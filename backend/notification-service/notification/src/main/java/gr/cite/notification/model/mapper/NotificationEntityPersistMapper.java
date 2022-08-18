package gr.cite.notification.model.mapper;

import gr.cite.notification.data.NotificationEntity;
import gr.cite.notification.model.mapper.utils.MappingFunction;
import gr.cite.notification.model.persist.NotificationPersist;
import gr.cite.tools.fieldset.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Map;

@Component
@RequestScope
public class NotificationEntityPersistMapper implements Mapper<NotificationEntity, NotificationPersist> {

    private static final Map<String, MappingFunction<NotificationEntity, NotificationPersist>> mappingMap = Map.of(
            NotificationPersist.Field._id.toLowerCase(), ((source, target) -> target.setId(source.getId())),
            NotificationPersist.Field._type.toLowerCase(), ((source, target) -> target.setType(source.getType())),
            NotificationPersist.Field._contactTypeHint.toLowerCase(), ((source, target) -> target.setContactTypeHint(source.getContactTypeHint())),
            NotificationPersist.Field._contactHint.toLowerCase(), ((source, target) -> target.setContactHint(source.getContactHint())),
            NotificationPersist.Field._notifiedAt.toLowerCase(), ((source, target) -> target.setNotifiedAt(source.getNotifiedAt())),
            NotificationPersist.Field._userId.toLowerCase(), ((source, target) -> target.setUserId(source.getUserId())),
            NotificationPersist.Field._data.toLowerCase(), ((source, target) -> target.setData(source.getData())),
            NotificationPersist.Field._notifiedWith.toLowerCase(), ((source, target) -> target.setNotifiedWith(source.getNotifiedWith())),
            NotificationPersist.Field._notifyState.toLowerCase(), ((source, target) -> target.setNotifyState(source.getNotifyState())),
            NotificationPersist.Field._retryCount.toLowerCase(), ((source, target) -> target.setRetryCount(source.getRetryCount()))
    );

    @Override
    public NotificationPersist map(NotificationEntity source, FieldSet fieldSet) {
        NotificationPersist target = new NotificationPersist();
        for (String field: fieldSet.getFields()) {
            mappingMap.get(field).map(source,target);
        }
        return target;
    }

    public static Class<NotificationEntity> getSourceClass() {
        return NotificationEntity.class;
    }

    public static Class<NotificationPersist> getTargetClass() {
        return NotificationPersist.class;
    }
}
