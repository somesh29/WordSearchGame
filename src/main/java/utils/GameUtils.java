package utils;

import enitities.Game;
import enitities.GamePlay;
import enitities.User;
import enums.GameState;
import responses.InfoResponse;

import java.util.List;

public class GameUtils {

    public static String getGrid() {
        return "";
    }

    public static InfoResponse getInfo(Game game, User user, GamePlay gamePlay) {
        List<String> turnSqeuence = game.getInfo().getTurnSequence(user.getNick());

        return InfoResponse.builder()
                .gameState(GameState.valueOf(game.getState()))
                .turnSequence(turnSqeuence)
                .currentPlayer(turnSqeuence.get(0))
                .grid(gamePlay.getGridInfo().getGrid())
                .scores(game.getInfo().getScores())
                .foundWords(gamePlay.getGridInfo().getFoundWords())
                .build();
    }
}
