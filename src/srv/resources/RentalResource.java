package srv.resources;

import cache.HousesCache;
import cache.RentalsCache;
import data.house.HouseDAO;
import data.rental.Rental;
import data.rental.RentalDAO;

import db.CosmosDBRentalsLayer;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import utils.AuthValidation;

import java.util.*;
import java.util.logging.Logger;

/**
 * Resource for managing creating, replying and listing rentals.
 */
@Path("/house/{houseId}/rental")
public class RentalResource {
    private final CosmosDBRentalsLayer rdb = CosmosDBRentalsLayer.getInstance();
    private final AuthValidation auth = new AuthValidation();
    private static final Logger Log = Logger.getLogger(RentalResource.class.getName());

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("scc:session") Cookie session, @PathParam("houseId") String houseId, Rental rental) {
        try {
            Log.info("createRental from: " + rental.getUserId() + " for: " + houseId);

            String userId = auth.getUserId(session);

            if (!rental.validateCreate()) {
                Log.info("Null information was given");
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }

            int startDate = rental.getInitDate();
            int endDate = rental.getEndDate();
            boolean empty = RentalsCache.getHouseRentalByDate(houseId, startDate, endDate).isEmpty();
            if (!empty) {
                Log.info("House is already rented.");
                throw new WebApplicationException(Response.Status.CONFLICT);
            }

            rental.setId(UUID.randomUUID().toString());
            rental.setHouseId(houseId);
            rental.setUserId(userId);

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
    public Response update(@CookieParam("scc:session") Cookie session, @PathParam("houseId") String houseId,
                           @QueryParam("rid") String rentalId, Rental rental) {
        try {
            List<RentalDAO> results = RentalsCache.getRentalById(rentalId);
            if (results.isEmpty()) {
                Log.info("Rental does not exist.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            RentalDAO rentalDB = results.get(0);
            auth.checkCookieUser(session, rental.getUserId());

            Log.info("updateRental from: " + rentalDB.getUserId() + " for: " + houseId);

            int startDate = rental.getInitDate();
            int endDate = rental.getEndDate();

            if(startDate > endDate) {
                Log.info("Invalid dates.");
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }

            if(startDate != 0 && rentalDB.validateStartDate(startDate)) {
                rentalDB.setInitDate(startDate);
            }
            if(endDate != 0 && rentalDB.validateEndDate(endDate)) {
                rentalDB.setEndDate(endDate);
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
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listRentals(@PathParam("houseId") String houseId, @QueryParam("st") String st , @QueryParam("len") String len) {
        Log.info("listRentals for: " + houseId);

        List<HouseDAO> results = HousesCache.getHouseById(houseId);
        if (results.isEmpty()) {
            Log.info("House does not exist.");
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        List<RentalDAO> rentals = RentalsCache.getHouseRentals(len, st, houseId);
        Log.info("Rentals retrieved.");
        return Response.ok(rentals).build();
    }


}
