package model;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by roman on 10.07.2016.
 */
public class PostPrivacyTypeTest {

    @Test
    public void privacyTypesIdCheck() {
        assertThat(PostPrivacyType.DEFAULT.getId(), is(0));
        assertThat(PostPrivacyType.PUBLIC.getId(), is(1));
        assertThat(PostPrivacyType.PRIVATE.getId(), is(2));
        assertThat(PostPrivacyType.PROTECTED.getId(), is(3));
    }

    @Test
    public void getTypeByIdCheck() {
        assertThat(PostPrivacyType.getTypeByID(0), is(PostPrivacyType.DEFAULT));
        assertThat(PostPrivacyType.getTypeByID(1), is(PostPrivacyType.PUBLIC));
        assertThat(PostPrivacyType.getTypeByID(2), is(PostPrivacyType.PRIVATE));
        assertThat(PostPrivacyType.getTypeByID(3), is(PostPrivacyType.PROTECTED));
    }

}