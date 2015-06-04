package de.is24.common.abtesting.remote.api.validation;

import de.is24.common.abtesting.remote.api.AbTestConfiguration;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;


public class DateRangeValidatorTest {
  private DateRangeValidator validator = new DateRangeValidator();


  @Test
  public void returnsTrueWhenDatesMissing() {
    AbTestConfiguration configuration = new AbTestConfiguration();

    assertThat(validator.isValid(configuration, null), is(true));
  }

  @Test
  public void returnsTrueWhenFromDateIsMissing() {
    AbTestConfiguration configuration = new AbTestConfiguration();
    configuration.setTo(now());

    assertThat(validator.isValid(configuration, null), is(true));
  }

  @Test
  public void returnsTrueWhenToDateIsMissing() {
    AbTestConfiguration configuration = new AbTestConfiguration();
    configuration.setFrom(now());

    assertThat(validator.isValid(configuration, null), is(true));
  }

  @Test
  public void returnsTrueWhenFromIsBeforeTo() {
    AbTestConfiguration configuration = new AbTestConfiguration();
    configuration.setFrom(now().minusDays(1));
    configuration.setTo(now().plusDays(1));

    assertThat(validator.isValid(configuration, null), is(true));
  }

  @Test
  public void returnsFalseWhenFromIsAfterTo() {
    AbTestConfiguration configuration = new AbTestConfiguration();
    configuration.setFrom(now().plusDays(1));
    configuration.setTo(now().minusDays(1));

    assertThat(validator.isValid(configuration, null), is(false));
  }
}
