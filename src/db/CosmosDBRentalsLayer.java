package db;

import com.azure.cosmos.*;
import com.azure.cosmos.implementation.NotFoundException;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
// TODO
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
        CosmosItemResponse<RentalDAO> res = rentals.replaceItem(rental, rental.getId(), new PartitionKey(rental.getId()), new CosmosItemRequestOptions());
        if(res.getStatusCode()<300)
            return res;
        else throw new NotFoundException();
    }

    public CosmosPagedIterable<RentalDAO> getRentalById(String id) {
        init();
        return rentals.queryItems("SELECT * FROM rentals WHERE rentals.id=\"" + id + "\"", new CosmosQueryRequestOptions(), RentalDAO.class);
    }

    public CosmosPagedIterable<RentalDAO> getHouseRentalByDate(String houseId, int startDate, int endDate) {
        init();
        return rentals.queryItems("SELECT * FROM rentals WHERE rentals.houseid=\"" + houseId + "\" AND rentals.startDate <= " + endDate + " AND rentals.endDate >= " + startDate, new CosmosQueryRequestOptions(), RentalDAO.class);
    }

    public CosmosPagedIterable<String> getRentalsHouseIdsByDate(String initDate, String endDate) {
        init();
        return rentals.queryItems("SELECT rentals.houseid FROM rentals WHERE rentals.startDate >= " + initDate + " AND rentals.endDate <= " + endDate, new CosmosQueryRequestOptions(), String.class);
    }

    public CosmosPagedIterable<RentalDAO> getRentals(String len, String st) {
        init();
        return rentals.queryItems("SELECT * FROM rentals LIMIT " + st + " OFFSET " + len, new CosmosQueryRequestOptions(), RentalDAO.class);
    }

    public CosmosPagedIterable<RentalDAO> getHouseRentals(String len, String st, String houseId) {
        init();
        return rentals.queryItems("SELECT * FROM rentals where rentals.houseid=\"" + houseId + "\" LIMIT " + st + " OFFSET " + len, new CosmosQueryRequestOptions(), RentalDAO.class);
    }

    public CosmosPagedIterable<RentalDAO> getUserRentals(String len, String st, String userId) {
        init();
        return rentals.queryItems("SELECT * FROM rentals where rentals.userId=\"" + userId + "\" LIMIT " + st + " OFFSET " + len, new CosmosQueryRequestOptions(), RentalDAO.class);
    }

    public void close() {
        client.close();
    }


}
