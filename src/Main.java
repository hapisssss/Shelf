import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
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

            @Override
            public void handle(HttpExchange exchange) throws IOException {
                // Set CORS headers
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");  // Allow all origins (or replace with a specific one)
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS"); // Allowed methods
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type"); // Allowed headers

                // Handle preflight OPTIONS request
                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(200, -1);  // No content for OPTIONS
                    return;
                }

                String method = exchange.getRequestMethod();
                String response = "";

                try {
                    switch (method) {
                        case "GET":
                            response = handleGet();  // Handle GET request
                            exchange.sendResponseHeaders(200, response.getBytes().length);
                            break;
                        case "POST":
                            response = handlePost(exchange.getRequestBody());  // Handle POST request
                            exchange.sendResponseHeaders(201, response.getBytes().length);
                            break;
                        case "PUT":
                            response = handlePut(exchange.getRequestBody());  // Handle PUT request
                            exchange.sendResponseHeaders(200, response.getBytes().length);
                            break;
                        case "DELETE":
                            response = handleDelete(exchange.getRequestBody());  // Handle DELETE request
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

                // Send the response to the client
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
        });

        // Memulai server
        server.setExecutor(null); // Executor default
        System.out.println("Server is listening on port 8080...");
        server.start();
    }
}
