import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DeleteBook implements BookOperationStrategy {
    @Override
    public void execute(List<Book> books, Connection connection) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Book book : books) {
                statement.setInt(1, book.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
