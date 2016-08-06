package utils;

import model.User;
import model.UserRole;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static utils.GeneralUtils.mapOrNull;

/**
 * Created by roman on 06.08.2016.
 */
public interface DAOUtils {
    static User parseUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setFirstName(resultSet.getString("first_name"));
        user.setLastName(resultSet.getString("last_name"));
        user.setInfo(resultSet.getString("info"));
        user.setBirthDate(mapOrNull(resultSet.getDate("birth_date"), Date::toLocalDate));
        user.setRegistrationTime(resultSet.getTimestamp("registration_time").toInstant());
        user.setRole(UserRole.getRoleById(resultSet.getInt("role")));
        return user;
    }

    static List<User> parseUsers(ResultSet resultSet) throws SQLException {
        List<User> result = new ArrayList<>();
        while (resultSet.next()) {
            result.add(parseUser(resultSet));
        }
        return Collections.unmodifiableList(result);
    }

    static Optional<User> parseUserOpt(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) return Optional.of(parseUser(resultSet));
        else return Optional.empty();
    }

}
