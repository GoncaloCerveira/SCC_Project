package db;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import utils.AzureKeys;

public class CosmosDB {

    public CosmosDB() {}

    public static CosmosClient createClient() {
        return new CosmosClientBuilder()
                .endpoint(AzureKeys.CONNECTION_URL)
                .key(AzureKeys.DB_KEY)
                .directMode()  // comment this if not to use direct mode
                .gatewayMode()
                // replace by .directMode() for better performance
                .consistencyLevel(ConsistencyLevel.SESSION)
                .connectionSharingAcrossClientsEnabled(true)
                .contentResponseOnWriteEnabled(true) // on write return the object written
                .buildClient();
    }


}
