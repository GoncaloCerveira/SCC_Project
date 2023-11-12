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

        CompletableFuture<List<UserDAO>> asyncUserDB = CompletableFuture.supplyAsync(() -> {
            CosmosPagedIterable<UserDAO> userDB = udb.getUserById(id);
            writeToCache("getUserById", id, userDB);
            return userDB.stream().toList();
        });

        if(user != null) {
            return user;
        }

        return asyncUserDB.join();
    }

    public List<UserDAO> getUsers() {
        List<UserDAO> users = readFromCache("getUsers", "", new TypeReference<>() {});

        CompletableFuture<List<UserDAO>> asyncUsersDB = CompletableFuture.supplyAsync(() -> {
            CosmosPagedIterable<UserDAO> usersDB = udb.getUsers();
            writeToCache("getUsers", "", usersDB);
            return usersDB.stream().toList();
        });

        if(users != null) {
            return users;
        }

        return asyncUsersDB.join();
    }


}
