package cache;

import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.core.type.TypeReference;
import data.user.UserDAO;
import db.CosmosDBUsersLayer;

public class UsersCache extends RedisCache {
    private static final CosmosDBUsersLayer udb = CosmosDBUsersLayer.getInstance();

    public static CosmosPagedIterable<UserDAO> getUserById(String id) {
        CosmosPagedIterable<UserDAO> user = readFromCache("getUserById", id, new TypeReference<>() {});

        if(user != null) {
            return user;
        }

        user = udb.getUserById(id);
        if(user.iterator().hasNext()) {
            writeToCache("getUserById", id, user);
        }

        return user;
    }

    public CosmosPagedIterable<UserDAO> getUsers() {
        CosmosPagedIterable<UserDAO> users = readFromCache("getUsers", "", new TypeReference<>() {});

        if(users != null) {
            return users;
        }

        users = udb.getUsers();
        if(users.iterator().hasNext()) {
            writeToCache("getUsers", "", users);
        }

        return users;
    }

}
