package db;

import com.azure.cosmos.*;
import com.azure.cosmos.implementation.NotFoundException;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import data.house.HouseDAO;

public class CosmosDBHousesLayer {
	private static final String CONNECTION_URL = "https://sccproject1.documents.azure.com:443/";
	private static final String DB_KEY = "oHSKcUrbfonJWUhvlU1vF93pZX4Q3q9s2DYoGH4uD5LA0S6iFa94ZU5XfhtnovCZM7dx8sB03lnIACDbXX66dw==";
	private static final String DB_NAME = "sccproject1";

	private static CosmosDBHousesLayer instance;

	public static synchronized CosmosDBHousesLayer getInstance() {
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
		db = client.getDatabase(DB_NAME);
		houses = db.getContainer("houses");
		
	}

	public CosmosItemResponse<Object> delHouseById(String id) {
		init();
		return houses.deleteItem(id, new CosmosItemRequestOptions());
	}
	
	public CosmosItemResponse<HouseDAO> putHouse(HouseDAO house) {
		init();
		CosmosItemResponse<HouseDAO> res = houses.createItem(house);
		if(res.getStatusCode()<300)
			return res;
		else throw new NotFoundException();
		//return houses.createItem(house);
	}
	
	public CosmosPagedIterable<HouseDAO> getHouseById( String id) {
		init();
		return houses.queryItems("SELECT * FROM houses WHERE houses.id=\"" + id + "\"", new CosmosQueryRequestOptions(), HouseDAO.class);
	}

	public CosmosPagedIterable<HouseDAO> getHouses() {
		init();
		return houses.queryItems("SELECT * FROM houses ", new CosmosQueryRequestOptions(), HouseDAO.class);
	}

	public void close() {
		client.close();
	}
	
	
}
