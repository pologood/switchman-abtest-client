package de.is24.common.abtesting.remote.api;

import de.is24.common.abtesting.remote.api.validation.ValidateDateRange;
import de.is24.common.abtesting.remote.api.validation.ValidateVariantIds;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.AutoPopulatingList;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@ValidateDateRange
public class AbTestConfiguration {
  public static final String REL = "abTestConfigurations";
  public static final String DATE_TIME_FORMAT = "dd.MM.YYYY HH:mm";

  @NotEmpty
  @NotNull
  private String name;

  @DateTimeFormat(pattern = DATE_TIME_FORMAT)
  @NotNull
  private DateTime from;

  @DateTimeFormat(pattern = DATE_TIME_FORMAT)
  @NotNull
  private DateTime to;

  private AbTestStorageType abTestStorageType;

  @NotEmpty
  @Valid
  @ValidateVariantIds
  private List<AbTestVariant> variants = new AutoPopulatingList<AbTestVariant>(AbTestVariant.class);

}
