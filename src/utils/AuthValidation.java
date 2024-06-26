package utils;

import cache.AuthCache;
import data.authentication.Session;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Response;

public class AuthValidation {
    /**
     * Throws exception if not appropriate user for operation on Hopuse
     */
    public Session checkCookieUser(Cookie session, String id)
            throws NotAuthorizedException {
        if (session == null || session.getValue() == null)
            throw new NotAuthorizedException("No session initialized");
        Session s;
        try {
            s = AuthCache.getSession(session.getValue());
        } catch (Exception e) {
            throw new NotAuthorizedException("No valid session initialized");
        }
        if (s == null || s.getName() == null || s.getName().length() == 0)
            throw new NotAuthorizedException("No valid session initialized");
        if (!s.getName().equals(id) && id != null)
            throw new NotAuthorizedException("Invalid user : " + s.getName());
        return s;
    }


}
