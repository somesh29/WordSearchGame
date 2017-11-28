package dao;


import com.google.common.base.Joiner;
import connectors.MySqlConnector;
import enitities.Game;
import enitities.User;
import enums.Assertum;
import lombok.extern.slf4j.Slf4j;
import utils.IdGenerator;

import javax.inject.Singleton;
import java.sql.*;

@Slf4j
public class UserDao {

    private Connection connection = MySqlConnector.getConnection();

    public User getUser(String gameId, String nick) {

        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE game_id= '" + gameId + "' and nick= '" + nick + "'");
            if(rs.next()) {
                User user = getUserObject(rs);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getPlayer(String gameId, String userId) {

        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE game_id= '" + gameId + "' and user_id= '" + userId + "'");
            if(rs.next()) {

                User user = getUserObject(rs);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUser(String userId) {

        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE user_id= '" + userId + "'");
            if(rs.next()) {
                User user = getUserObject(rs);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertUser(User user) {

        try {
            String query = "INSERT INTO users(game_id, user_id, nick, is_admin) values('" +
                    user.getGameId() + "' , '" + user.getUserId() + "' , '" + user.getNick() + "' , '"
                    + user.getIsAdmin() + "')";
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            preparedStmt.execute();
        } catch (Exception e) {

            log.error("Exception errored while insertion {}", e);
        }
    }

    private User getUserObject(ResultSet rs) throws SQLException {

        User user = User.builder()
                .isAdmin(Assertum.valueOf(rs.getString("is_admin")))
                .gameId(rs.getString("game_id"))
                .userId(rs.getString("user_id"))
                .nick(rs.getString("nick"))
                .id(rs.getLong("id"))
                .score(rs.getInt("score"))
                .build();
        return user;
    }

    public void updateUserScore(String userId, int score) {
        try {

            String query = "UPDATE users SET score= " + score + " where user_id='" + userId +"'";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();

        } catch(Exception e) {
            log.error("Exception errored while update in game {} :  {}", userId,  e);
        }
    }
}
