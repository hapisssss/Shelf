import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
                book.setStatus(resultSet.getString("status"));
                book.setProgress(resultSet.getInt("progress"));
                book.setCategory(resultSet.getString("category"));


                // Retrieve the image as a BLOB and convert it to a byte array
                InputStream imageStream = resultSet.getBinaryStream("image");
                if (imageStream != null) {
                    byte[] imageBytes = toByteArray(imageStream);
                    book.setImage(imageBytes);  // Set the byte array for the image
                } else {
                    book.setImage(null);  // If no image, set to null
                }

                books.add(book);  // Menambahkan book ke dalam list books
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Utility method to convert InputStream to byte array
    private byte[] toByteArray(InputStream inputStream) throws SQLException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new SQLException("Error reading image from input stream", e);
        }
    }
}
