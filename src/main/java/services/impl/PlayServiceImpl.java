package services.impl;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import dao.GameDao;
import dao.GamePlayDao;
import dao.UserDao;
import enitities.Game;
import enitities.GamePlay;
import enitities.User;
import enums.Directions;
import enums.ResponseCode;
import helpers.GridHelper;
import lombok.extern.slf4j.Slf4j;
import models.Cell;
import models.Word;
import requests.PlayRequest;
import responses.GenericResponse;
import services.PlayService;
import utils.Constants;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static enums.GameState.COMPLETED;
import static enums.ResponseCode.*;

@Slf4j
public class PlayServiceImpl implements PlayService {

    private final GameDao gameDao;
    private final GamePlayDao gamePlayDao;
    private final UserDao userDao;
    private final String playerId = "playerId";
    private final String score = "score";

    private static final int gridSize = Constants.girdSize;

    @Inject
    public PlayServiceImpl(GameDao gameDao, GamePlayDao gamePlayDao, UserDao userDao) {

        this.gameDao = gameDao;
        this.gamePlayDao = gamePlayDao;
        this.userDao = userDao;
    }


    @Override
    public GenericResponse play(PlayRequest playRequest) throws Exception {

        ResponseCode responseCode = getResponseCode(playRequest);
        User user = userDao.getUser(playRequest.getPlayerId());
        GamePlay gamePlay = gamePlayDao.getGamePlay(playRequest.getGameId());

        if(responseCode != VALID_MOVE) {
            updateChance(gamePlay, user);
            return GenericResponse.builder().data(Collections.singletonMap("code", responseCode)).success(false).build();
        }

        GamePlay gamePlayUpdated = getUpdatedGamePlay(playRequest, gamePlay, user);
        gamePlayDao.updateGame(gamePlayUpdated);


        int newScore = user.getScore() + playRequest.getWord().length();
        userDao.updateUserScore(user.getUserId(), newScore);

        Game game = gameDao.getGame(playRequest.getGameId());
        game.getInfo().getJoinedUsers()
                .stream()
                .filter(each -> each.getUserId().equals(playRequest.getPlayerId()))
                .forEach(
                    each -> each.setScore(newScore)
                );

        checkEndGame(game, gamePlayUpdated);
        gameDao.updateGame(game);

        return GenericResponse.builder()
                .success(true)
                .data(
                        ImmutableMap.of(
                                playerId, playRequest.getPlayerId(),
                                score, newScore
                        )
                ).build();
    }

    private void checkEndGame(Game game, GamePlay gamePlay) {
        if(gamePlay.getGridInfo().getFoundWords().size() >= Constants.maxWords) {
            game.setState(COMPLETED.name());
        }
    }

    private ResponseCode getResponseCode(PlayRequest playRequest) throws Exception {
        Game game = gameDao.getGame(playRequest.getGameId());
        if (game == null) {
            return GAME_NOT_FOUND;
        }

        if(game.getState().equals(COMPLETED.name())) {
            return GAME_COMPLETED;
        }

        List<User> users = game.getInfo().getJoinedUsers();
        if (users.stream().filter(each -> each.getUserId().equals(playRequest.getPlayerId())).count() == 0) {
            return PLAYER_NOT_REGISTERED;
        }

        GamePlay gamePlay = gamePlayDao.getGamePlay(game.getGameId());
        String nextTurn = game.getInfo().getTurnSequence(gamePlay.getLastPlayedUser()).get(0);
        User nextPlayer = game.getInfo().getJoinedUsers().stream().filter(each -> each.getNick().equals(nextTurn)).findFirst().get();

        if (!(nextPlayer.getUserId().equals(playRequest.getPlayerId()))) {

            return INVALID_CHANCE;
        }

        Directions direction = getDirection(playRequest);
        if (direction == Directions.INVALID) {

            return INVALID_MOVE;
        }

        List<String> wordList = GridHelper.getWordList().stream().map(String::toUpperCase).collect(Collectors.toList());
        if (!(wordList.contains(playRequest.getWord()))) {
            return INCORRECT_WORD;
        }

        if (checkIfAlreadyFound(playRequest, gamePlay)) {
            return WORD_ALREADY_FOUND;
        }

        Character[][] grid = GridHelper.getGrid(gamePlay.getGridInfo().getGrid());
        if (!checkCorrectWord(playRequest, grid, direction)) {
            return INCORRECT_WORD;
        }
        return VALID_MOVE;
    }

