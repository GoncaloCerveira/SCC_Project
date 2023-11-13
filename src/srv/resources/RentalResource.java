package srv.resources;

import cache.RentalsCache;
import data.authentication.AuthResource;
import data.rental.Rental;
import data.rental.RentalDAO;

import db.CosmosDBRentalsLayer;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
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
    private final data.authentication.AuthResource auth = new AuthResource();
    private static final Logger Log = Logger.getLogger(RentalResource.class.getName());

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("scc:session") Cookie session, @PathParam("id") String houseId, Rental rental) {

        try {
            Log.info("createRental from: " + rental.getUserId() + " for: " + houseId);

            auth.checkCookieUser(session, rental.getUserId());

            if (!rental.validateCreate()) {
                Log.info("Null information was given");
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }

            int startDate = rental.getStartDate();
            int endDate = rental.getEndDate();
            boolean empty = RentalsCache.getHouseRentalByDate(houseId, startDate, endDate).isEmpty();
            if (!empty) {
                Log.info("House is already rented.");
                throw new WebApplicationException(Response.Status.CONFLICT);
            }

            rental.setId(UUID.randomUUID().toString());
            rental.setHouseId(houseId);

            rdb.postRental(new RentalDAO(rental));
            Log.info("Rental created.");
            return Response.ok().build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }
    }

    @PATCH
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@CookieParam("scc:session") Cookie session, @PathParam("id") String houseId, Rental rental) {

        try {
            Log.info("updateRental from: " + rental.getUserId() + " for: " + houseId);

            auth.checkCookieUser(session, rental.getUserId());

            if (!rental.validateUpdate()) {
                Log.info("Null information was given");
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }

            String id = rental.getId();
            boolean empty = RentalsCache.getRentalById(id).isEmpty();
            if (!empty) {
                Log.info("Rental does not exist.");
                throw new WebApplicationException(Response.Status.CONFLICT);
            }

            rdb.putRental(new RentalDAO(rental));
            Log.info("Rental updated.");
            return Response.ok().build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }
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

        List<RentalDAO> rentals = RentalsCache.getHouseRentals(houseId);
        if(rentals.isEmpty()) {
            Log.info("House does not exist or has no rentals.");
            throw new WebApplicationException(Response.Status.CONFLICT);
        }

        Log.info("Rentals retrieved.");
        return Response.ok(rentals).build();
    }
}
