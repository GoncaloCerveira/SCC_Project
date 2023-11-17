package cache;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.core.type.TypeReference;
import data.rental.RentalDAO;
import db.CosmosDBRentalsLayer;

import java.util.List;

public class RentalsCache extends RedisCache {
    private static final CosmosDBRentalsLayer rdb = CosmosDBRentalsLayer.getInstance();

    public static List<RentalDAO> getRentalById(String id) {
        String key = id;
        List<RentalDAO> rental = readFromCache("getRentalById", key, new TypeReference<>() {});

        if(rental != null) {
            return rental;
        }

        CosmosPagedIterable<RentalDAO> rentalDB = rdb.getRentalById(id);
        if(rentalDB.iterator().hasNext()) {
            writeToCache("getRentalById", key, rentalDB);
        }
        return rentalDB.stream().toList();
    }

    public static List<RentalDAO> getHouseRentalsByDate(String houseId, String initDate, String endDate) {
        String id = houseId + initDate + endDate;
        List<RentalDAO> rental = readFromCache("getHouseRentalsByDate", id, new TypeReference<>() {});

        if(rental != null) {
            return rental;
        }

        CosmosPagedIterable<RentalDAO> rentalDB = rdb.getHouseRentalsByDate(houseId, initDate, endDate);
        if(rentalDB.iterator().hasNext()) {
            writeToCache("getHouseRentalsByDate", id, rentalDB);
        }
        return rentalDB.stream().toList();
    }

    public static List<RentalDAO> getRentals(String st, String len) {
        String key = st + len;
        List<RentalDAO> rentals = readFromCache("getRentals", key, new TypeReference<>() {});

        if(rentals != null) {
            return rentals;
        }

        CosmosPagedIterable<RentalDAO> rentalsDB = rdb.getRentals(st, len);
        if(rentalsDB.iterator().hasNext()) {
            writeToCache("getRentals", key, rentalsDB);
        }
        return rentalsDB.stream().toList();
    }

    public static List<RentalDAO> getHouseRentals(String st, String len, String houseId) {
        String key = st + len + houseId;
        List<RentalDAO> rentals = readFromCache("getHouseRentals", key, new TypeReference<>() {});

        if(rentals != null) {
            return rentals;
        }

        CosmosPagedIterable<RentalDAO> rentalsDB = rdb.getHouseRentals(st, len, houseId);
        if(rentalsDB.iterator().hasNext()) {
            writeToCache("getHouseRentals", key, rentalsDB);
        }
        return rentalsDB.stream().toList();
    }

    public static List<RentalDAO> getUserRentals(String st, String len, String userId) {
        String key = st + len + userId;
        List<RentalDAO> rentals = readFromCache("getUserRentals", key, new TypeReference<>() {});

        if(rentals != null) {
            return rentals;
        }

        CosmosPagedIterable<RentalDAO> rentalsDB = rdb.getUserRentals(st, len, userId);
        if(rentalsDB.iterator().hasNext()) {
            writeToCache("getUserRentals", key, rentalsDB);
        }
        return rentalsDB.stream().toList();
    }

    public static List<RentalDAO> getFreeSlots(boolean isFree) {
        String key = String.valueOf(isFree);
        List<RentalDAO> rentals = readFromCache("getFreeSlots", key, new TypeReference<>() {});

        if(rentals != null) {
            return rentals;
        }

        CosmosPagedIterable<RentalDAO> rentalsDB = rdb.getFreeSlots(isFree);
        if(rentalsDB.iterator().hasNext()) {
            writeToCache("getFreeSlots", key, rentalsDB);
        }
        return rentalsDB.stream().toList();
    }

    public static List<String> getHouseIdsByPeriodLocation(String st, String len, String initDate, String endDate, String location) {
        String key = st + len + initDate + endDate;
        List<String> houseIds = readFromCache("getHouseIdsByPeriodLocation", key, new TypeReference<>() {});

        if(houseIds != null) {
            return houseIds;
        }

        CosmosPagedIterable<String> houseIdsDB = rdb.getHouseIdsByPeriodLocation(st, len, initDate, endDate, location);
        if(houseIdsDB.iterator().hasNext()) {
            writeToCache("getHouseIdsByPeriodLocation", key, houseIdsDB);
        }
        return houseIdsDB.stream().toList();
    }


}
