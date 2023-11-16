package cache;

import com.azure.cosmos.util.CosmosPagedIterable;
import data.authentication.Session;
import data.authentication.SessionDAO;
import db.CosmosDBQuestionsLayer;
import db.CosmosDBSessionsLayer;

import java.util.Iterator;

public class AuthCache extends  RedisCache {

    private static final CosmosDBSessionsLayer sdb = CosmosDBSessionsLayer.getInstance();

    public static void putSession(Session session) {
        if(USE_CACHE) {
            writeToCache("Session", session.getId(), session);
        } else {
            SessionDAO sessionDB = new SessionDAO(session);
            sdb.postSession(sessionDB);
        }
    }

    public static Session getSession(String uid) {
        if(USE_CACHE) {
            return readFromCache("Session", uid, Session.class);
        } else {
            Iterator<SessionDAO> results = sdb.getSessionById(uid).iterator();
            if(results.hasNext()) {
                SessionDAO sessionDB = results.next();
                return new Session(sessionDB.getId(), sessionDB.getName());
            }
            return null;
        }
    }
}
