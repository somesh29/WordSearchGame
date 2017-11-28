package requests;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

@Data
@Getter
@Setter
public class CreateGameRequest {
    private String nick;
    @Min(2)
    private int maxPlayers;
}
