package org.example;

import java.util.List;
import java.util.Map;

public class Settings {
    public static final String API_URL_REALTIME = "https://api.climacell.co/v3/weather/realtime";
    public static final String API_URL_HISTORICAL = "https://api.climacell.co/v3/weather/historical/station";
    public static final String API_URL_LOCATION = "https://api.climacell.co/v3/locations";
    public static final String API_KEY = "";

    public static final List<String> FIELD_LIST = List.of(
            "temp",
            "feels_like",
            "dewpoint",
            "humidity",
            "wind_speed",
            "wind_direction",
            "wind_gust",
            "baro_pressure",
            "precipitation",
            "precipitation_type",
            "visibility"
    );

    public static final String FIELDS = String.join("%2C",Settings.FIELD_LIST);
}
