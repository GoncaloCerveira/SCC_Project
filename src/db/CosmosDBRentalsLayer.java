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

public class CosmosDBRentalsLayer {
    private static final String CONNECTION_URL = "https://sccproject1.documents.azure.com:443/";
    private static final String DB_KEY = "oHSKcUrbfonJWUhvlU1vF93pZX4Q3q9s2DYoGH4uD5LA0S6iFa94ZU5XfhtnovCZM7dx8sB03lnIACDbXX66dw==";
    private static final String DB_NAME = "sccproject1";

    private static CosmosDBRentalsLayer instance;

    public static synchronized CosmosDBRentalsLayer getInstance() {
        if( instance != null)
            return instance;

        CosmosClient client = new CosmosClientBuilder()
                .endpoint(CONNECTION_URL)
                .key(DB_KEY)
                //.directMode() comment this if not to use direct mode
                .gatewayMode()
                // replace by .directMode() for better performance
                .consistencyLevel(ConsistencyLevel.SESSION)
                .connectionSharingAcrossClientsEnabled(true)
                .contentResponseOnWriteEnabled(true) // on write return the object written
                .buildClient();
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
        db = client.getDatabase(DB_NAME);
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

    public CosmosItemResponse<RentalDAO> putRental(RentalDAO rental) {
        init();
        CosmosItemResponse<RentalDAO> res = rentals.createItem(rental);
        if(res.getStatusCode()<300)
            return res;
        else throw new NotFoundException();
        //return rentals.createItem(rentals);
    }

    public CosmosPagedIterable<RentalDAO> getRentalById(String id) {
        init();
        return rentals.queryItems("SELECT * FROM rentals WHERE rentals.id=\"" + id + "\"", new CosmosQueryRequestOptions(), RentalDAO.class);
    }

    public CosmosPagedIterable<RentalDAO> getRentals() {
        init();
        return rentals.queryItems("SELECT * FROM rentals ", new CosmosQueryRequestOptions(), RentalDAO.class);
    }

    public void close() {
        client.close();
    }

}
