package model;

import lombok.Data;

@Data
public class Comment {
    int id;
    int postId;
    int authorId;
    int text;
    int creationTime;
}
