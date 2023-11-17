package utils;

import data.house.HouseDAO;
import data.media.MediaDAO;
import data.rental.RentalDAO;
import data.user.UserDAO;
import db.*;
import srv.resources.MediaResource;

public class ClearContainers {
    private static CosmosDBHousesLayer hdb;
    private static CosmosDBMediaLayer mdb;
    private static CosmosDBQuestionsLayer qdb;
    private static CosmosDBRentalsLayer rdb;
    private static CosmosDBSessionsLayer sdb;
    private static CosmosDBUsersLayer udb;
    private static final MediaResource media = new MediaResource();

    private static void getInstances() {
        hdb = CosmosDBHousesLayer.getInstance();
        mdb = CosmosDBMediaLayer.getInstance();
        qdb = CosmosDBQuestionsLayer.getInstance();
        rdb = CosmosDBRentalsLayer.getInstance();
        sdb = CosmosDBSessionsLayer.getInstance();
        udb = CosmosDBUsersLayer.getInstance();
    }

    private static void clearUsers() {
        System.out.println("Clear Users");
        int count = 0;
        for (UserDAO userDAO : udb.getUsers()) {
            udb.delUser(userDAO);
            media.deleteFile("images", userDAO.getPhotoId());

            count++;
            if(count % 100 == 0) {
                System.out.println(count);
            }
        }
        System.out.println(count);
    }

    private static void clearHouses() {
        System.out.println("Clear Houses");
        int count = 0;
        for (HouseDAO houseDAO : hdb.getHousesNonPaged()) {
            hdb.delHouse(houseDAO);
            for (MediaDAO mediaDAO : mdb.getMediaByItemId(houseDAO.getId())) {
                media.deleteFile("images", mediaDAO.getId());
            }
            count++;
            if(count % 100 == 0) {
                System.out.println(count);
            }
        }
        System.out.println(count);
    }

    private static void clearRentals() {
        System.out.println("Clear Rentals");
        int count = 0;
        for(RentalDAO rentalDAO : rdb.getRentalsNonPaged()) {
            rdb.delRental(rentalDAO);

            count++;
            if (count % 100 == 0) {
                System.out.println(count);
            }
        }
        System.out.println(count);
    }

    public static void main(String[] args) {
        AzureProperties.setLocalKeys();
        getInstances();
        double startTime = System.currentTimeMillis();

        clearUsers();
        clearHouses();
        clearRentals();

        double endTime = System.currentTimeMillis();
        double executionTime = (endTime - startTime) / (60 * 1000);

        System.out.println("Execution time: " + executionTime + " minutes");
    }


}
