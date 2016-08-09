package rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.interfaces.PostViewDAO;
import listeners.ServicesProvider;
import lombok.extern.slf4j.Slf4j;
import model.PostView;
import model.User;
import utils.SessionUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by roman on 09.08.2016.
 */
@Slf4j
@Path("/secure/timeline")
public class TimelineResource {
    private final PostViewDAO postViewDAO;

    public TimelineResource(@Context ServletContext context) {
        postViewDAO = (PostViewDAO) context.getAttribute(ServicesProvider.POST_VIEW_DAO);
    }

    @Context
    HttpServletRequest request;

    private String objectToJsonString(Object o) throws JsonProcessingException {
        return new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(o);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/gettimeline")
    public Response getTimeline(
            @DefaultValue("-1") @QueryParam("offsetId") int offsetId,
            @DefaultValue("5") @QueryParam("limit") int limit
    ) {
        log.info("Serving GET rest on {} path", request.getServletPath() + request.getContextPath());
        User sessionUser = SessionUtils.getSessionUser(request.getSession());

        log.info("Trying to get timeline for '{}' user", sessionUser.getUsername());
        try {
            List<PostView> result = postViewDAO.getTimeline(sessionUser.getId(), offsetId, limit);

            Response response = Response.ok(objectToJsonString(result)).build();
            log.info("Returning timeline!");
            return response;
        } catch (JsonProcessingException e) {
            log.warn("Error while processing json!", e);
            return Response.serverError().build();
        } catch (RuntimeException e) {
            log.warn("Error while getting timeline from database", e);
            return Response.serverError().build();
        }
    }
}
