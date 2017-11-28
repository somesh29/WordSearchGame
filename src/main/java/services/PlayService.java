package services;

import requests.PlayRequest;
import responses.GenericResponse;

import java.io.IOException;

public interface PlayService {

    GenericResponse play(PlayRequest playRequest) throws Exception;
}
