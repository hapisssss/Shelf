import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class ShowBook implements BookOperationStrategy {
    @Override
    public void execute(List<Book> books, Connection connection) {
        String sql = "SELECT * FROM books";

        // Mengambil data dari database dan menambahkan ke dalam list books
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Book book = new Book();
                book.setId(resultSet.getInt("id"));
                book.setTitle(resultSet.getString("title"));
                book.setAuthor(resultSet.getString("author"));
                book.setPublish(resultSet.getDate("publish"));
                book.setImage(resultSet.getString("image"));
                book.setStatus(resultSet.getString("status"));
                books.add(book);  // Menambahkan book ke dalam list books
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
