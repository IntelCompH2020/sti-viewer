package gr.cite.notification.common.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FieldsValueMatchValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface  FieldsValueMatch {
	Class<?>[] groups () default {};

	String field();
	String fieldMatch();
	String failOn();

	String message() default "Fields values don't match!";

	Class<? extends Payload>[] payload () default {};

	@Target({ ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	@interface List {
		FieldsValueMatch[] value();
	}
}
