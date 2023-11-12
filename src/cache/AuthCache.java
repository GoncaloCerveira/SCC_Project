package cache;

import data.authentication.Session;

public class AuthCache extends  RedisCache {

    public static void putSession(Session session) {
        writeToCache("Session", session.getId(), session);
    }

    public static Session getSession(String uid) {
        return readFromCache("Session", uid, Session.class);
    }
}
