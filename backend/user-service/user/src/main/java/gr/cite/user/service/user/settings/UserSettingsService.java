package gr.cite.user.service.user.settings;

import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.exception.MyForbiddenException;
import gr.cite.tools.exception.MyNotFoundException;
import gr.cite.tools.exception.MyValidationException;
import gr.cite.tools.fieldset.FieldSet;
import gr.cite.user.model.UserSettings;
import gr.cite.user.model.persist.UserSettingsPersist;

import javax.management.InvalidApplicationException;
import java.util.List;
import java.util.UUID;

public interface UserSettingsService {
	UserSettings getUserSettings(String key, UUID userId, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;

	UserSettings persist(UserSettingsPersist model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;

	List<UserSettings> batchPersist(List<UserSettingsPersist> model, FieldSet fields) throws MyForbiddenException, MyValidationException, MyApplicationException, MyNotFoundException, InvalidApplicationException;

	FieldSet getModelFields();

	UserSettings deleteAndSave(UUID userId, UUID id) throws MyForbiddenException, InvalidApplicationException;

}
