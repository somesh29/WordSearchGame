package resources;

import com.google.inject.Inject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import requests.*;
import responses.GenericResponse;
import services.GameService;
import services.PlayService;


import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/game")
@Api(value = "game apis")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GameResource {

    private final GameService gameService;
    private final PlayService playService;

    @Inject
    public GameResource(GameService gameService, PlayService playService) {
        this.gameService = gameService;
        this.playService = playService;
    }

    @POST
    @Path("/create")
    @ApiOperation("Create a game")
    @ApiResponses({
            @ApiResponse(code = 200, response = Response.class, message = "Success"),
            @ApiResponse(code = 500, response = Exception.class, message = "Failure")
    })
    public GenericResponse createGame(@Valid CreateGameRequest createGameRequest) throws Exception {
        return gameService.createGame(createGameRequest);
    }

    @GET
    @Path("/get/{gameId}")
    @ApiOperation("Get a game")
    @ApiResponses({
            @ApiResponse(code = 200, response = Response.class, message = "Success"),
            @ApiResponse(code = 500, response = Exception.class, message = "Failure")
    })
    public GenericResponse getGame(@PathParam("gameId") String gameId) throws Exception {
        return gameService.getGame(gameId);
    }

    @POST
    @Path("/join")
    @ApiOperation("Join a game")
    @ApiResponses({
            @ApiResponse(code = 200, response = Response.class, message = "Success"),
            @ApiResponse(code = 500, response = Exception.class, message = "Failure")
    })
    public GenericResponse joinGame(@Valid JoinGameRequest joinGameRequest) throws Exception {
        return gameService.joinGame(joinGameRequest);
    }

    @POST
    @Path("/info")
    @ApiOperation("Get info of a game")
    @ApiResponses({
            @ApiResponse(code = 200, response = Response.class, message = "Success"),
            @ApiResponse(code = 500, response = Exception.class, message = "Failure")
    })
    public GenericResponse getInfo(@Valid InfoRequest infoRequest) throws Exception {
        return gameService.getInfo(infoRequest);
    }

    @POST
    @Path("/start")
    @ApiOperation("Start a game")
    @ApiResponses({
            @ApiResponse(code = 200, response = Response.class, message = "Success"),
            @ApiResponse(code = 500, response = Exception.class, message = "Failure")
    })
    public GenericResponse getInfo(@Valid StartGameRequest startGameRequest) throws Exception {
        return gameService.startGame(startGameRequest);
    }

    @POST
    @Path("/play")
    @ApiOperation("Play a move")
    @ApiResponses({
            @ApiResponse(code = 200, response = Response.class, message = "Success"),
            @ApiResponse(code = 500, response = Exception.class, message = "Failure")
    })
    public GenericResponse play(@Valid PlayRequest playRequest) throws Exception {
        return playService.play(playRequest);
    }
}
