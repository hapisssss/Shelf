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
                // Set CORS headers
                setCorsHeaders(exchange);

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
                        case "OPTIONS":
                            // Handle preflight request
                            response = "";
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

            private void setCorsHeaders(HttpExchange exchange) {
                // Add CORS headers to every response
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
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
