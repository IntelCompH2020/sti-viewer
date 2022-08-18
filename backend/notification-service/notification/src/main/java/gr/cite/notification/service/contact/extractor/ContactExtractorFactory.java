package gr.cite.notification.service.contact.extractor;

import gr.cite.notification.common.enums.NotificationContactType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ContactExtractorFactory {

    private final Map<NotificationContactType, ContactExtractor> contactExtractorMap;


    @Autowired
    public ContactExtractorFactory(List<ContactExtractor> contactExtractorList) {
        this.contactExtractorMap = contactExtractorList.stream().collect(Collectors.toMap(ContactExtractor::supports, contactExtractor -> contactExtractor));
    }

    public ContactExtractor fromContactType(NotificationContactType type) {
        return this.contactExtractorMap.getOrDefault(type, null);
    }
}
