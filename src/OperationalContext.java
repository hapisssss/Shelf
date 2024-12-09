import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OperationalContext {
    private BookOperationStrategy bookOperation;

    public void setBookOperation(BookOperationStrategy bookOperation) {
        this.bookOperation = bookOperation;
    }

    public void executeStrategy(List<Book> books, DatabaseConnection dbConnection) {
        Connection connection = dbConnection.getConnection();
        bookOperation.execute(books, connection);
    }
}
