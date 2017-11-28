package dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import connectors.MySqlConnector;
import enitities.GamePlay;
import models.GridInfo;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.io.IOException;
import java.sql.*;

@Singleton
@Slf4j
public class GamePlayDao {

    private Connection connection = MySqlConnector.getConnection();
    private static ObjectMapper mapper = new ObjectMapper();

    public GamePlay getGamePlay(String gameId) throws Exception {

        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM game_play WHERE game_id= '" + gameId +"'");
            if(rs.next()) {
                return getGamePlayObject(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private GamePlay getGamePlayObject(ResultSet rs) throws SQLException, IOException {

        return GamePlay.builder()
                .gameId(rs.getString("game_id"))
                .id(rs.getLong("id"))
                .lastPlayedUser(rs.getString("last_played_user"))
                .gridInfo(mapper.readValue(rs.getString("grid_info"), GridInfo.class))
                .build();
    }


    public  void insertGamePlay(GamePlay gamePlay)  {

        try {
            String query = "INSERT INTO GAME_PLAY(game_id, last_played_user, grid_info) values('" +
                    gamePlay.getGameId() + "' , '" +
                    gamePlay.getLastPlayedUser() + "' , '" +
                    mapper.writeValueAsString(gamePlay.getGridInfo()) + "')";
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            preparedStmt.execute();
        } catch (Exception e) {

            log.error("Exception errored while insertion {}", e);
        }
    }

    public void updateGame(GamePlay gamePlay) {

        try {

            String query = "UPDATE GAME_PLAY SET game_id= '" + gamePlay.getGameId() + "' ," +
                    " last_played_user ='" + gamePlay.getLastPlayedUser() + "', grid_info= '" +
                    mapper.writeValueAsString(gamePlay.getGridInfo())+ "' " +
                     " where game_id='" + gamePlay.getGameId() +"'";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();

        } catch(Exception e) {
            log.error("Exception errored while update in game {} :  {}",gamePlay.toString(),  e);
        }
    }
}
