package db;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.implementation.NotFoundException;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.PartitionKey;
import data.discount.DiscountDAO;
import utils.AzureProperties;

public class CosmosDBDiscountLayer {
    private static CosmosDBDiscountLayer instance;

    public static synchronized CosmosDBDiscountLayer getInstance() {
        if( instance != null)
            return instance;

        CosmosClient client = CosmosDB.createClient();
        instance = new CosmosDBDiscountLayer(client);
        return instance;

    }

    private CosmosClient client;
    private CosmosDatabase db;
    private CosmosContainer discounts;

    public CosmosDBDiscountLayer(CosmosClient client) {
        this.client = client;
    }

    private synchronized void init() {
        if( db != null)
            return;
        db = client.getDatabase(AzureProperties.DB_NAME);
        discounts = db.getContainer("discounts");

    }

    public CosmosItemResponse<Object> delDiscount(DiscountDAO discount) {
        init();
        return discounts.deleteItem(discount, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<DiscountDAO> postDiscount(DiscountDAO discount) {
        init();
        CosmosItemResponse<DiscountDAO> res = discounts.createItem(discount);
        if(res.getStatusCode()<300)
            return res;
        else throw new NotFoundException();
    }

    public CosmosItemResponse<DiscountDAO> putDiscount(DiscountDAO discount) {
        init();
        CosmosItemResponse<DiscountDAO> res = discounts.replaceItem(discount, discount.getId(), new PartitionKey(discount.getHouseId()), new CosmosItemRequestOptions());
        if(res.getStatusCode()<300)
            return res;
        else throw new NotFoundException();
    }

    public void close() {
        client.close();
    }

}