package infrastructure;

import java.io.IOException;

public interface Queue {
    void send(String message) throws IOException;
}
