import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class UpdateBook implements BookOperationStrategy {

    // Implementing the abstract method from the interface
    @Override
    public void execute(List<Book> books, Connection connection) {
        String sql = "UPDATE books SET title = ?, author = ?, publish = ?, image = ?, status = ?, category = ?, progress = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Book book : books) {
                // Set all the fields as usual
                statement.setString(1, book.getTitle());
                statement.setString(2, book.getAuthor());
                statement.setDate(3, new java.sql.Date(book.getPublish().getTime()));
                statement.setString(5, book.getStatus());
                statement.setString(6, book.getCategory());
                statement.setInt(7, book.getProgress());
                statement.setInt(8, book.getId());

                // Handle the image as binary data (BLOB)
                byte[] imageBytes = book.getImage();  // Assuming getImage() returns byte array
                if (imageBytes != null) {
                    InputStream imageStream = new ByteArrayInputStream(imageBytes);
                    statement.setBinaryStream(4, imageStream, imageBytes.length); // Set the image as BLOB
                } else {
                    statement.setNull(4, java.sql.Types.BLOB); // If no image is provided, set it to NULL
                }

                // Execute the update statement for each book
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
