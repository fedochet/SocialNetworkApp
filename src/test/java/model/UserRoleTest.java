package model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by roman on 05.08.2016.
 */
public class UserRoleTest {

    @Test
    public void userRoleIdCheck() {
        assertThat(UserRole.ADMIN.getId(),is(1));
        assertThat(UserRole.USER.getId(), is(0));
    }

    @Test
    public void getByIdCheck() {
        assertThat(UserRole.getRoleById(0), is(UserRole.USER));
        assertThat(UserRole.getRoleById(1), is(UserRole.ADMIN));
    }

}