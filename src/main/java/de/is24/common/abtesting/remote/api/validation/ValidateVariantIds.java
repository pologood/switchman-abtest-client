package de.is24.common.abtesting.remote.api.validation;

import javax.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Constraint(validatedBy = { VariantIdValidator.class })
@Documented
@Retention(RUNTIME)
@Target({ FIELD })
public @interface ValidateVariantIds {
  String message() default "variant ids must be in ascending order starting from 0";

  Class[] groups() default {};

  Class[] payload() default {};
}
