import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class AddBook implements BookOperationStrategy {

    @Override
    public void execute(List<Book> books, Connection connection) {
        String sql = "INSERT INTO books (title, author, publish, image, status, category, progress) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Book book : books) {
                // Set all the other fields as normal
                statement.setString(1, book.getTitle());
                statement.setString(2, book.getAuthor());
                statement.setDate(3, new java.sql.Date(book.getPublish().getTime()));
                statement.setString(5, book.getStatus());
                statement.setString(6, book.getCategory());
                statement.setInt(7, book.getProgress());

                // Handle the image as binary data (BLOB)
                byte[] imageBytes = book.getImage();  // Assume book.getImage() returns a byte array
                if (imageBytes != null) {
                    InputStream imageStream = new ByteArrayInputStream(imageBytes);
                    statement.setBinaryStream(4, imageStream, imageBytes.length); // Set the image as BLOB
                } else {
                    statement.setNull(4, java.sql.Types.BLOB); // If no image is provided, set it to NULL
                }

                // Execute the insert statement for each book
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
