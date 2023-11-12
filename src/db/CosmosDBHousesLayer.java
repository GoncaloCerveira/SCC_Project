package db;

import com.azure.cosmos.*;
import com.azure.cosmos.implementation.NotFoundException;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import data.house.HouseDAO;
import utils.AzureProperties;

public class CosmosDBHousesLayer {
	private static CosmosDBHousesLayer instance;

	public static synchronized CosmosDBHousesLayer getInstance() {
		if( instance != null)
			return instance;

		CosmosClient client = CosmosDB.createClient();
		instance = new CosmosDBHousesLayer(client);
		return instance;

	}

	private final CosmosClient client;
	private CosmosDatabase db;
	private CosmosContainer houses;

	public CosmosDBHousesLayer(CosmosClient client) {
		this.client = client;
	}
	
	private synchronized void init() {
		if( db != null)
			return;
		db = client.getDatabase(AzureProperties.DB_NAME);
		houses = db.getContainer("houses");
		
	}

	public CosmosItemResponse<Object> delHouseById(String id) {
		init();
		return houses.deleteItem(id, new CosmosItemRequestOptions());
	}

	public CosmosItemResponse<Object> delHouse(HouseDAO house) {
		init();
		return houses.deleteItem(house, new CosmosItemRequestOptions());
	}

	public CosmosItemResponse<HouseDAO> postHouse(HouseDAO house) {
		init();
		CosmosItemResponse<HouseDAO> res = houses.createItem(house);
		if(res.getStatusCode()<300)
			return res;
		else throw new NotFoundException();
	}
	
	public CosmosItemResponse<HouseDAO> putHouse(HouseDAO house) {
		init();
		CosmosItemResponse<HouseDAO> res = houses.replaceItem(house, house.getId(), new PartitionKey(house.getLocation()), new CosmosItemRequestOptions());
		if(res.getStatusCode()<300)
			return res;
		else throw new NotFoundException();
	}
	
	public CosmosPagedIterable<HouseDAO> getHouseById(String id) {
		init();
		return houses.queryItems("SELECT * FROM houses WHERE houses.id=\"" + id + "\"", new CosmosQueryRequestOptions(), HouseDAO.class);
	}

	public CosmosPagedIterable<HouseDAO> getHouses() {
		init();
		return houses.queryItems("SELECT * FROM houses ", new CosmosQueryRequestOptions(), HouseDAO.class);
	}

	public CosmosPagedIterable<HouseDAO> getUserHouses(String ownerId) {
		init();
		return houses.queryItems("SELECT * FROM houses WHERE houses.ownerId=\"" + ownerId + "\"", new CosmosQueryRequestOptions(), HouseDAO.class);
	}

	public CosmosPagedIterable<HouseDAO> getHousesByLocation(String location) {
		init();
		return houses.queryItems("SELECT * FROM houses WHERE houses.location=\"" + location + "\"", new CosmosQueryRequestOptions(), HouseDAO.class);
	}

	public CosmosPagedIterable<HouseDAO> getHousesByLocationForPeriod(String location, String startDate, String endDate) {
		init();
		return houses.queryItems("SELECT * FROM houses WHERE houses.location=\"" + location + "\"", new CosmosQueryRequestOptions(), HouseDAO.class);
	}

	public void close() {
		client.close();
	}
	
	
}
