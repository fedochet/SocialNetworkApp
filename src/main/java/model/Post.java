package model;

import lombok.Data;

import java.time.Instant;

/**
 * Created by roman on 05.07.2016.
 */

@Data
public class Post {
    private int id;
    private int authorId;

    private String text;

    private Instant creationTime;
    private PostPrivacyType postPrivacyType;
}
