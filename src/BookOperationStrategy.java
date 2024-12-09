import java.sql.Connection;
import java.util.List;

public interface BookOperationStrategy {
    void execute(List<Book> books, Connection connection);
}
