package org.example;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class DBManager {
    private static Connection conn;

    public DBManager() {
    }
    public void getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:WeatherDB.db");
        } catch (SQLException | ClassNotFoundException throwables) {
            System.out.println("Failed at creating connection!");
            throwables.printStackTrace();
        }
    }
    public void closeConn() {
        try {
            conn.close();
        } catch (SQLException throwables) {
            System.out.println("Failed at closing connection!");
            throwables.printStackTrace();
        }
    }

    public void insertObservation(Observation obs) {
        // ensure connection exists
        if (conn == null){
            getConnection();
        }
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("" +
                    "INSERT INTO Observations (" +
                    "`DistrictID`,`Temperature`,`FeelsLike`,`Dewpoint`," +
                    "`WindSpeed`,`WindGust`,`BaroPressure`,`Visibility`,`Humidity`," +
                    "`WindDirection`,`Precipitation`,`PrecipitationType`,`ObservationTime`)" +
                    " SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?" +
                    " WHERE NOT EXISTS (SELECT * FROM Observations WHERE DistrictID = ? AND ObservationTime = ?)");
            preparedStatement.setString(1, obs.getDistrictID());
            preparedStatement.setDouble(2, obs.getTemp());
            preparedStatement.setDouble(3, obs.getFeelsLike());
            preparedStatement.setDouble(4, obs.getDewpoint());
            preparedStatement.setDouble(5, obs.getWindSpeed());
            preparedStatement.setDouble(6, obs.getWindGust());
            preparedStatement.setDouble(7, obs.getBaroPressure());
            preparedStatement.setDouble(8, obs.getVisibility());
            preparedStatement.setDouble(9, obs.getHumidity());
            preparedStatement.setDouble(10,obs.getWindDirection());
            preparedStatement.setDouble(11,obs.getPrecipitation());
            preparedStatement.setString(12,obs.getPrecipitationType());
            preparedStatement.setString(13,obs.getObservationTime().toString());
            preparedStatement.setString(14,obs.getDistrictID());
            preparedStatement.setString(15,obs.getObservationTime().toString());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            System.out.println("Failed at inserting observation into Data Base!");
            throwables.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("Failed at inserting empty observation!");
            System.out.println(obs);
            e.printStackTrace();
        }
    }
    public void insertDistrict(District district) throws SQLException {
        // ensure connection exists
        if (conn == null){
            getConnection();
        }

        PreparedStatement preparedStatement = conn.prepareStatement("" +
                "INSERT INTO Districts (DistrictID, Longitude, Latitude, LocationName)" +
                "SELECT ?, ?, ?, ?" +
                "WHERE NOT EXISTS (SELECT * FROM Districts WHERE Longitude = ? AND Latitude = ?);");
        preparedStatement.setString(1, district.getId());
        preparedStatement.setDouble(2, district.getLongitude());
        preparedStatement.setDouble(3, district.getLatitude());
        preparedStatement.setString(4, district.getName());
        preparedStatement.setDouble(5, district.getLongitude());
        preparedStatement.setDouble(6, district.getLatitude());
        preparedStatement.execute();
    }
    public void writeAllDistrictDataToFile (District district, String filename) {
        String query = "SELECT * FROM WeatherInLocation WHERE LocationName like \"" + district.getName() + "\"";
        try {
            ResultSet resultSet = executeCustomQuery(query);
            CSVWriter csvWriter = new CSVWriter(new FileWriter(filename));
            csvWriter.writeAll(resultSet, true);
            System.out.println("Data stored in " + filename);
        } catch (SQLException throwables) {
            System.out.println("Failed at getting all observations for district from Data Base!");
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<District> getAllDistricts() {
        List<District> districts = new ArrayList<>();
        String query = "SELECT * FROM Districts;";

        try {
            ResultSet resultSet = executeCustomQuery(query);

            while(resultSet.next()){
                String id = resultSet.getString("DistrictID");
                float longitude = (float) resultSet.getDouble("Longitude");
                float latitude = (float) resultSet.getDouble("Latitude");
                String name = resultSet.getString("LocationName");
                districts.add(new District(id, longitude, latitude, name));
            }
        } catch (SQLException throwables) {
            System.out.println("Failed at getting all districts from Data Base!");
            throwables.printStackTrace();
        }
        return districts;
    }
    public ResultSet executeCustomQuery(String query) throws SQLException {
        if(conn == null){
            getConnection();
        }
        Statement statement = conn.createStatement();
        return statement.executeQuery(query);
    }

}
