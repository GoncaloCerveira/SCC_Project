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
    @Path("/{rentalId}/renter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("scc:session") Cookie session, @PathParam("houseId") String houseId, Rental rental,
                           @PathParam("rentalId") String rentalId) {
        try {
            auth.checkCookieUser(session, null);

            List<RentalDAO> results = RentalsCache.getRentalById(rentalId);
            if(results.isEmpty()) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            RentalDAO rentalDB = results.get(0);
            rentalDB.setUser(session.getName());
            rentalDB.setFree(false);

            rdb.postRental(rentalDB);
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
    public Response getSlots(@CookieParam("scc:session") Cookie session, @PathParam("houseId") String houseId,
                             @QueryParam("free") boolean free) {
        try {
            auth.checkCookieUser(session, null);
            return Response.ok(RentalsCache.getFreeSlots()).build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }

    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@CookieParam("scc:session") Cookie session, @PathParam("houseId") String houseId,
                           @QueryParam("rentalId") String rentalId, Rental rental) {
        try {
            List<RentalDAO> results = RentalsCache.getRentalById(rentalId);
            if (results.isEmpty()) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            RentalDAO rentalDB = results.get(0);
            auth.checkCookieUser(session, rental.getUser());

            rdb.putRental(new RentalDAO(rental));
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
    public Response listRentals(@PathParam("houseId") String houseId,
                                @QueryParam("st") String st , @QueryParam("len") String len) {
        Log.info("listRentals for: " + houseId);

        List<HouseDAO> results = HousesCache.getHouseById(houseId);
        if (results.isEmpty()) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        List<RentalDAO> rentals = RentalsCache.getHouseRentals(st, len, houseId);
        return Response.ok(rentals).build();
    }


}
