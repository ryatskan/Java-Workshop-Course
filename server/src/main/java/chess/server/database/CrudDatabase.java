package chess.server.database;

import org.springframework.data.repository.CrudRepository;

public interface CrudDatabase extends CrudRepository<User, String> {
}
