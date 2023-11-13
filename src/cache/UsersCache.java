package cache;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.core.type.TypeReference;
import data.user.User;
import data.user.UserDAO;
import db.CosmosDBUsersLayer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UsersCache extends RedisCache {
    private static final CosmosDBUsersLayer udb = CosmosDBUsersLayer.getInstance();

    public static List<UserDAO> getUserById(String id) {
        List<UserDAO> user = readFromCache("getUserById", id, new TypeReference<>() {});

        if(user != null) {
            return user;
        }

        CosmosPagedIterable<UserDAO> userDB = udb.getUserById(id);
        if(userDB.iterator().hasNext()) {
            writeToCache("getUserById", id, userDB);
        }
        return userDB.stream().toList();
    }

    public List<UserDAO> getUsers() {
        List<UserDAO> users = readFromCache("getUsers", "", new TypeReference<>() {});

        if(users != null) {
            return users;
        }

        CosmosPagedIterable<UserDAO> usersDB = udb.getUsers();
        if(usersDB.iterator().hasNext()) {
            writeToCache("getUsers", "", usersDB);
        }
        return usersDB.stream().toList();
    }


}
