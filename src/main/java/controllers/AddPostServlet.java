package controllers;

import lombok.extern.slf4j.Slf4j;
import model.Post;
import model.PostPrivacyType;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by roman on 24.07.2016.
 */

@Slf4j
@WebServlet(urlPatterns = "/addpost")
public class AddPostServlet extends BaseServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Serving POST on {} path", req.getServletPath());

        User user = (User) req.getSession().getAttribute("sessionUser");

        String text = req.getParameter("text");

        PostPrivacyType privacyType;
        try {
            privacyType = PostPrivacyType.getTypeByID(Integer.parseInt(req.getParameter("post_privacy_type")));
        } catch (IllegalArgumentException e) {
            log.warn("Illegal argument passed as post privacy type: '{}'; setting to default", req.getParameter("post_privacy_type"));
            privacyType = PostPrivacyType.DEFAULT;
        }

        Post post = new Post();
        try {
            post.setText(text);
            post.setAuthorId(user.getId());
            post.setPostPrivacyType(privacyType);

            log.info("Trying to save post: '{}'", post);
            postDAO.create(post);
            log.info("Post '{}' is successfully created; redirecting to /", post);
            resp.sendRedirect("/");
        } catch (RuntimeException e) {
            log.warn("Failed to save post: '" + post.toString() + "'", e);
            resp.sendError(500, "Error while saving post");
        }
    }
}
