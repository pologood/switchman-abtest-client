package de.is24.common.abtesting.remote.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class AbTestVariant {
  @NotNull
  private Integer id;

  private String description;

  @NotNull
  private Integer weight;

}
