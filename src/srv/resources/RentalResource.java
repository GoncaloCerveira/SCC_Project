package srv.resources;

import db.CosmosDBRentalsLayer;
import jakarta.ws.rs.Path;

import java.util.logging.Logger;

/**
 * Resource for managing creating, replying and listing rentals.
 */
@Path("/house/{id}/rental")
public class RentalResource {

    private CosmosDBRentalsLayer db;
    private static final Logger Log = Logger.getLogger(QuestionResource.class.getName());

}
