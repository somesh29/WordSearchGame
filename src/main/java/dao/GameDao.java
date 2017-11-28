package dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import connectors.MySqlConnector;
import enitities.Game;
import lombok.extern.slf4j.Slf4j;
import models.GameInfo;

import javax.inject.Singleton;
import java.sql.*;


@Singleton
@Slf4j
public class GameDao {

    private static Connection connection = MySqlConnector.getConnection();
    private static ObjectMapper mapper = new ObjectMapper();


    public Game getGame(String gameId) {

       Statement stmt = null;
       try {
           stmt = connection.createStatement();
           String query = "SELECT * FROM games where game_id= '" + gameId + "'";
           ResultSet rs = stmt.executeQuery(query);
           if(rs.next()) {

               Game game = new Game();
               game.setId(rs.getLong("id"));
               game.setGameId(rs.getString("game_id"));
               game.setAdminUserId(rs.getString("admin_user_id"));
               game.setState(rs.getString("state"));
               game.setMaxPlayers(rs.getInt("max_players"));
               game.setInfo(mapper.readValue(rs.getString("info"), GameInfo.class));
               game.setMaxPlayers(rs.getInt("max_players"));
               return game;
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
       return null;
    }

    public void createGame(Game game)  {

        try {
            String query = "INSERT INTO GAMES(game_id, admin_user_id, state, info, max_players) values('" +
                    game.getGameId() + "' , '" + game.getAdminUserId() + "' , '" + game.getState()
                    + "' , '" + mapper.writeValueAsString(game.getInfo()) + "', " + game.getMaxPlayers() + ")";
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            preparedStmt.execute();
        } catch (Exception e) {

            log.error("Exception errored while insertion {}", e);
        }
    }

    public void updateGame(Game game) {

        try {

            String query = "UPDATE GAMES SET admin_user_id= '" + game.getAdminUserId() + "' ," +
                    " state ='" + game.getState()+ "', info= '" + mapper.writeValueAsString(game.getInfo())+ "' " +
                    " , max_players= " + game.getMaxPlayers()  +" where game_id='" + game.getGameId() +"'";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();

        } catch(Exception e) {
            log.error("Exception errored while update in game {} :  {}",game.toString(),  e);
        }
    }
}
