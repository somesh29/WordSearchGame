package responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.GameState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.Word;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class InfoResponse {
    private GameState gameState;

    private String currentPlayer;

    private List<String> turnSequence;

    private List<Word> foundWords;

    private Map<String, Integer> scores;

    private String grid;

}
