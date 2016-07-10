package model;

/**
 * Created by roman on 05.07.2016.
 */
public enum PostPrivacyType {
    DEFAULT,
    PUBLIC,
    PRIVATE,
    PROTECTED;

    public int getId() {
        switch (this) {
            case DEFAULT: return 0;
            case PUBLIC: return 1;
            case PRIVATE: return 2;
            case PROTECTED: return 3;
        }
        throw new IllegalStateException("Illegal PostPrivacyType:" + this.name());
    }

    public static PostPrivacyType getTypeByID(int id) {
        switch (id) {
            case 0: return DEFAULT;
            case 1: return PUBLIC;
            case 2: return PRIVATE;
            case 3: return PROTECTED;
        }
        throw new IllegalArgumentException("Illegal ID for PostPrivacyType:" + id);
    }
}