    private GamePlay getUpdatedGamePlay(PlayRequest playRequest, GamePlay gamePlay, User user) {

        List<Word> words = gamePlay.getGridInfo().getFoundWords();
        Word word = Word.builder()
                .start(playRequest.getStart())
                .end(playRequest.getEnd())
                .word(playRequest.getWord())
                .build();
        words.add(word);

        return GamePlay.builder()
                .lastPlayedUser(user.getNick())
                .gameId(playRequest.getGameId())
                .gridInfo(gamePlay.getGridInfo()).build();
    }

    private void updateChance(GamePlay gamePlay, User user) {
        gamePlay.setLastPlayedUser(user.getNick());
        gamePlayDao.updateGame(gamePlay);
    }

    private boolean checkCorrectWord(PlayRequest playRequest, Character[][] grid, Directions direction) {

        if(direction == Directions.EAST) {

            if(!checkInEast(playRequest, grid)) {
                return false;
            }
        } else if(direction == Directions.SOUTH) {

            if(!checkInSouth(playRequest, grid)) {
                return false;
            }
        }
        else if(direction == Directions.SOUTH_EAST) {

            if(!checkInSouthEast(playRequest, grid)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkInSouthEast(PlayRequest playRequest, Character[][] grid) {

        int x = playRequest.getStart().getRow();
        int y = playRequest.getStart().getColumn();
        int i=0;
        if((playRequest.getEnd().getColumn()-playRequest.getStart().getColumn()) != (playRequest.getWord().length()-1))
            return false;

        while(y<=playRequest.getEnd().getColumn()) {
            if(playRequest.getWord().charAt(i) != grid[x][y]) {
                return false;
            }
            x++;i++;y++;
        }
        return true;
    }

    private boolean checkInSouth(PlayRequest playRequest, Character[][] grid) {

        int x = playRequest.getStart().getRow();
        int y = playRequest.getStart().getColumn();
        int i=0;
        if((playRequest.getEnd().getRow()-playRequest.getStart().getRow()) != (playRequest.getWord().length()-1))
            return false;
        while(x<=playRequest.getEnd().getRow()) {
            if(playRequest.getWord().charAt(i) != grid[x][y]) {
                return false;
            }
            x++;i++;
        }
        return true;
    }

    private boolean checkInEast(PlayRequest playRequest, Character[][] grid) {

        int x = playRequest.getStart().getRow();
        int y = playRequest.getStart().getColumn();
        int i=0;
        if((playRequest.getEnd().getColumn()-playRequest.getStart().getColumn()) != (playRequest.getWord().length()-1))
            return false;
        while(y<=playRequest.getEnd().getColumn()) {
            if(playRequest.getWord().charAt(i) != grid[x][y]) {
                return false;
            }
            y++;i++;
        }
        return true;
    }

    private boolean checkIfAlreadyFound(PlayRequest playRequest, GamePlay gamePlay) {

        List<Word> foundWords = gamePlay.getGridInfo().getFoundWords();
        for(Word word : foundWords) {

            if(word.getEnd().getRow() == playRequest.getEnd().getRow()) {
                if(word.getEnd().getColumn() == playRequest.getEnd().getColumn()) {
                    if(word.getStart().getRow() == playRequest.getStart().getRow()) {
                        if(word.getStart().getColumn() == playRequest.getStart().getColumn()) {
                            if(word.getWord().equals(playRequest.getWord())) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private Directions getDirection(PlayRequest playRequest) {

        Cell start = playRequest.getStart();
        Cell end = playRequest.getEnd();

        if(!checkValidCell(start)) return Directions.INVALID;
        if(!checkValidCell(end)) return Directions.INVALID;

        //Same row
        if(start.getRow() == end.getRow() && end.getColumn() >= start.getColumn()) {
            return Directions.EAST;
        }
        //Same column
        if(start.getColumn() == end.getColumn() && end.getRow() >= start.getRow()) {
            return Directions.SOUTH;
        }
        //Diagonal
        if(end.getColumn() >= start.getColumn() && end.getRow() >= start.getRow()
                && ((end.getColumn()-start.getColumn()) == (end.getRow()-start.getRow()))) {
            return Directions.SOUTH_EAST;
        }
        return Directions.INVALID;
    }

    private boolean checkValidCell(Cell cell) {
        return cell.getRow() >= 0 && cell.getRow() < gridSize && cell.getColumn() >= 0 && cell.getColumn() < gridSize;
    }
}
