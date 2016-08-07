package rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.interfaces.PostViewDAO;
import listeners.ServicesProvider;
import lombok.extern.slf4j.Slf4j;
import model.PostView;
import model.User;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

/**
 * Created by roman on 25.07.2016.
 */

@Slf4j
@Path("/posts")
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {
    private PostViewDAO postViewDAO;

    @Context
    private void init(ServletContext context) {
        postViewDAO = (PostViewDAO) context.getAttribute(ServicesProvider.POST_VIEW_DAO);
    }

    private String objectToJsonString(Object o) throws JsonProcessingException {
        return new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(o);
    }

    @Context HttpServletRequest request;

    @GET
    @Path("/getposts")
    public Response getPosts(
            @QueryParam("authorId") int authorId,
            @DefaultValue("-1") @QueryParam("offsetId") int offset,
            @DefaultValue("20") @QueryParam("limit") int limit
    ) {
        log.info("Serving GET rest on {};", request.getServletPath() + request.getPathInfo());

        int sessionUserId
                = Optional.ofNullable((User)request.getSession().getAttribute("sessionUser"))
                .map(User::getId).orElse(-1);

        List<PostView> posts = postViewDAO.getAsUserByAuthorId(sessionUserId, authorId, offset, limit);
        try {
            String response = objectToJsonString(posts);
            return Response.ok(response).build();
        } catch (JsonProcessingException e) {
            log.warn("Error while processing json!", e);
            return Response.noContent().build();
        }
    }
}
