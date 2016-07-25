package rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.interfaces.PostDAO;
import dao.interfaces.UserDAO;
import listeners.ServicesProvider;
import model.Post;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by roman on 25.07.2016.
 */

@Path("/getposts")
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {
    private static PostDAO postDAO;
    private static UserDAO userDAO;

    @Context
    private void init(ServletContext context) {
        if (postDAO == null) {
            postDAO = (PostDAO) context.getAttribute(ServicesProvider.POST_DAO);
        }

        if (userDAO == null) {
            userDAO = (UserDAO) context.getAttribute(ServicesProvider.USER_DAO);
        }
    }

    private String objectToJsonString(Object o) throws JsonProcessingException {
        return new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(o);
    }

    @Context HttpServletRequest request;

    @GET
    public Response getPosts(
            @QueryParam("userId") int userId,
            @QueryParam("offset") int offset,
            @QueryParam("limit") int limit
    ) {
        List<Post> posts = postDAO.getByAuthorId(userId, offset, limit);
        try {
            String response = objectToJsonString(posts);
            return Response.ok(response).build();
        } catch (JsonProcessingException e) {
            return Response.noContent().build();
        }

    }
}
