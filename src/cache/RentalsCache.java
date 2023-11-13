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

        CompletableFuture<List<RentalDAO>> asyncRentalDB = CompletableFuture.supplyAsync(() -> {
            CosmosPagedIterable<RentalDAO> rentalDB = rdb.getRentalById(id);
            writeToCache("getRentalById", id, rentalDB);
            return rentalDB.stream().toList();
        });

        if(rental != null) {
            return rental;
        }

        return asyncRentalDB.join();
    }

    public static List<RentalDAO> getHouseRentalByDate(String houseId, int startDate, int endDate) {
        String id = houseId + "#" + startDate + "#" + endDate;
        List<RentalDAO> rental = readFromCache("getHouseRentalByDate", id, new TypeReference<>() {});

        CompletableFuture<List<RentalDAO>> asyncRentalDB = CompletableFuture.supplyAsync(() -> {
            CosmosPagedIterable<RentalDAO> rentalDB = rdb.getHouseRentalByDate(houseId, startDate, endDate);
            writeToCache("getHouseRentalByDate", id, rentalDB);
            return rentalDB.stream().toList();
        });

        if(rental != null) {
            return rental;
        }

        return asyncRentalDB.join();
    }

    public static List<RentalDAO> getRentals() {
        List<RentalDAO> rentals = readFromCache("getRentals", "", new TypeReference<>() {});

        CompletableFuture<List<RentalDAO>> asyncRentalsDB = CompletableFuture.supplyAsync(() -> {
            CosmosPagedIterable<RentalDAO> rentalsDB = rdb.getRentals();
            writeToCache("getRentals", "", rentalsDB);
            return rentalsDB.stream().toList();
        });

        if(rentals != null) {
            return rentals;
        }

        return asyncRentalsDB.join();
    }

    public static List<RentalDAO> getHouseRentals(String houseId) {
        List<RentalDAO> rentals = readFromCache("getHouseRentals", houseId, new TypeReference<>() {});

        CompletableFuture<List<RentalDAO>> asyncRentalsDB = CompletableFuture.supplyAsync(() -> {
            CosmosPagedIterable<RentalDAO> rentalsDB = rdb.getHouseRentals(houseId);
            writeToCache("getHouseRentals", houseId, rentalsDB);
            return rentalsDB.stream().toList();
        });

        if(rentals != null) {
            return rentals;
        }

        return asyncRentalsDB.join();
    }


}
