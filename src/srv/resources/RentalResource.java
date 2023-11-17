package srv.resources;

import cache.RentalsCache;
import data.rental.Rental;
import data.rental.RentalDAO;

import db.CosmosDBRentalsLayer;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import utils.AuthValidation;

import java.util.*;

/**
 * Resource for managing creating, replying and listing rentals.
 */
@Path("/house/{houseId}/rental")
public class RentalResource {
    private final CosmosDBRentalsLayer rdb = CosmosDBRentalsLayer.getInstance();
    private final AuthValidation auth = new AuthValidation();

    @POST
    @Path("/{rentalId}/renter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("scc:session") Cookie session, @PathParam("houseId") String houseId,
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

            rdb.putRental(rentalDB);
            return Response.ok().build();
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
            auth.checkCookieUser(session, rentalDB.getUser());

            rdb.putRental(new RentalDAO(rental));
            return Response.ok().build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }

    }

    @PUT
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@CookieParam("scc:session") Cookie session, @PathParam("houseId") String houseId,
                           @QueryParam("rentalId") String rentalId) {
        try {
            List<RentalDAO> results = RentalsCache.getRentalById(rentalId);
            if (results.isEmpty()) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            RentalDAO rentalDB = results.get(0);
            auth.checkCookieUser(session, rentalDB.getUser());

            rdb.delRental(rentalDB);
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
    public Response list(@CookieParam("scc:session") Cookie session, @PathParam("houseId") String houseId,
                         @QueryParam("st") String st , @QueryParam("len") String len, @QueryParam("free") boolean free) {
        try {
            auth.checkCookieUser(session, null);

            List<RentalDAO> rentals;
            if(st != null && len != null) {
                rentals = RentalsCache.getHouseRentals(st, len, houseId);
            } else {
                rentals = RentalsCache.getFreeSlots(free);
            }

            return Response.ok(rentals).build();
        } catch (WebApplicationException e) {
            throw e;
        } catch(Exception e) {
            throw new InternalServerErrorException(e);
        }

    }


}
