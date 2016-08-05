package model;

import lombok.Data;

import java.time.Instant;

@Data
public class Like {
    private int id;
    private int postId;
    private int userId;
    private Instant creationTime;
}
