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

        CompletableFuture<List<HouseDAO>> asyncHouseDB = CompletableFuture.supplyAsync(() -> {
            CosmosPagedIterable<HouseDAO> houseDB = hdb.getHouseById(id);
            writeToCache("getHouseById", id, houseDB);
            return houseDB.stream().toList();
        });

        if(house != null) {
            return house;
        }

        return asyncHouseDB.join();
    }

    public static List<HouseDAO> getHouses() {
        List<HouseDAO> houses = readFromCache("getHouses", "", new TypeReference<>() {});

        CompletableFuture<List<HouseDAO>> asyncHousesDB = CompletableFuture.supplyAsync(() -> {
            CosmosPagedIterable<HouseDAO> housesDB = hdb.getHouses();
            writeToCache("getHouses", "", housesDB);
            return housesDB.stream().toList();
        });

        if(houses != null) {
            return houses;
        }

        return asyncHousesDB.join();
    }

    public static List<HouseDAO> getUserHouses(String ownerId) {
        List<HouseDAO> houses = readFromCache("getUserHouses", ownerId, new TypeReference<>() {});

        CompletableFuture<List<HouseDAO>> asyncHousesDB = CompletableFuture.supplyAsync(() -> {
            CosmosPagedIterable<HouseDAO> housesDB = hdb.getHouses();
            writeToCache("getUserHouses", ownerId, housesDB);
            return housesDB.stream().toList();
        });

        if(houses != null) {
            return houses;
        }

        return asyncHousesDB.join();
    }

    public static List<HouseDAO> getHousesByLocation(String location) {
        List<HouseDAO> houses = readFromCache("getHousesByLocation", location, new TypeReference<>() {});

        CompletableFuture<List<HouseDAO>> asyncHousesDB = CompletableFuture.supplyAsync(() -> {
            CosmosPagedIterable<HouseDAO> housesDB = hdb.getHouses();
            writeToCache("getHousesByLocation", location, housesDB);
            return housesDB.stream().toList();
        });

        if(houses != null) {
            return houses;
        }

        return asyncHousesDB.join();
    }


}
