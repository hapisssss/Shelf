import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UpdateBook implements BookOperationStrategy {
    @Override
    public void execute(List<Book> books, Connection connection) {
        String sql = "UPDATE books SET title = ?, author = ?, publish = ?, image = ?, status = ?, category = ?, progress = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Book book : books) {
                statement.setString(1, book.getTitle());
                statement.setString(2, book.getAuthor());
                statement.setDate(3, new java.sql.Date(book.getPublish().getTime()));
                statement.setString(4, book.getImage());
                statement.setString(5, book.getStatus());
                statement.setString(6, book.getCategory());
                statement.setInt(7, book.getProgress());
                statement.setInt(8, book.getId());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}