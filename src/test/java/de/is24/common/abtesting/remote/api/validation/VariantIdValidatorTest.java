package de.is24.common.abtesting.remote.api.validation;

import de.is24.common.abtesting.remote.api.AbTestVariant;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


public class VariantIdValidatorTest {
  private VariantIdValidator validator = new VariantIdValidator();

  @Test
  public void validatesMissingVariants() {
    assertThat(validator.isValid(null, null), is(true));
  }

  @Test
  public void validatesEmptyVariants() {
    assertThat(validator.isValid(createVariants(), null), is(true));
  }

  @Test
  public void validatesAscendingVariants() {
    assertThat(validator.isValid(createVariants(0, 1, 2), null), is(true));
  }

  @Test
  public void failValidationOnAscendingVariantsWithGaps() {
    assertThat(validator.isValid(createVariants(0, 2, 4), null), is(false));
  }

  @Test
  public void failValidationOnAscendingVariantsNotStartingFromZero() {
    assertThat(validator.isValid(createVariants(1, 2, 3), null), is(false));
  }

  @Test
  public void failValidationOnDescendingVariants() {
    assertThat(validator.isValid(createVariants(2, 1, 0), null), is(false));
  }

  @Test
  public void failValidationOnRandomVariants() {
    assertThat(validator.isValid(createVariants(2, 0, 1), null), is(false));
    assertThat(validator.isValid(createVariants(1, 0, 2), null), is(false));
    assertThat(validator.isValid(createVariants(0, 2, 1), null), is(false));
    assertThat(validator.isValid(createVariants(1, 2, 0), null), is(false));
  }


  private List<AbTestVariant> createVariants(Integer... ids) {
    List<AbTestVariant> variants = new ArrayList<>();
    for (Integer id : ids) {
      AbTestVariant variant = new AbTestVariant();
      variant.setId(id);
      variants.add(variant);
    }

    return variants;
  }


}
