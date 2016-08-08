package rest;

import dao.interfaces.FollowerDAO;
import listeners.ServicesProvider;
import lombok.extern.slf4j.Slf4j;
import model.User;
import utils.SessionUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by roman on 08.08.2016.
 */

@Slf4j
@Path("/secure/followers")
public class FollowersResource {
    private final FollowerDAO followerDAO;

    public FollowersResource(@Context ServletContext context) {
        followerDAO = (FollowerDAO) context.getAttribute(ServicesProvider.FOLLOWER_DAO);
    }

    @Context
    HttpServletRequest request;

    public static class SubscribeJson {
        public int userId;
    }

    @POST
    @Path("/subscribe")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response subscribe(SubscribeJson json) {
        log.info("Serving POST rest on {} path", request.getServletPath() + request.getPathInfo());

        User sessionUser = SessionUtils.getSessionUser(request.getSession());
        log.info("User '{}' trying to subscribe to user with {} id", sessionUser.getUsername(), json.userId);

        if (sessionUser.getId() == json.userId) {
            log.warn("User cannot subscribe to itself!");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        try {
            if (followerDAO.addFollower(json.userId, sessionUser.getId())) {
                log.info("User '{}' is now following user with id {}", sessionUser.getUsername(), json.userId);
            } else {
                log.warn("User '{}' is already following user with id {}; subscription is not added", sessionUser.getUsername(), json.userId);
            }
            return Response.ok().build();
        } catch (RuntimeException e) {
            log.warn("Error while trying to add '" + sessionUser.getUsername() + "' follower to user with id "+json.userId, e);
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("/unsubscribe")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response unsubscribe(SubscribeJson json) {
        log.info("Serving DELETE rest on {} path", request.getServletPath() + request.getPathInfo());

        User sessionUser = SessionUtils.getSessionUser(request.getSession());
        log.info("Trying to remove user '{}' subscribe from user with id {}", sessionUser.getUsername(), json.userId);

        if (sessionUser.getId() == json.userId) {
            log.warn("User cannot unsubscribe from itself!");
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        try {
            if (followerDAO.removeFollower(json.userId, sessionUser.getId())) {
                log.info("user '{}' unsubscribed from user with id {}!", sessionUser.getUsername(), json.userId);
            } else {
                log.warn("user '{}' was not subscribed to user with id {}; subscription cannot be removed", sessionUser.getUsername(), json.userId);
            }
            return Response.ok().build();
        } catch (RuntimeException e) {
            log.warn("Error while trying to remove '" + sessionUser.getUsername() + "' follower from user with id "+json.userId, e);
            return Response.serverError().build();
        }
    }

}
