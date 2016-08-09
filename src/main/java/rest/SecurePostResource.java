package rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.interfaces.PostDAO;
import dao.interfaces.PostViewDAO;
import listeners.ServicesProvider;
import lombok.extern.slf4j.Slf4j;
import model.User;
import utils.SessionUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by roman on 25.07.2016.
 */

@Slf4j
@Path("/posts")
@Produces(MediaType.APPLICATION_JSON)
public class SecurePostResource {
    private final PostViewDAO postViewDAO;
    private final PostDAO postDAO;

    public SecurePostResource(@Context ServletContext context) {
        postViewDAO = (PostViewDAO) context.getAttribute(ServicesProvider.POST_VIEW_DAO);
        postDAO = (PostDAO) context.getAttribute(ServicesProvider.POST_DAO);
    }

    private String objectToJsonString(Object o) throws JsonProcessingException {
        return new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(o);
    }

    public static class PostIdJson {
        public int postId;
    }

    @Context
    HttpServletRequest request;

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/removepost")
    public Response removePost(PostIdJson json) {
        log.info("Serving {} rest on {};", request.getMethod(), request.getServletPath() + request.getPathInfo());

        User sessionUser = SessionUtils.getSessionUser(request.getSession());

        log.info("User '{}' is trying to remove post with {} id", sessionUser.getUsername(), json.postId);
        boolean postCanBeRemoved
                = postDAO.getById(json.postId)
                .filter(p -> p.getAuthorId() == sessionUser.getId())
                .isPresent();

        if (!postCanBeRemoved) {
            log.info("User '{}' cannot remove not existing or not his post with {} id", sessionUser.getUsername(), json.postId);
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        try {
            if (postDAO.deleteById(json.postId)) {
                log.info("'{}'s post with id {} is removed", sessionUser.getUsername(), json.postId);
                return Response.ok().build();
            } else {
                log.warn("'{}'s post with id {} is not removed!", sessionUser.getUsername(), json.postId);
                return Response.ok().build();
            }
        } catch (RuntimeException e) {
            log.warn("Savig in DB failed!", e);
            return Response.serverError().build();
        }
    }
}
