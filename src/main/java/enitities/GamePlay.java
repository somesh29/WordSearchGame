package enitities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.GridInfo;

@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GamePlay {

    private Long id;
    private String lastPlayedUser;
    private String gameId;
    private GridInfo gridInfo;
}
