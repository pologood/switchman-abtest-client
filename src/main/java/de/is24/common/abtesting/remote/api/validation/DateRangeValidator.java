package de.is24.common.abtesting.remote.api.validation;

import de.is24.common.abtesting.remote.api.AbTestConfiguration;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class DateRangeValidator implements ConstraintValidator<ValidateDateRange, AbTestConfiguration> {
  public void initialize(ValidateDateRange validateDateRange) {
  }

  public boolean isValid(AbTestConfiguration configuration, ConstraintValidatorContext constraintValidatorContext) {
    if ((configuration.getFrom() != null) && (configuration.getTo() != null)) {
      return configuration.getFrom().isBefore(configuration.getTo());
    }

    return true;
  }


}
