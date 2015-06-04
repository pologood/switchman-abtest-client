package de.is24.common.abtesting.remote.command;

import de.is24.common.hateoas.HateoasLinkProvider;
import de.is24.common.hystrix.HateoasRemoteCommand;
import org.springframework.web.client.RestOperations;


public abstract class AbstractAbTestRemoteCommand<T> extends HateoasRemoteCommand<T> {
  public static final String COMMAND_GROUP_KEY = "AbTestRemoteGroup";

  public AbstractAbTestRemoteCommand(Setter setter, RestOperations restOperations,
                                     HateoasLinkProvider hateoasLinkProvider) {
    super(setter, restOperations, hateoasLinkProvider);
  }
}
