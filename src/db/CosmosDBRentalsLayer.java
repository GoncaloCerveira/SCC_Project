package db;

import com.azure.cosmos.*;
import com.azure.cosmos.implementation.NotFoundException;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import data.rental.RentalDAO;
import utils.AzureProperties;

public class CosmosDBRentalsLayer {
    private static CosmosDBRentalsLayer instance;

    public static synchronized CosmosDBRentalsLayer getInstance() {
        if( instance != null)
            return instance;

        CosmosClient client = CosmosDB.createClient();
        instance = new CosmosDBRentalsLayer(client);
        return instance;

    }

    private CosmosClient client;
    private CosmosDatabase db;
    private CosmosContainer rentals;

    public CosmosDBRentalsLayer(CosmosClient client) {
        this.client = client;
    }

    private synchronized void init() {
        if( db != null)
            return;
        db = client.getDatabase(AzureProperties.DB_NAME);
        rentals = db.getContainer("rentals");

    }

    public CosmosItemResponse<Object> delRentalById(String id) {
        init();
        PartitionKey key = new PartitionKey( id);
        return rentals.deleteItem(id, key, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<Object> delRental(RentalDAO rental) {
        init();
        return rentals.deleteItem(rental, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<RentalDAO> postRental(RentalDAO rental) {
        init();
        CosmosItemResponse<RentalDAO> res = rentals.createItem(rental);
        if(res.getStatusCode()<300)
            return res;
        else throw new NotFoundException();
    }

    public CosmosItemResponse<RentalDAO> putRental(RentalDAO rental) {
        init();
        CosmosItemResponse<RentalDAO> res = rentals.replaceItem(rental, rental.getId(), new PartitionKey(rental.getHouse()), new CosmosItemRequestOptions());
        if(res.getStatusCode()<300)
            return res;
        else throw new NotFoundException();
    }

    public CosmosPagedIterable<RentalDAO> getRentalById(String id) {
        init();
        String query = "SELECT * FROM rentals WHERE rentals.id=\"" + id + "\"";
        return rentals.queryItems(query, new CosmosQueryRequestOptions(), RentalDAO.class);
    }

    public CosmosPagedIterable<RentalDAO> getHouseRentalsByDate(String houseId, String initDate, String endDate) {
        init();
        String query = "SELECT * FROM rentals WHERE rentals.house=\"" + houseId + "\" AND rentals.fromDate>=\"" + initDate + "\" AND rentals.toDate<=\"" + endDate + "\"";
        return rentals.queryItems(query, new CosmosQueryRequestOptions(), RentalDAO.class);
    }

    public CosmosPagedIterable<RentalDAO> getRentals(String st, String len) {
        init();
        String query = "SELECT * FROM rentals";
        if(st != null && len != null) {
            query = query + " OFFSET " + st + " LIMIT " + len;
        }
        return rentals.queryItems(query, new CosmosQueryRequestOptions(), RentalDAO.class);
    }

    public CosmosPagedIterable<RentalDAO> getHouseRentals(String st, String len, String houseId) {
        init();
        String query = "SELECT * FROM rentals where rentals.house=\"" + houseId + "\"";
        if(st != null && len != null) {
            query = query + " OFFSET " + st + " LIMIT " + len;
        }
        return rentals.queryItems(query, new CosmosQueryRequestOptions(), RentalDAO.class);
    }

    public CosmosPagedIterable<RentalDAO> getUserRentals(String st, String len, String userId) {
        init();
        String query = "SELECT * FROM rentals where rentals.user=\"" + userId + "\"";
        if(st != null && len != null) {
            query = query + " OFFSET " + st + " LIMIT " + len;
        }
        return rentals.queryItems(query, new CosmosQueryRequestOptions(), RentalDAO.class);
    }

    public CosmosPagedIterable<RentalDAO> getFreeSlots(boolean isFree) {
        init();
        String query = "SELECT * FROM rentals WHERE rentals.free=" + isFree;
        return rentals.queryItems(query, new CosmosQueryRequestOptions(), RentalDAO.class);
    }

    public CosmosPagedIterable<String> getHouseIdsByPeriodLocation(String st, String len, String initDate, String endDate, String location) {
        init();
        String query = "SELECT rentals.houseId FROM rentals WHERE rentals.fromDate>=\"" + initDate + "\" AND rentals.endDate<=\"" + endDate + "\" AND rentals.location=\"" + location + "\"";
        if(st != null && len != null) {
            query = query + " OFFSET " + st + " LIMIT " + len;
        }
        return rentals.queryItems(query, new CosmosQueryRequestOptions(), String.class);
    }

    public void close() {
        client.close();
    }


}
