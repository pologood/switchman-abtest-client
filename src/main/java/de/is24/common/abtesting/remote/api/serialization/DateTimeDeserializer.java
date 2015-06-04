package de.is24.common.abtesting.remote.api.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import java.io.IOException;


public class DateTimeDeserializer extends JsonDeserializer<DateTime> {
  public static final DateTimeFormatter DATE_TIME_FORMATTER = ISODateTimeFormat.dateTime();

  @Override
  public DateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                       throws IOException, JsonProcessingException {
    return DATE_TIME_FORMATTER.parseDateTime(jsonParser.getValueAsString());
  }
}
