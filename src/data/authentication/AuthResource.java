package data.authentication;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Cookie;

public class AuthResource {
    /**
     * Throws exception if not appropriate user for operation on Hopuse
     */
    public Session checkCookieUser(Cookie session, String id)
            throws NotAuthorizedException {
        if (session == null || session.getValue() == null)
            throw new NotAuthorizedException("No session initialized");
        Session s;
        try {
            s = RedisLayer.getInstance().getSession(session.getValue());
        } catch (CacheException e) {
            throw new NotAuthorizedException("No valid session initialized");
        }
        if (s == null || s.getName() == null || s.getName().length() == 0)
            throw new NotAuthorizedException("No valid session initialized");
        if (!s.getName().equals(id) /*&& !s.getName().equals("admin")*/)
            throw new NotAuthorizedException("Invalid user : " + s.getName());
        return s;
    }
}
