package utils;

import data.house.HouseDAO;
import data.media.MediaDAO;
import data.user.UserDAO;
import db.*;
import srv.resources.MediaResource;

public class ClearContainers {
    private static final CosmosDBHousesLayer hdb = CosmosDBHousesLayer.getInstance();
    private static final CosmosDBMediaLayer mdb = CosmosDBMediaLayer.getInstance();
    private static final CosmosDBQuestionsLayer qdb = CosmosDBQuestionsLayer.getInstance();
    private static final CosmosDBRentalsLayer rdb = CosmosDBRentalsLayer.getInstance();
    private static final CosmosDBSessionsLayer sdb = CosmosDBSessionsLayer.getInstance();
    private static final CosmosDBUsersLayer udb = CosmosDBUsersLayer.getInstance();
    private static final MediaResource media = new MediaResource();

    private static void clearUsers() {
        for (UserDAO userDAO : udb.getUsers()) {
            udb.delUser(userDAO);
            media.deleteFile("images", userDAO.getPhotoId());
        }
    }

    private static void clearHouses() {
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
    }

    public static void main(String[] args) {
        clearUsers();
        clearHouses();
    }


}
