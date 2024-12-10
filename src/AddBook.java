import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AddBook implements BookOperationStrategy {
    @Override
    public void execute(List<Book> books, Connection connection) {
        String sql = "INSERT INTO books (title, author, image, category, progress) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Book book : books) {
                statement.setString(1, book.getTitle());
                statement.setString(2, book.getAuthor());
//                statement.setDate(3, new java.sql.Date(book.getPublish().getTime()));
                statement.setString(3, book.getImage());
//                statement.setString(5, book.getStatus());
                statement.setString(4, book.getCategory());
                statement.setInt(5, book.getProgress());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
