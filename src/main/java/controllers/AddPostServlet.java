package controllers;

import dao.interfaces.PostDAO;
import listeners.ServicesProvider;
import lombok.extern.slf4j.Slf4j;
import model.Post;
import model.PostPrivacyType;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by roman on 24.07.2016.
 */

@Slf4j
@WebServlet(urlPatterns = "/addpost")
public class AddPostServlet extends HttpServlet {
    private PostDAO postDAO;

    @Override
    public void init() throws ServletException {
        postDAO = (PostDAO) getServletContext().getAttribute(ServicesProvider.POST_DAO);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("sessionUser");

        String text = req.getParameter("text");

        PostPrivacyType privacyType;
        try {
            privacyType = PostPrivacyType.getTypeByID(Integer.parseInt(req.getParameter("post_privacy_type")));
        } catch (IllegalArgumentException e) {
            privacyType = PostPrivacyType.DEFAULT;
        }

        try {
            Post post = new Post();
            post.setText(text);
            post.setAuthorId(user.getId());
            post.setPostPrivacyType(privacyType);

            postDAO.create(post);
            resp.sendRedirect("/");
        } catch (RuntimeException e) {
            resp.sendError(500, "Error while saving post");
        }
    }
}
