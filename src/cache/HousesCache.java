package cache;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
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

    public static List<HouseDAO> getHouses(String st, String len) {
        List<HouseDAO> houses = readFromCache("getHouses", "", new TypeReference<>() {});

        if(houses != null) {
            return houses;
        }

        CosmosPagedIterable<HouseDAO> housesDB = hdb.getHouses(st, len);
        if(housesDB.iterator().hasNext()) {
            writeToCache("getHouses", "", housesDB);
        }

        return housesDB.stream().toList();
    }

    public static List<HouseDAO> getHousesById(String st, String len, List<String> ids) {
        Gson gson = new Gson();
        String keys = gson.toJson(ids);
        List<HouseDAO> houses = readFromCache("getHousesById", keys, new TypeReference<>() {});

        if(houses != null) {
            return houses;
        }

        CosmosPagedIterable<HouseDAO> housesDB = hdb.getHousesById(st, len, ids);
        if(housesDB.iterator().hasNext()) {
            writeToCache("getHousesById", keys, housesDB);
        }

        return housesDB.stream().toList();
    }

    public static List<HouseDAO> getUserHouses(String st, String len, String ownerId) {
        List<HouseDAO> houses = readFromCache("getUserHouses", ownerId, new TypeReference<>() {});

        if(houses != null) {
            return houses;
        }

        CosmosPagedIterable<HouseDAO> housesDB = hdb.getUserHouses(st, len, ownerId);
        if(housesDB.iterator().hasNext()) {
            writeToCache("getUserHouses", ownerId, housesDB);
        }

        return housesDB.stream().toList();
    }

    public static List<HouseDAO> getHousesByLocation(String st, String len, String location) {
        List<HouseDAO> houses = readFromCache("getHousesByLocation", location, new TypeReference<>() {});

        if(houses != null) {
            return houses;
        }

        CosmosPagedIterable<HouseDAO> housesDB = hdb.getHousesByLocation(st, len, location);
        if(housesDB.iterator().hasNext()) {
            writeToCache("getHousesByLocation", location, housesDB);
        }

        return housesDB.stream().toList();
    }

    public static List<HouseDAO> getHouseDiscounts(String st, String len) {
        String key = st + len;
        List<HouseDAO> houses = readFromCache("getHouseDiscounts", key, new TypeReference<>() {});

        if(houses != null) {
            return houses;
        }

        CosmosPagedIterable<HouseDAO> housesDB = hdb.getHouseDiscounts(st, len);
        if(housesDB.iterator().hasNext()) {
            writeToCache("getHouseDiscounts", key, housesDB);
        }

        return housesDB.stream().toList();
    }


}
