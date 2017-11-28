package responses;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GenericResponse {
    private boolean success;

    private Object data;
}
