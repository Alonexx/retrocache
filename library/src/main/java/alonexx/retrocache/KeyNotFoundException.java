package alonexx.retrocache;

import java.io.IOException;

/**
 * Signals that there's no corresponding record for a specified key found.
 */
public class KeyNotFoundException extends IOException {

    public KeyNotFoundException() {
    }

    public KeyNotFoundException(String message) {
        super(message);
    }

    public KeyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyNotFoundException(Throwable cause) {
        super(cause);
    }
}
