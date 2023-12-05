package targetServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Server {
  private int numberOfThreads;
  private static final String RESOURCES_DIR = "resources/";
  private Map<String, String> books;
  private HttpServer server;

  public Server(int numberOfThreads) {
    this.numberOfThreads = numberOfThreads;
    books = new HashMap<>();
    loadBooks();
  }

  private void loadBooks() {
    try {
      Files.list(Paths.get(RESOURCES_DIR))
          .filter(path -> path.toString().endsWith(".txt"))
          .forEach(path -> {
            try {
              String text = new String(Files.readAllBytes(path));
              books.put(path.getFileName().toString(), text);
            } catch (IOException e) {
              e.printStackTrace();
            }
          });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void runServer() {
    server = null;
    try {
      server = HttpServer.create(new InetSocketAddress(8000), 0);
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    server.createContext("/search", new WordCountHandler(books));
    Executor executor = numberOfThreads > 1 ? Executors.newFixedThreadPool(numberOfThreads)
        : Executors.newSingleThreadExecutor();
    server.setExecutor(executor);
    server.start();
  }

  public void stopServer() {
    if (server != null) {
      server.stop(0);
    }
  }

  static class WordCountHandler implements HttpHandler {
    private final Map<String, String> books;

    public WordCountHandler(Map<String, String> books) {
      this.books = books;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
      String query = httpExchange.getRequestURI().getQuery();
      if (query == null || !query.startsWith("word=")) {
        sendTextResponse(httpExchange, "Invalid request", 400);
        return;
      }

      String word = query.substring(5); // After "word="
      StringBuilder response = new StringBuilder();

      for (Map.Entry<String, String> entry : books.entrySet()) {
        String bookName = entry.getKey();
        String text = entry.getValue();
        long count = countWord(word, text);
        response.append(formatBookName(bookName)).append(": ").append(count).append("\n");
      }

      sendTextResponse(httpExchange, response.toString(), 200);
    }

    private long countWord(String word, String text) {
      Pattern pattern = Pattern.compile("\\b" + Pattern.quote(word) + "\\b");
      Matcher matcher = pattern.matcher(text);
      long count = 0;
      while (matcher.find()) {
        count++;
      }
      return count;
    }

    private void sendTextResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
      byte[] responseBytes = response.getBytes();
      exchange.sendResponseHeaders(statusCode, responseBytes.length);
      try (OutputStream outputStream = exchange.getResponseBody()) {
        outputStream.write(responseBytes);
      }
    }

    private String formatBookName(String fileName) {
      String withoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
      String[] parts = withoutExtension.split("_");
      return Arrays.stream(parts)
          .map(part -> Character.toUpperCase(part.charAt(0)) + part.substring(1))
          .collect(Collectors.joining(" "));
    }
  }
}
