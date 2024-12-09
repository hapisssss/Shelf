import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class Main {
    public static void main(String[] args) throws Exception {
        // Membuat HTTP Server di port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Menambahkan handler API di endpoint /books
        server.createContext("/books", new HttpHandler() {
            private final OperationalContext context = new OperationalContext();
            private DatabaseConnection dbConnection = DatabaseConnection.getInstance();

            private final Gson gson = new Gson();

            public HttpHandler create() throws SQLException {
                dbConnection = DatabaseConnection.getInstance();
                return this;
            }

            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String method = exchange.getRequestMethod();
                String response = "";

                try {
                    switch (method) {
                        case "GET":
                            response = handleGet(); // Show books
                            exchange.sendResponseHeaders(200, response.getBytes().length);
                            break;
                        case "POST":
                            response = handlePost(exchange.getRequestBody()); // Add books
                            exchange.sendResponseHeaders(201, response.getBytes().length);
                            break;
                        case "PUT":
                            response = handlePut(exchange.getRequestBody()); // Update books
                            exchange.sendResponseHeaders(200, response.getBytes().length);
                            break;
                        case "DELETE":
                            response = handleDelete(exchange.getRequestBody()); // Delete books
                            exchange.sendResponseHeaders(200, response.getBytes().length);
                            break;
                        default:
                            response = "Method not allowed";
                            exchange.sendResponseHeaders(405, response.getBytes().length);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    response = "Internal Server Error";
                    exchange.sendResponseHeaders(500, response.getBytes().length);
                }

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }

            private String handleGet() {
                List<Book> books = new ArrayList<>();
                context.setBookOperation(new ShowBook());  // Menyiapkan operasi ShowBook
                context.executeStrategy(books, dbConnection);  // Mengambil daftar buku
                return gson.toJson(books);  // Mengembalikan daftar buku dalam format JSON
            }


            private String handlePost(InputStream requestBody) throws IOException {
                Type listType = new TypeToken<List<Book>>() {}.getType();
                List<Book> books = gson.fromJson(new InputStreamReader(requestBody), listType);
                context.setBookOperation(new AddBook());
                context.executeStrategy(books, dbConnection);
                return "Books added successfully!";
            }


            private String handlePut(InputStream requestBody) throws IOException {
                Type listType = new TypeToken<List<Book>>() {}.getType();
                List<Book> books = gson.fromJson(new InputStreamReader(requestBody), listType);

                context.setBookOperation(new UpdateBook());
                context.executeStrategy(books, dbConnection);
                return "Books updated successfully!";
            }

            private String handleDelete(InputStream requestBody) throws IOException {
                Type listType = new TypeToken<List<Book>>() {}.getType();
                List<Book> books = gson.fromJson(new InputStreamReader(requestBody), listType);

                context.setBookOperation(new DeleteBook());
                context.executeStrategy(books, dbConnection);
                return "Books deleted successfully!";
            }
        }.create());

        // Memulai server
        server.setExecutor(null); // Executor default
        System.out.println("Server is listening on port 8080...");
        server.start();
    }
}




//        // MELAKUKAN ADD BOOK
//        try {
//            // Ambil instance koneksi database (Singleton Pattern)
//            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
//
//            // Buat objek buku yang ingin ditambahkan
//            Book book1 = new Book();
//            book1.setTitle("Effective Java");
//            book1.setAuthor("Joshua Bloch");
//            book1.setPublish(new Date()); // Tanggal hari ini
//            book1.setImage("effective_java.jpg");
//            book1.setStatus("Available");
//
//            Book book2 = new Book();
//            book2.setTitle("Design Patterns: Elements of Reusable Object-Oriented Software");
//            book2.setAuthor("Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides");
//            book2.setPublish(new Date());
//            book2.setImage("design_patterns.jpg");
//            book2.setStatus("Available");
//
//            // Masukkan buku-buku ke dalam list
//            List<Book> booksToAdd = new ArrayList<>();
//            booksToAdd.add(book1);
//            booksToAdd.add(book2);
//
//            // Gunakan OperationalContext untuk menambahkan buku
//            OperationalContext context = new OperationalContext();
//            context.setBookOperation(new AddBook()); // Gunakan strategi AddBook
//            context.executeStrategy(booksToAdd, dbConnection);
//
//            System.out.println("Buku berhasil ditambahkan ke database!");
//
//        } catch (SQLException e) {
//            System.err.println("Terjadi kesalahan saat menghubungkan ke database: " + e.getMessage());
//        }



//        // MELAKUKAN SHOW BOOK
//        try {
//            // Ambil instance koneksi database (Singleton Pattern)
//            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
//
//            // Buat list kosong (tidak perlu diisi untuk operasi ShowBook)
//            List<Book> booksToShow = new ArrayList<>();
//
//            // Gunakan OperationalContext untuk menampilkan buku
//            OperationalContext context = new OperationalContext();
//            context.setBookOperation(new ShowBook()); // Gunakan strategi ShowBook
//            context.executeStrategy(booksToShow, dbConnection);
//
//        } catch (SQLException e) {
//            System.err.println("Terjadi kesalahan saat menghubungkan ke database: " + e.getMessage());
//        }

//
//        // MELAKUKAN UPDATE BOOK
//        try {
//            // Ambil instance koneksi database (Singleton Pattern)
//            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
//
//            // Buat objek buku yang ingin diperbarui
//            Book bookToUpdate = new Book();
//            bookToUpdate.setId(1); // ID buku yang akan di-update
//            bookToUpdate.setTitle("Effective Java (3rd Edition)");
//            bookToUpdate.setAuthor("Joshua Bloch");
//            bookToUpdate.setPublish(new Date()); // Tanggal update (misalnya tanggal sekarang)
//            bookToUpdate.setImage("effective_java_3rd.jpg");
//            bookToUpdate.setStatus("pantek");
//
//            // Masukkan buku ke dalam list
//            List<Book> booksToUpdate = new ArrayList<>();
//            booksToUpdate.add(bookToUpdate);
//
//            // Gunakan OperationalContext untuk mengupdate buku
//            OperationalContext context = new OperationalContext();
//            context.setBookOperation(new UpdateBook()); // Gunakan strategi UpdateBook
//            context.executeStrategy(booksToUpdate, dbConnection);
//
//            System.out.println("Buku berhasil diperbarui!");
//
//        } catch (SQLException e) {
//            System.err.println("Terjadi kesalahan saat menghubungkan ke database: " + e.getMessage());
//        }
//
