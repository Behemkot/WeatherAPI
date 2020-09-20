package org.example;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class DistrictDeserializer extends JsonDeserializer<District> {
    @Override
    public District deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);

        final String id = node.get("id").asText();
        final float longitude = (float) node.get("point").get("lon").asDouble();
        final float latitude = (float) node.get("point").get("lat").asDouble();
        final String name = node.get("name").asText();

        return new District(id, longitude, latitude, name);
    }
}
