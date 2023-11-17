package db;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.implementation.NotFoundException;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import data.media.MediaDAO;
import utils.AzureProperties;

public class CosmosDBMediaLayer {
    private static CosmosDBMediaLayer instance;

    public static synchronized CosmosDBMediaLayer getInstance() {
        if( instance != null)
            return instance;

        CosmosClient client = CosmosDB.createClient();
        instance = new CosmosDBMediaLayer(client);
        return instance;

    }

    private final CosmosClient client;
    private CosmosDatabase db;
    private CosmosContainer media;

    public CosmosDBMediaLayer(CosmosClient client) {
        this.client = client;
    }

    private synchronized void init() {
        if( db != null)
            return;
        db = client.getDatabase(AzureProperties.DB_NAME);
        media = db.getContainer("media");

    }

    public CosmosItemResponse<Object> delMediaById(String id) {
        init();
        return media.deleteItem(id, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<Object> delMedia(MediaDAO m) {
        init();
        return media.deleteItem(m, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<MediaDAO> postMedia(MediaDAO m) {
        init();
        CosmosItemResponse<MediaDAO> res = media.createItem(m);
        if(res.getStatusCode()<300)
            return res;
        else throw new NotFoundException();
    }

    public CosmosPagedIterable<MediaDAO> getItemMedia(String st, String len, String itemId) {
        init();
        String query = "SELECT * FROM media WHERE media.item=\"" + itemId + "\"";
        if(st != null && len != null) {
            query = query + " OFFSET " + st + " LIMIT " + len;
        }
        return media.queryItems(query, new CosmosQueryRequestOptions(), MediaDAO.class);
    }

    public void close() {
        client.close();
    }


}
