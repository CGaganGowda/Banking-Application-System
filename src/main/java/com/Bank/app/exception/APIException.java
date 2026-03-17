

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public class TodoApiException extends RuntimeException {
    private HttpStatus status;
    private String message;
}
