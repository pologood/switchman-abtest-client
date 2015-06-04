package de.is24.common.abtesting.remote.api.validation;

import javax.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Constraint(validatedBy = { DateRangeValidator.class })
@Documented
@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE })
public @interface ValidateDateRange {
  String message() default "{end} should be later than {start}";

  Class[] groups() default {};

  Class[] payload() default {};
}
