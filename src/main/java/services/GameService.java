package services;

import requests.CreateGameRequest;
import requests.InfoRequest;
import requests.JoinGameRequest;
import requests.StartGameRequest;
import responses.GenericResponse;
import responses.InfoResponse;

public interface GameService {

    GenericResponse createGame(CreateGameRequest createGameRequest) throws Exception;

    GenericResponse getGame(String gameId) throws Exception;

    GenericResponse joinGame(JoinGameRequest joinGameRequest) throws Exception;

    GenericResponse getInfo(InfoRequest infoRequest) throws Exception;

    GenericResponse startGame(StartGameRequest startGameRequest) throws Exception;
}
