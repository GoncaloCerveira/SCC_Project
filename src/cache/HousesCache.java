package cache;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.core.type.TypeReference;
import data.house.HouseDAO;
import db.CosmosDBHousesLayer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HousesCache extends AuthCache{

    private static final CosmosDBHousesLayer hdb = CosmosDBHousesLayer.getInstance();

    public static List<HouseDAO> getHouseById(String id) {
        List<HouseDAO> house = readFromCache("getHouseById", id, new TypeReference<>() {});

        if(house != null) {
            return house;
        }

        CosmosPagedIterable<HouseDAO> houseDB = hdb.getHouseById(id);
        if(houseDB.iterator().hasNext()) {
            writeToCache("getHouseById", id, houseDB);
        }

        return houseDB.stream().toList();
    }

    public static List<HouseDAO> getHouses() {
        List<HouseDAO> houses = readFromCache("getHouses", "", new TypeReference<>() {});

        if(houses != null) {
            return houses;
        }

        CosmosPagedIterable<HouseDAO> housesDB = hdb.getHouses();
        if(housesDB.iterator().hasNext()) {
            writeToCache("getHouses", "", housesDB);
        }

        return housesDB.stream().toList();
    }

    public static List<HouseDAO> getUserHouses(String ownerId) {
        List<HouseDAO> houses = readFromCache("getUserHouses", ownerId, new TypeReference<>() {});

        if(houses != null) {
            return houses;
        }

        CosmosPagedIterable<HouseDAO> housesDB = hdb.getHouses();
        if(housesDB.iterator().hasNext()) {
            writeToCache("getUserHouses", ownerId, housesDB);
        }

        return housesDB.stream().toList();
    }

    public static List<HouseDAO> getHousesByLocation(String location) {
        List<HouseDAO> houses = readFromCache("getHousesByLocation", location, new TypeReference<>() {});

        if(houses != null) {
            return houses;
        }

        CosmosPagedIterable<HouseDAO> housesDB = hdb.getHouses();
        if(housesDB.iterator().hasNext()) {
            writeToCache("getHousesByLocation", location, housesDB);
        }

        return housesDB.stream().toList();
    }


}
