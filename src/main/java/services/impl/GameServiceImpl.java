package services.impl;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import dao.GameDao;
import dao.GamePlayDao;
import dao.UserDao;
import enitities.Game;
import enitities.GamePlay;
import enitities.User;
import enums.Assertum;
import helpers.GridHelper;
import models.GameInfo;
import models.GridInfo;
import requests.CreateGameRequest;
import requests.InfoRequest;
import requests.JoinGameRequest;
import requests.StartGameRequest;
import responses.GenericResponse;
import responses.JoinGameResponse;
import services.GameService;
import utils.GameUtils;
import utils.IdGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static enums.Assertum.no;
import static enums.GameState.IN_PLAY;
import static enums.GameState.READY;
import static enums.GameState.WAITING;
import static enums.ResponseCode.*;

public class GameServiceImpl implements GameService {

    private final GameDao gameDao;
    private final UserDao userDao;
    private final GamePlayDao gamePlayDao;

    @Inject
    public GameServiceImpl(GameDao gameDao, UserDao userDao, GamePlayDao gamePlayDao) {
        this.gameDao = gameDao;
        this.userDao = userDao;
        this.gamePlayDao = gamePlayDao;
    }

    @Override
    public GenericResponse createGame(CreateGameRequest createGameRequest) throws Exception {
        //1. Create game
        Game game = getNewGame(createGameRequest);
        gameDao.createGame(game);

        //2. Create admin meta data
        User user = User.builder()
                .isAdmin(Assertum.yes)
                .userId(game.getAdminUserId())
                .nick(createGameRequest.getNick())
                .gameId(game.getGameId())
                .build();
        userDao.insertUser(user);

        return GenericResponse.builder().success(true).data(game).build();
    }

    private Game getNewGame(CreateGameRequest createGameRequest) {
        String gameId = Joiner.on("").join("G", IdGenerator.generate(5));

        //Admin user object
        User user = User.builder()
                .nick(createGameRequest.getNick())
                .userId(Joiner.on("").join("U", IdGenerator.generate(5)))
                .gameId(gameId)
                .isAdmin(Assertum.yes)
                .build();

        return Game.builder()
                .gameId(gameId)
                .adminUserId(user.getUserId())
                .info(GameInfo.builder().joinedUsers(Lists.newArrayList(user)).build())
                .maxPlayers(createGameRequest.getMaxPlayers())
                .state(WAITING.name())
                .build();
    }

    @Override
    public GenericResponse getGame(String gameId) throws Exception {
        Game game = gameDao.getGame(gameId);
        if(game == null) {
            return GenericResponse.builder().success(false).data(Collections.singletonMap("code", GAME_NOT_FOUND.name())).build();
        }
        return GenericResponse.builder().success(true).data(game).build();
    }

    @Override
    public GenericResponse joinGame(JoinGameRequest joinGameRequest) throws Exception {
        Game game = gameDao.getGame(joinGameRequest.getGameId());
        //1. If Game does not exist
        if(game == null) {
            return GenericResponse.builder().success(false).data(Collections.singletonMap("code", GAME_NOT_FOUND.name())).build();
        }

        //2. If game is not in waiting state
        if(!game.getState().equals(WAITING.name())) {
            return GenericResponse.builder().success(false).data(Collections.singletonMap("code", MAX_PLAYERS_REACHED.name())).build();
        }

        //3. If nick already exists
        User user = userDao.getUser(game.getGameId(), joinGameRequest.getNick());
        if(Objects.nonNull(user)) {
            return GenericResponse.builder().success(false).data(Collections.singletonMap("code", NICK_ALREADY_EXISTS.name())).build();
        }

        //4. Add user to the game
        User newUser = User.builder()
                .isAdmin(no)
                .gameId(game.getGameId())
                .userId(Joiner.on("").join("U", IdGenerator.generate(5)))
                .nick(joinGameRequest.getNick())
                .build();
        updateGameInfo(game, newUser);
        userDao.insertUser(newUser);

        return GenericResponse.builder()
                .success(true)
                .data(
                        JoinGameResponse.builder()
                        .nick(newUser.getNick())
                        .playerId(newUser.getUserId())
                        .registered(true)
                        .build()
                ).build();
    }

    @Override
    public GenericResponse getInfo(InfoRequest infoRequest) throws Exception {

        Game game = gameDao.getGame(infoRequest.getGameId());

        if(game.getState().equals(WAITING.name())) {

            return GenericResponse.builder().success(false).data(Collections.singletonMap("code", GAME_IN_WAITING_STATE)).build();
        }

        if(game == null) {
            return GenericResponse.builder().success(false).data(Collections.singletonMap("code", GAME_NOT_FOUND.name())).build();
        }

        User user = userDao.getUser(infoRequest.getPlayerId());
        if(user == null) {
            return GenericResponse.builder().success(false).data(Collections.singletonMap("code", PLAYER_NOT_REGISTERED.name())).build();
        }

        GamePlay gamePlay = gamePlayDao.getGamePlay(infoRequest.getGameId());

        return GenericResponse.builder()
                .success(true)
                .data(GameUtils.getInfo(game, user, gamePlay))
                .build();
    }

    @Override
    public GenericResponse startGame(StartGameRequest startGameRequest) throws Exception {
        Game game = gameDao.getGame(startGameRequest.getGameId());
        User user = userDao.getUser(startGameRequest.getPlayerId());

        if(game == null) return GenericResponse.builder().success(false).data(Collections.singletonMap("code", GAME_NOT_FOUND.name())).build();

        if(user == null) return GenericResponse.builder().success(false).data(Collections.singletonMap("code", PLAYER_NOT_REGISTERED.name())).build();

        if(user.getIsAdmin() == no) return GenericResponse.builder().success(false).data(Collections.singletonMap("code", UNAUTHORIZED_PLAYER.name())).build();

        game.setState(IN_PLAY.name());
        gameDao.updateGame(game);

        GamePlay gamePlay = getGamePlay(game);
        gamePlayDao.insertGamePlay(gamePlay);
        return GenericResponse.builder()
                .success(true)
                .data(gamePlay.getGridInfo().getGrid())
                .build();
    }

    private GamePlay getGamePlay(Game game) {
        return GamePlay.builder()
                .gameId(game.getGameId())
                .gridInfo(
                        GridInfo.builder()
                        .grid(GridHelper.getGridAsString(GridHelper.getNewGrid()))
                        .foundWords(Lists.newArrayList())
                        .build()
                )
                .lastPlayedUser(game.getInfo().getJoinedUsers().get(game.getMaxPlayers()-1).getNick())
                .build();
    }

    private void updateGameInfo(Game game, User user) {

        GameInfo info = game.getInfo();
        List<User> joinedUsers = info.getJoinedUsers();
        joinedUsers.add(user);
        info.setJoinedUsers(joinedUsers);
        game.setInfo(info);

        if(info.getJoinedUsers().size() == game.getMaxPlayers()) {
            game.setState(READY.name());
        }
        gameDao.updateGame(game);
    }
}
