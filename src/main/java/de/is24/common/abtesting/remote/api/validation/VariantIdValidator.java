package de.is24.common.abtesting.remote.api.validation;

import de.is24.common.abtesting.remote.api.AbTestVariant;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;


public class VariantIdValidator implements ConstraintValidator<ValidateVariantIds, List<AbTestVariant>> {
  @Override
  public void initialize(ValidateVariantIds constraintAnnotation) {
  }

  @Override
  public boolean isValid(List<AbTestVariant> variants, ConstraintValidatorContext context) {
    if (isEmpty(variants)) {
      return true;
    }

    for (int i = 0; i < variants.size(); i++) {
      if (variants.get(i).getId() != i) {
        return false;
      }
      ;
    }
    return true;
  }
}
