package model;

import lombok.Data;

import java.time.Instant;

/**
 * Created by roman on 07.08.2016.
 */

@Data
public class PostView {
    private int postId;
    private int postAuthorId;

    private String postText;

    private Instant postCreationTime;

    private int authorId;
    private String authorUsername;
    private String authorFirstname;
    private String authorLastname;

    private int likes;
    private boolean canLike;
}
