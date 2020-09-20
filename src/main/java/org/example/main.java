package org.example;

import java.io.IOException;

public class main {
    public static void main(String[] args) {
        /* Inicjalizacja aplikacji. Konstruktor czyta zapisane w bazie danych dzielnice Krakowa.*/
        App app = new App();

//        for(District dist : app.districts) {
//            System.out.println(dist.getName());
//        }
//
//        /* Wyświetlanie obecnej sytuacji pogodowej w Krakowie (Stare Miasto) */
//        District stareMiasto = app.districts.get(0);
//        Observation currentInStareMiasto = app.getCurrentByDistrict(stareMiasto);
//        System.out.println(currentInStareMiasto.toString());
//
//        /* Serializacja i deserializacja obserwacji */
//        currentInStareMiasto.serialize("currentInStareMiasto.ser");
//        Observation deserializedCurrent = new Observation();
//        try {
//             deserializedCurrent = Observation.deserialize("currentInStareMiasto.ser");
//        } catch (IOException | ClassNotFoundException e) {
//            System.out.println("Failed at deserializing object!");
//            e.printStackTrace();
//        }
//        System.out.println("Obiekt po serializacji i deserializacji:");
//        System.out.println(deserializedCurrent.toString());
//
//
//        /* Wyświetlanie obecnej sytuacji pogodowej dla dowolnej lokalizacji */
//        Observation anyLocation = app.getCurrentByLocation(0.23f, 2.3213f);
//        System.out.println(anyLocation.toString());
//
//        /* Zapis do pliku CSV danych historycznych dla wybranej dzielnicy */
//        app.saveDistrictDataToCSV(stareMiasto, "stareMiasto.csv");
//
//        /*
//        Aktualizacja danych historycznych dla kazdej dzielnicy.
//        Maksymalnie do 4 tygodni wstecz.
//        */
//        app.fetchUpdateUpTo4Weeks();

        try {
            District NewYork = app.createLocation(-73.935242f, 40.730610f, "New York");
            System.out.println(NewYork.getId());
        } catch (IOException | BadRequestException | InterruptedException e) {
            e.printStackTrace();
        }


    }
}
