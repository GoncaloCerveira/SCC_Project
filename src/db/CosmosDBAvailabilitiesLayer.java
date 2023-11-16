package db;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.implementation.NotFoundException;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import data.availability.AvailabilityDAO;
import data.house.HouseDAO;
import data.rental.RentalDAO;
import utils.AzureProperties;

public class CosmosDBAvailabilitiesLayer {
    private static CosmosDBAvailabilitiesLayer instance;

    public static synchronized CosmosDBAvailabilitiesLayer getInstance() {
        if( instance != null)
            return instance;

        CosmosClient client = CosmosDB.createClient();
        instance = new CosmosDBAvailabilitiesLayer(client);
        return instance;

    }

    private final CosmosClient client;
    private CosmosDatabase db;
    private CosmosContainer availabilities;

    public CosmosDBAvailabilitiesLayer(CosmosClient client) {
        this.client = client;
    }

    private synchronized void init() {
        if( db != null)
            return;
        db = client.getDatabase(AzureProperties.DB_NAME);
        availabilities = db.getContainer("availability");

    }

    public CosmosItemResponse<Object> delAvailability(AvailabilityDAO availability) {
        init();
        return availabilities.deleteItem(availability, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<AvailabilityDAO> postAvailability(AvailabilityDAO availability) {
        init();
        CosmosItemResponse<AvailabilityDAO> res = availabilities.createItem(availability);
        if(res.getStatusCode()<300)
            return res;
        else throw new NotFoundException();
    }

    public CosmosItemResponse<AvailabilityDAO> putAvailability(AvailabilityDAO availability) {
        init();
        CosmosItemResponse<AvailabilityDAO> res = availabilities.replaceItem(availability, availability.getId(), new PartitionKey(availability.getHouseId()), new CosmosItemRequestOptions());
        if(res.getStatusCode()<300)
            return res;
        else throw new NotFoundException();
    }

    public CosmosPagedIterable<String> getHouseIdByPeriodLocation(String len, String st, String initDate, String endDate) {
        init();
        return availabilities.queryItems("SELECT availabilities.houseId FROM availabilities WHERE availabilities.fromDate >= " + initDate + " AND rentals.endDate <= " + endDate + "\" LIMIT " + len + " OFFSET " + st, new CosmosQueryRequestOptions(), String.class);
    }

    public void close() {
        client.close();
    }

}