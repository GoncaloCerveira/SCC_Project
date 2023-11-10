package srv.resources;

import data.rental.Rental;
import data.rental.RentalDAO;

import db.CosmosDBRentalsLayer;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.*;
import java.util.logging.Logger;

/**
 * Resource for managing creating, replying and listing rentals.
 */
@Path("/house/{id}/rental")
public class RentalResource {

    private final CosmosDBRentalsLayer rdb = CosmosDBRentalsLayer.getInstance();
    private static final Logger Log = Logger.getLogger(RentalResource.class.getName());

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@PathParam("id") String houseId, Rental rental) {
        Log.info("createRental from: " + rental.getUserId() + " for: " + houseId);
        if(!rental.validateCreate()) {
            Log.info("Null information was given");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        int startDate = rental.getStartDate();
        int endDate = rental.getEndDate();
        boolean exists = rdb.getHouseRentalByDate(houseId, startDate, endDate).iterator().hasNext();
        if(exists) {
            Log.info("House is already rented.");
            throw new WebApplicationException(Response.Status.CONFLICT);
        }


        rental.setId(UUID.randomUUID().toString());
        rental.setHouseId(houseId);

        rdb.postRental(new RentalDAO(rental));
        Log.info("Rental created.");
        return Response.ok().build();
    }

    @PATCH
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") String houseId, Rental rental) {
        Log.info("updateRental from: " + rental.getUserId() + " for: " + houseId);
        if(!rental.validateUpdate()) {
            Log.info("Null information was given");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        String id = rental.getId();
        boolean exists = rdb.getRentalById(id).iterator().hasNext();
        if(!exists) {
            Log.info("Rental does not exist.");
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        rdb.putRental(new RentalDAO(rental));
        Log.info("Rental updated.");
        return Response.ok().build();
    }

    @GET
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(@PathParam("id") String houseId, Rental rental) {
        Log.info("listRentals for: " + houseId);
        if(houseId == null) {
            Log.info("Null information was given");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        Iterator<RentalDAO> rentals = rdb.getHouseRentals(houseId).iterator();
        if(!rentals.hasNext()) {
            Log.info("House does not exist or has no rentals.");
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        Log.info("Rentals retrieved.");
        return Response.ok(rentals).build();
    }
}
