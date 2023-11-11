package utils;

import java.util.Locale;

import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.util.CosmosPagedIterable;

import data.user.UserDAO;
import data.house.HouseDAO;
import db.CosmosDBUsersLayer;
import db.CosmosDBHousesLayer;

public class TestHouses {
    public static void main(String[] args) {
        System.setProperty(org.slf4j.simple.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "Error");

        try {
            Locale.setDefault(Locale.US);
            CosmosDBUsersLayer udb = CosmosDBUsersLayer.getInstance();
            CosmosDBHousesLayer hdb = CosmosDBHousesLayer.getInstance();

            String uId = "0:" + System.currentTimeMillis();
            CosmosItemResponse<UserDAO> res = null;
            UserDAO u = new UserDAO();
            u.setId(uId);
            u.setName("SCC " + uId);
            u.setPwd("super_secret");
            u.setPhotoId("0:34253455");

            res = udb.putUser(u);
            System.out.println( "Put result");
            System.out.println( res.getStatusCode());
            System.out.println( res.getItem());

            String hId = "0:" + System.currentTimeMillis();
            CosmosItemResponse<HouseDAO> res2 = null;
            HouseDAO h = new HouseDAO();
            h.setId(hId);
            h.setName("SCC " + hId);
            h.setLocation("Lisbon");
            h.setOwnerId(uId);

            res2 = hdb.putHouse(h);
            System.out.println( "Put result");
            System.out.println( res2.getStatusCode());
            System.out.println( res2.getItem());

            CosmosPagedIterable<HouseDAO> resGet;

            System.out.println( "Get for all ids");
            resGet = hdb.getHouses();
            for( HouseDAO e: resGet) {
                System.out.println( e);
            }

            hId = "0:" + System.currentTimeMillis();
            res2 = null;
            h = new HouseDAO();
            h.setId(hId);
            h.setName("SCC " + hId);
            h.setLocation("Porto");
            h.setOwnerId(uId);

            res2 = hdb.putHouse(h);
            System.out.println( "Put result");
            System.out.println( res2.getStatusCode());
            System.out.println( res2.getItem());

            System.out.println( "Get by id result");
            resGet = hdb.getHouseById(hId);
            for( HouseDAO e: resGet) {
                System.out.println( e);
            }

            System.out.println( "Delete house");
            hdb.delHouseById(hId);

            udb.close();
            hdb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
