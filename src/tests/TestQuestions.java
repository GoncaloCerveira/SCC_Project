package tests;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.util.CosmosPagedIterable;

import data.question.QuestionDAO;
import data.user.UserDAO;
import data.house.HouseDAO;
import data.rental.RentalDAO;
import db.CosmosDBQuestionsLayer;
import db.CosmosDBUsersLayer;
import db.CosmosDBHousesLayer;
import db.CosmosDBRentalsLayer;

public class TestQuestions {
    public static void main(String[] args) {
        System.setProperty(org.slf4j.simple.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "Error");

        try {
            Locale.setDefault(Locale.US);
            CosmosDBUsersLayer udb = CosmosDBUsersLayer.getInstance();
            CosmosDBHousesLayer hdb = CosmosDBHousesLayer.getInstance();
            CosmosDBRentalsLayer rdb = CosmosDBRentalsLayer.getInstance();
            CosmosDBQuestionsLayer qdb = CosmosDBQuestionsLayer.getInstance();

            String uId = "0:" + System.currentTimeMillis();
            CosmosItemResponse<UserDAO> res = null;
            UserDAO u = new UserDAO();
            u.setId(uId);
            u.setName("SCC " + uId);
            u.setPwd("super_secret");

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

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

            String rId = "0:" + System.currentTimeMillis();
            CosmosItemResponse<RentalDAO> res3 = null;
            RentalDAO r = new RentalDAO();
            r.setId(rId);
            r.setHouseId(hId);
            r.setUserId(uId);
            Date date = dateFormat.parse("12-12-2023");
            r.setInitDate((int) date.getTime());
            Date date2 = dateFormat.parse("18-12-2023");
            r.setEndDate((int) date2.getTime());
            r.setPrice(200);

            res3 = rdb.putRental(r);
            System.out.println( "Put result");
            System.out.println( res3.getStatusCode());
            System.out.println( res3.getItem());

            String qId = "0:" + System.currentTimeMillis();
            CosmosItemResponse<QuestionDAO> res4 = null;
            QuestionDAO q = new QuestionDAO();
            q.setId(qId);
            q.setHouseId(hId);
            q.setOwner(uId);
            q.setText("Does the house have heating?");

            res4 = qdb.putQuestion(q);
            System.out.println( "Put result");
            System.out.println( res4.getStatusCode());
            System.out.println( res4.getItem());

            CosmosPagedIterable<QuestionDAO> resGet;

            System.out.println( "Get by id result");
            resGet = qdb.getQuestionById(qId);
            for( QuestionDAO e: resGet) {
                System.out.println( e);
            }

            System.out.println( "Delete question");
            qdb.delQuestionById(qId);

            System.out.println( "Get for all ids");
            resGet = qdb.getQuestions("20", "0");
            for( QuestionDAO e: resGet) {
                System.out.println( e);
            }

            System.out.println( "Test Done");

            udb.close();
            hdb.close();
            rdb.close();
            qdb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
