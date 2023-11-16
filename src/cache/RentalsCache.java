package cache;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.core.type.TypeReference;
import data.rental.RentalDAO;
import db.CosmosDBRentalsLayer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RentalsCache extends RedisCache {
    private static final CosmosDBRentalsLayer rdb = CosmosDBRentalsLayer.getInstance();

    public static List<RentalDAO> getRentalById(String id) {
        List<RentalDAO> rental = readFromCache("getRentalById", id, new TypeReference<>() {});

        if(rental != null) {
            return rental;
        }

        CosmosPagedIterable<RentalDAO> rentalDB = rdb.getRentalById(id);
        if(rentalDB.iterator().hasNext()) {
            writeToCache("getQuestionById", id, rentalDB);
        }
        return rentalDB.stream().toList();
    }

    public static List<RentalDAO> getHouseRentalByDate(String houseId, int startDate, int endDate) {
        String id = houseId + "#" + startDate + "#" + endDate;
        List<RentalDAO> rental = readFromCache("getHouseRentalByDate", id, new TypeReference<>() {});

        if(rental != null) {
            return rental;
        }

        CosmosPagedIterable<RentalDAO> rentalDB = rdb.getHouseRentalByDate(houseId, startDate, endDate);
        if(rentalDB.iterator().hasNext()) {
            writeToCache("getHouseRentalByDate", id, rentalDB);
        }
        return rentalDB.stream().toList();
    }

    public static List<String> getRentalsHouseIdsByDate(String startDate, String endDate) {
        String key = startDate + endDate;
        List<String> houseIds = readFromCache("getRentalsHouseIdsByDate", key, new TypeReference<>() {});

        if(houseIds != null) {
            return houseIds;
        }

        CosmosPagedIterable<String> houseIdsDB = rdb.getRentalsHouseIdsByDate(startDate, endDate);
        if(houseIdsDB.iterator().hasNext()) {
            writeToCache("getRentalsHouseIdsByDate", key, houseIdsDB);
        }
        return houseIdsDB.stream().toList();
    }

    public static List<RentalDAO> getRentals(String st, String len) {
        List<RentalDAO> rentals = readFromCache("getRentals", "", new TypeReference<>() {});

        if(rentals != null) {
            return rentals;
        }

        CosmosPagedIterable<RentalDAO> rentalsDB = rdb.getRentals(st, len);
        if(rentalsDB.iterator().hasNext()) {
            writeToCache("getRentals", "", rentalsDB);
        }
        return rentalsDB.stream().toList();
    }

    public static List<RentalDAO> getHouseRentals(String st, String len, String houseId) {
        List<RentalDAO> rentals = readFromCache("getHouseRentals", houseId, new TypeReference<>() {});

        if(rentals != null) {
            return rentals;
        }

        CosmosPagedIterable<RentalDAO> rentalsDB = rdb.getHouseRentals(st, len, houseId);
        if(rentalsDB.iterator().hasNext()) {
            writeToCache("getHouseRentals", houseId, rentalsDB);
        }
        return rentalsDB.stream().toList();
    }

    public static List<RentalDAO> getUserRentals(String st, String len, String userId) {
        List<RentalDAO> rentals = readFromCache("getUserRentals", userId, new TypeReference<>() {});

        if(rentals != null) {
            return rentals;
        }

        CosmosPagedIterable<RentalDAO> rentalsDB = rdb.getUserRentals(st, len, userId);
        if(rentalsDB.iterator().hasNext()) {
            writeToCache("getUserRentals", userId, rentalsDB);
        }
        return rentalsDB.stream().toList();
    }


}
