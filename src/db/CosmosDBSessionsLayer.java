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
import data.authentication.SessionDAO;
import data.availability.AvailabilityDAO;
import data.user.UserDAO;
import utils.AzureProperties;

public class CosmosDBSessionsLayer {
    private static CosmosDBSessionsLayer instance;

    public static synchronized CosmosDBSessionsLayer getInstance() {
        if( instance != null)
            return instance;

        CosmosClient client = CosmosDB.createClient();
        instance = new CosmosDBSessionsLayer(client);
        return instance;

    }

    private final CosmosClient client;
    private CosmosDatabase db;
    private CosmosContainer sessions;

    public CosmosDBSessionsLayer(CosmosClient client) {
        this.client = client;
    }

    private synchronized void init() {
        if( db != null)
            return;
        db = client.getDatabase(AzureProperties.DB_NAME);
        sessions = db.getContainer("sessions");

    }

    public CosmosItemResponse<Object> delSession(SessionDAO session) {
        init();
        return sessions.deleteItem(session, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<SessionDAO> postSession(SessionDAO session) {
        init();
        CosmosItemResponse<SessionDAO> res = sessions.createItem(session);
        if(res.getStatusCode()<300)
            return res;
        else throw new NotFoundException();
    }

    public CosmosItemResponse<AvailabilityDAO> putSession(AvailabilityDAO availability) {
        init();
        CosmosItemResponse<AvailabilityDAO> res = sessions.replaceItem(availability, availability.getId(), new PartitionKey(availability.getHouseId()), new CosmosItemRequestOptions());
        if(res.getStatusCode()<300)
            return res;
        else throw new NotFoundException();
    }

    public CosmosPagedIterable<SessionDAO> getSessionById(String uid) {
        init();
        return sessions.queryItems("SELECT * FROM sessions WHERE sessions.id=\"" + uid + "\"", new CosmosQueryRequestOptions(), SessionDAO.class);
    }

    public void close() {
        client.close();
    }
}
