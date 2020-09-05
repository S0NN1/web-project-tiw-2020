package it.polimi.tiw.dao;

import it.polimi.tiw.beans.User;
import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "UserDAO")
public class UserDAO extends HttpServlet {
    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public void registerUser(String name, String surname, String email, String password) throws SQLException {
        String encryptedPassword = DigestUtils.sha512Hex(password);
        String query = "INSERT INTO user (email, password, name, surname) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, encryptedPassword);
            preparedStatement.setString(3, name);
            preparedStatement.setString(4, surname);
            preparedStatement.executeUpdate();
        }
    }

    public User getUserInfo(String email, String password) throws SQLException {
        String query = "SELECT * FROM user WHERE email=? AND password=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            String encryptedPassword = DigestUtils.sha512Hex(password);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, encryptedPassword);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.isBeforeFirst())
                    return null;
                else {
                    User retrievedUser = new User();
                    retrievedUser.setId(resultSet.getInt("id"));
                    retrievedUser.setEmail(resultSet.getString("email"));
                    retrievedUser.setPassword(resultSet.getString("password"));
                    retrievedUser.setName(resultSet.getString("name"));
                    retrievedUser.setSurname(resultSet.getString("surname"));
                    return retrievedUser;
                }
            }
        }
    }
}
