package db;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosDatabase;

public class DBClient {
    private static final String CONNECTION_URL = "https://sccproject1.documents.azure.com:443/";
    private static final String DB_KEY = "oHSKcUrbfonJWUhvlU1vF93pZX4Q3q9s2DYoGH4uD5LA0S6iFa94ZU5XfhtnovCZM7dx8sB03lnIACDbXX66dw==";
    public static final String DB_NAME = "sccproject1";

    public DBClient() {}

    public static CosmosClient createClient() {
        return new CosmosClientBuilder()
                .endpoint(CONNECTION_URL)
                .key(DB_KEY)
                //.directMode() comment this if not to use direct mode
                .gatewayMode()
                // replace by .directMode() for better performance
                .consistencyLevel(ConsistencyLevel.SESSION)
                .connectionSharingAcrossClientsEnabled(true)
                .contentResponseOnWriteEnabled(true) // on write return the object written
                .buildClient();
    }

    public static void createContainersIfNotExist() {
        CosmosClient client = createClient();
        CosmosDatabase db = client.getDatabase(DB_NAME);

        db.createContainerIfNotExists("houses", "/id");
        db.createContainerIfNotExists("questions", "/id");
        db.createContainerIfNotExists("rentals", "/id");
        db.createContainerIfNotExists("users", "/id");
    }

}
