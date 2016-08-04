package model;

/**
 * Created by roman on 05.08.2016.
 */
public enum UserRole {
    USER,
    ADMIN;

    public int getId() {
        switch (this) {
            case USER: return 0;
            case ADMIN: return 1;
        }
        throw new IllegalArgumentException("Illegal UserRole: " + this.name());
    }

    public static UserRole getRoleById(int id) {
        switch (id) {
            case 0: return USER;
            case 1: return ADMIN;
        }
        throw new IllegalArgumentException("Illegal ID for post privacy type: " + id);
    }
}
