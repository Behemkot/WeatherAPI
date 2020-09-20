package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class App {
    public List<District> districts = new ArrayList<>();
    private DBManager manager = new DBManager();

    public App() {
        districts = getDistrictsFromDB();
    }

    public List<District> getDistrictsFromDB() {
        this.manager.getConnection();
        List<District> districts = new ArrayList<>();
        districts = manager.getAllDistricts();
        this.manager.closeConn();
        return districts;
    }
    public void fetchUpdateUpTo4Weeks() {
        List<Observation> observations = new ArrayList<Observation>();
        List<Observation> fetched = new ArrayList<>();
        for(District district : districts) {
            try {
                fetched = fetchUpdateForDistrict(district.getId());
                for(Observation obs : fetched) {
                    if(obs != null) {
                        observations.add(obs);
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (BadRequestException e) {
                e.printStackTrace();
                break;
            } catch (NullPointerException e) {
                System.out.println("No possible updates!");
                break;
            }
        }

        manager.getConnection();
        for(Observation obs : observations){
            manager.insertObservation(obs);
        }
        System.out.println("Observations inserted: " + observations.size());
        manager.closeConn();
    }
    private List<Observation> fetchUpdateForDistrict(String ID) throws SQLException, BadRequestException {
        manager.getConnection();
        String query = "SELECT max(ObservationTime) as \"ObservationTime\" FROM Observations WHERE DistrictID like \"" + ID + "\";";
        ResultSet resultSet = manager.executeCustomQuery(query);
        String time = resultSet.getString("ObservationTime");
        LocalDateTime fromTime = LocalDateTime.parse(time);
        LocalDateTime now = LocalDateTime.now();
        // clear if before today
        if (fromTime.getDayOfYear() < now.getDayOfYear()){
            fromTime = LocalDateTime.of(fromTime.getYear(), fromTime.getMonth(), fromTime.getDayOfMonth(), 0, 0, 0, 0);
            manager.closeConn();
            System.out.println("Geting update for: " + ID + " FROM: " + fromTime.toString() + " TO: " + now);
            return getHistoricalFromTo(ID, fromTime, now);
        }
        manager.closeConn();
        return null;
    }
    public List<Observation> getHistoricalFromTo(String ID, LocalDateTime from, LocalDateTime to) throws BadRequestException {
        List<Observation> observations = new ArrayList<Observation>();
        LocalDateTime delta = from.plusDays(1);
        // update all days before today
        while(delta.isBefore(to)){
            observations.addAll(getUpTo24HoursUpdate(ID, from, from.plusDays(1)));
            from = delta;
            delta = delta.plusDays(1);
        }
        // update for today
        observations.addAll(getUpTo24HoursUpdate(ID, from, to));
        return observations;
    }
    private List<Observation> getUpTo24HoursUpdate(String ID, LocalDateTime from, LocalDateTime to) throws BadRequestException {
        // sprawdzenie czy przedzial czasowy nie jest za maly lub nieodpowiedni
        assert from.plusMinutes(30).isBefore(to);
        List<Observation> myObjects = new ArrayList<>();

        try {
            StringBuilder builder = new StringBuilder(Settings.API_URL_HISTORICAL);
            // add request parameters
            builder.append("?location_id=").append(ID);
            builder.append("&unit_system=").append("si");
            builder.append("&start_time=").append(from);
            builder.append("&end_time=").append(to);
            builder.append("&fields=").append(String.join("%2C",Settings.FIELD_LIST));
            builder.append("&apikey=").append(Settings.API_KEY);

            // create request
            String url = builder.toString();

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .header("accept", "application/json")
                    .uri(URI.create(url))
                    .build();

            // execute request
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200){
                throw new BadRequestException(response.statusCode() + ": " + response.body());
            }

            // map output to list of objects Observation

            // create mapper
            ObjectMapper mapper = new ObjectMapper();
            // create module with custom deserializer
            SimpleModule module = new SimpleModule();
            // register custom deserializer
            module.addDeserializer(Observation.class, new ObservationDeserializer());
            mapper.registerModule(module);
            //confugire mapper
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            // serialize json to list of objects
            myObjects = mapper.readValue(
                    response.body(),
                    mapper.getTypeFactory().constructCollectionType(List.class, Observation.class));

            // Insert observations to database
            manager.getConnection();
            for (Observation observation : myObjects) {
                observation.setDistrictID(ID);
                manager.insertObservation(observation);
            }
            manager.closeConn();
        } catch (IOException | InterruptedException e) {
            System.out.println("Failed at executing request!");
            e.printStackTrace();
        }
        return myObjects;
    }
    public District createLocation(float longitude, float latitude, String locationName) throws IOException, InterruptedException, BadRequestException {
        String requestBody = "{\"point\":{\"lat\":" + latitude +
                ",\"lon\":" + longitude + "}," +
                "\"name\":\"" + locationName + "\"}";

        System.out.println(requestBody);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .header("content-type", "application/json")
                .uri(URI.create(Settings.API_URL_LOCATION + "?apikey=" + Settings.API_KEY))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();


        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if(response.statusCode() != 200) {
            throw new BadRequestException(response.body());
        }

        System.out.println(response.body());

        ObjectMapper mapper = new ObjectMapper();
        // create module with custom deserializer
        SimpleModule module = new SimpleModule();
        // register custom deserializer
        module.addDeserializer(District.class, new DistrictDeserializer());
        mapper.registerModule(module);
        //confugire mapper
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // serialize json to list of objects
        return  mapper.readValue(response.body(), District.class);
    }
    public Observation getCurrentByDistrict(District district)  {
        Observation observation = getCurrentByLocation(district.getLongitude(), district.getLatitude());
        observation.setDistrictID(district.getId());
        return observation;
    }
    public  Observation getCurrentByLocation(float longitude, float latitude)  {
        Observation observation = new Observation();
        StringBuilder builder = new StringBuilder(Settings.API_URL_REALTIME);
        //builder.append("?location_id=").append(ID);
        builder.append("?lat=").append(latitude);
        builder.append("&lon=").append(longitude);
        builder.append("&unit_system=").append("si");
        builder.append("&fields=").append(String.join("%2C",Settings.FIELD_LIST));
        builder.append("&apikey=").append(Settings.API_KEY);

        String url = builder.toString();
        System.out.println(url);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("accept", "application/json")
                .uri(URI.create(url))
                .build();


        try {
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            // create mapper
            ObjectMapper mapper = new ObjectMapper();
            // create module with custom deserializer
            SimpleModule module = new SimpleModule();
            // register custom deserializer
            module.addDeserializer(Observation.class, new ObservationDeserializer());
            mapper.registerModule(module);
            //confugire mapper
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            // serialize json to list of objects
            observation = mapper.readValue(response.body(), Observation.class);
            String ID = "Unknown";
            observation.setDistrictID(ID);

            return observation;
        } catch (JsonProcessingException e) {
            System.out.println("Failed at mapping observation from json");
            e.printStackTrace();
        } catch (InterruptedException | IOException e) {
            System.out.println("Failed at sending request!");
            e.printStackTrace();
        }
        return null;
    }
    public void saveDistrictDataToCSV(District district, String filename) {
        manager.getConnection();
        manager.writeAllDistrictDataToFile(district, filename);
        manager.closeConn();
    }

}
