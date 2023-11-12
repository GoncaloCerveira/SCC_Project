package cache;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.core.type.TypeReference;
import data.user.User;
import data.user.UserDAO;
import db.CosmosDBUsersLayer;

import java.util.List;

public class UsersCache extends RedisCache {
    private static final CosmosDBUsersLayer udb = CosmosDBUsersLayer.getInstance();

    public static List<UserDAO> getUserById(String id) {
        CosmosPagedIterable<UserDAO> userDB = udb.getUserById(id);
        writeToCache("getUserById", id, userDB);
        List<UserDAO> user = readFromCache("getUserById", id, new TypeReference<>() {});

        return user;
    }

    public List<UserDAO> getUsers() {
        CosmosPagedIterable<UserDAO> usersDB = udb.getUsers();
        writeToCache("getUsers", "", usersDB);
        List<UserDAO> users = readFromCache("getUsers", "", new TypeReference<>() {});

        return users;
    }


}
