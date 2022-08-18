package gr.cite.notification.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class JsonHandlingService {
	private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(JsonHandlingService.class));
	private final ObjectMapper objectMapper = new ObjectMapper();

	public String toJson(Object item) throws JsonProcessingException {
		if (item == null) return null;
		return objectMapper.writeValueAsString(item);
	}

	public String toJsonSafe(Object item) {
		if (item == null) return null;
		try {
			return objectMapper.writeValueAsString(item);
		} catch (Exception ex) {
			logger.error("Json Parsing Error: " + ex.getLocalizedMessage(), ex);
			return null;
		}
	}

	public <T> T fromJson(Class<T> type, String json) throws JsonProcessingException {
		if (json == null) return null;
		return objectMapper.readValue(json, type);
	}

	public <T> T fromJsonSafe(Class<T> type, String json) {
		if (json == null) return null;
		try {
			return objectMapper.readValue(json, type);
		} catch (Exception ex) {
			logger.error("Json Parsing Error: " + ex.getLocalizedMessage(), ex);
			return null;
		}
	}
}
