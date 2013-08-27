package infrastructure.redis;

import java.util.List;

public interface Redis {
    void save(String message);
    List<String> messages();
}
