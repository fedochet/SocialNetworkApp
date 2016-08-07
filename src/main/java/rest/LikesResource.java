package rest;

import dao.interfaces.LikeDAO;
import listeners.ServicesProvider;
import lombok.extern.slf4j.Slf4j;
import model.User;
import utils.SessionUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 * Created by roman on 07.08.2016.
 */

@Path("/likes")
@Slf4j
public class LikesResource {
    private final LikeDAO likeDAO;

    public LikesResource(@Context ServletContext context) {
        likeDAO = (LikeDAO) context.getAttribute(ServicesProvider.LIKE_DAO);
    }

    public static class LikeJson {
        public int postId;
    }

    @Context
    HttpServletRequest request;
    @Context
    HttpSession session;

    @POST
    @Path("/addlike")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response  addLike(LikeJson likeJson) {
        log.info("Serving POST rest on {};", request.getServletPath() + request.getPathInfo());

        Optional<User> sessionUserOpt = SessionUtils.getSessionUser(request.getSession(false));
        if (!sessionUserOpt.isPresent()) {
            log.warn("No user is attached to session; cannot add like!");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("User is not authorised!").build();
        }
        User sessionUser = sessionUserOpt.get();

        try {
            log.info("Trying to add '{}'s like to post with id '{}'", sessionUser.getUsername(), likeJson.postId);
            likeDAO.addLike(likeJson.postId, sessionUser.getId());
            log.info("Success! '{}'s like is added to post with id '{}'", sessionUser.getUsername(), likeJson.postId);
            return Response.ok().build();
        } catch (RuntimeException e) {
            log.warn("Error occured while adding "+sessionUser.getUsername()+" like to post with id "+ likeJson.postId, e);
            return Response.serverError().entity("Error occured while adding like").build();
        }
    }
}
