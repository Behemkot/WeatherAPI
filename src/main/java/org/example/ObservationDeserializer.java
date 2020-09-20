package org.example;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ObservationDeserializer extends  JsonDeserializer<Observation>{
    @Override
    public Observation deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);

        Observation obs = new Observation();
        final float temp = (float) node.get("temp").get("value").asDouble();
        final float feels_like = (float) node.get("feels_like").get("value").asDouble();
        final float dewpoint = (float) node.get("dewpoint").get("value").asDouble();
        final float wind_speed = (float) node.get("wind_speed").get("value").asDouble();
        final float wind_gust = (float) node.get("wind_gust").get("value").asDouble();
        final float baro_pressure = (float) node.get("baro_pressure").get("value").asDouble();
        final float humidity = (float) node.get("humidity").get("value").asDouble();
        final float visibility = (float) node.get("visibility").get("value").asDouble();
        final float wind_direction = (float) node.get("wind_direction").get("value").asDouble();
        final float precipitation = (float) node.get("precipitation").get("value").asDouble();
        final String precipitation_type = node.get("precipitation_type").get("value").asText();
        final String observation_time_string = node.get("observation_time").get("value").asText();

        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        LocalDateTime observationTime = LocalDateTime.parse(observation_time_string, formatter);

        obs.setTemp(temp);
        obs.setFeelsLike(feels_like);
        obs.setDewpoint(dewpoint);
        obs.setWindSpeed(wind_speed);
        obs.setWindGust(wind_gust);
        obs.setBaroPressure(baro_pressure);
        obs.setHumidity(humidity);
        obs.setVisibility(visibility);
        obs.setWindDirection(wind_direction);
        obs.setPrecipitation(precipitation);
        obs.setPrecipitationType(precipitation_type);
        obs.setObservationTime(observationTime);

        return obs;
    }
}
