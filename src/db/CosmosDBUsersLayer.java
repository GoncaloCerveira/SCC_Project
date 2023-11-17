package db;

import com.azure.cosmos.*;
import com.azure.cosmos.implementation.NotFoundException;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import data.user.UserDAO;
import utils.AzureProperties;

public class CosmosDBUsersLayer {
	private static CosmosDBUsersLayer instance;
	private final CosmosClient client;
	private static CosmosDatabase db;
	private CosmosContainer users;

	public static synchronized CosmosDBUsersLayer getInstance() {
		if( instance != null)
			return instance;

		CosmosClient client = CosmosDB.createClient();
		instance = new CosmosDBUsersLayer(client);

		return instance;

	}

	public CosmosDBUsersLayer(CosmosClient client) {
		this.client = client;
	}

	private synchronized void init() {
		if( db != null)
			return;
		db = client.getDatabase(AzureProperties.DB_NAME);
		users = db.getContainer("users");

	}

	public CosmosItemResponse<Object> delUserById(String id) {
		init();
		PartitionKey key = new PartitionKey( id);
		return users.deleteItem(id, key, new CosmosItemRequestOptions());
	}

	public CosmosItemResponse<Object> delUser(UserDAO user) {
		init();
		return users.deleteItem(user, new CosmosItemRequestOptions());
	}

	public CosmosItemResponse<UserDAO> postUser(UserDAO user) {
		init();
		CosmosItemResponse<UserDAO> res = users.createItem(user);
		if(res.getStatusCode()<300)
			return res;
		else throw new NotFoundException();
	}

	public CosmosItemResponse<UserDAO> putUser(UserDAO user) {
		init();
		CosmosItemResponse<UserDAO> res = users.replaceItem(user, user.getId(), new PartitionKey(user.getId()), new CosmosItemRequestOptions());
		if(res.getStatusCode() < 300)
			return res;
		else throw new NotFoundException();
	}

	public CosmosPagedIterable<UserDAO> getUserById(String id) {
		init();
		String query = "SELECT * FROM users WHERE users.id=\"" + id + "\"";
		return users.queryItems(query, new CosmosQueryRequestOptions(), UserDAO.class);
	}

	public CosmosPagedIterable<UserDAO> getUsers(String st, String len) {
		init();
		String query = "SELECT * FROM users";
		if(st != null && len != null) {
			query = query + " OFFSET " + st + " LIMIT " + len;
		}
		return users.queryItems(query, new CosmosQueryRequestOptions(), UserDAO.class);
	}

	public void close() {
		client.close();
	}


}