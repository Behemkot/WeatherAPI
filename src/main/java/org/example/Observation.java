package org.example;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "districtID",
        "temp",
        "feels_like",
        "dewpoint",
        "wind_speed",
        "wind_gust",
        "baro_pressure",
        "visibility",
        "humidity",
        "wind_direction",
        "precipitation",
        "precipitation_type",
        "observation_time"
})

public class Observation implements Comparator<Observation>, Serializable {
    @JsonProperty("lon")
    private String districtID;
    @JsonProperty("temp")
    private float temp;
    @JsonProperty("feels_like")
    private float feelsLike;
    @JsonProperty("dewpoint")
    private float dewpoint;
    @JsonProperty("wind_speed")
    private float windSpeed;
    @JsonProperty("wind_gust")
    private float windGust;
    @JsonProperty("baro_pressure")
    private float baroPressure;
    @JsonProperty("visibility")
    private float visibility;
    @JsonProperty("humidity")
    private float humidity;
    @JsonProperty("wind_direction")
    private float windDirection;
    @JsonProperty("precipitation")
    private float precipitation;
    @JsonProperty("precipitation_type")
    private String precipitationType;
    @JsonProperty("weather_code")
    private LocalDateTime observationTime;

    // default constructor
    public Observation() {

    }
    // constructor for JsonParser
    public Observation(String districtID, float temp, float feelsLike, float dewpoint, float windSpeed,
                       float windGust, float baroPressure, float visibility, float humidity, float windDirection,
                       float precipitation, String precipitationType, LocalDateTime observationTime) {
        this.districtID = districtID;
        this.temp = temp;
        this.feelsLike = feelsLike;
        this.dewpoint = dewpoint;
        this.windSpeed = windSpeed;
        this.windGust = windGust;
        this.baroPressure = baroPressure;
        this.visibility = visibility;
        this.humidity = humidity;
        this.windDirection = windDirection;
        this.precipitation = precipitation;
        this.precipitationType = precipitationType;
        this.observationTime = observationTime;
    }

    public void serialize(String filename) {
        try {
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(filename));
            stream.writeObject(this);
            stream.close();
            System.out.println("Object successfully serialized to file: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Observation deserialize(String filename) throws IOException, ClassNotFoundException {
        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(filename));
        return (Observation) stream.readObject();
    }
    @Override
    public String toString() {
        String windDir = "";
        float dir = this.getWindDirection();
        if (dir <= 22.5 || 360 - 22.5 > dir ) {
            windDir = "N";
        } else if (dir <= 67.5) {
            windDir = "NE";
        } else if (dir <= 112.5) {
            windDir = "E";
        } else if (dir <= 157.5) {
            windDir = "SE";
        } else if (dir <= 202.5) {
            windDir = "S";
        } else if (dir <= 247.5) {
            windDir = "SW";
        } else if (dir <= 292.5) {
            windDir = "w";
        } else if (dir <= 337.5) {
            windDir = "NW";
        }

        String builder = "Weather at: " + this.districtID + '\n' +
                "TEMPERATURE: " + this.getTemp() + " \u2103 " +
                "feels like: " + this.getFeelsLike() + " \u2103\n" +
                "WIND: " + this.getWindSpeed() + " up to " + windGust + " km/h " + windDir + "\n" +
                "PRECIPITATION: " + this.getPrecipitation() + " " +
                this.getPrecipitationType() + "\n" +
                "BARO PRESSURE: " + this.getBaroPressure() + " hPa\n";
        return builder;
    }

    @Override
    public int compare(Observation o1, Observation o2) {
        if(o1.getObservationTime().isEqual(o2.getObservationTime())) {
            return 0;
        }
        else if (o1.getObservationTime().isBefore(o2.getObservationTime())) {
            return -1;
        }
        return 1;
    }





    /* Geters Seters */
    public String getDistrictID() {
        return districtID;
    }
    public void setDistrictID(String districtID) {
        this.districtID = districtID;
    }
    public float getTemp() {
        return temp;
    }
    public void setTemp(float temp) {
        this.temp = temp;
    }
    public float getFeelsLike() {
        return feelsLike;
    }
    public void setFeelsLike(float feelsLike) {
        this.feelsLike = feelsLike;
    }
    public float getDewpoint() {
        return dewpoint;
    }
    public void setDewpoint(float dewpoint) {
        this.dewpoint = dewpoint;
    }
    public float getWindSpeed() {
        return windSpeed;
    }
    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }
    public float getWindGust() {
        return windGust;
    }
    public void setWindGust(float windGust) {
        this.windGust = windGust;
    }
    public float getBaroPressure() {
        return baroPressure;
    }
    public void setBaroPressure(float baroPressure) {
        this.baroPressure = baroPressure;
    }
    public float getVisibility() {
        return visibility;
    }
    public void setVisibility(float visibility) {
        this.visibility = visibility;
    }
    public float getHumidity() {
        return humidity;
    }
    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }
    public float getWindDirection() {
        return windDirection;
    }
    public void setWindDirection(float windDirection) {
        this.windDirection = windDirection;
    }
    public float getPrecipitation() {
        return precipitation;
    }
    public void setPrecipitation(float precipitation) {
        this.precipitation = precipitation;
    }
    public String getPrecipitationType() {
        return precipitationType;
    }
    public void setPrecipitationType(String precipitationType) {
        this.precipitationType = precipitationType;
    }
    public LocalDateTime getObservationTime() {
        return observationTime;
    }
    public void setObservationTime(LocalDateTime observationTime) {
        this.observationTime = observationTime;
    }


}