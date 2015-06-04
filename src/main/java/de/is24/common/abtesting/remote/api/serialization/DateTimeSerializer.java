package de.is24.common.abtesting.remote.api.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import java.io.IOException;


public class DateTimeSerializer extends JsonSerializer<DateTime> {
  public static final DateTimeFormatter DATE_TIME_FORMATTER = ISODateTimeFormat.dateTime();

  @Override
  public void serialize(DateTime value, JsonGenerator gen,
                        SerializerProvider arg2) throws IOException, JsonProcessingException {
    gen.writeString(DATE_TIME_FORMATTER.print(value));
  }
}
