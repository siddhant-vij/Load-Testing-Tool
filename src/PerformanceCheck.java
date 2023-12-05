import targetServer.Server;
import loadTestingTool.LoadTester;
import loadTestingTool.LoadTester.PerformanceMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PerformanceCheck {
  private static final String SERVER_URL = "http://localhost:8000/search?word=";
  private static final String CSV_FILE_PATH = "resources/search_words.csv";
  private static final int MAX_THREADS = 16;

  public static void main(String[] args) throws Exception {
    List<String> words = loadWordsFromCSV(CSV_FILE_PATH);
    for (int serverThreads = 1; serverThreads <= MAX_THREADS; serverThreads++) {
      Server server = new Server(serverThreads);
      Thread serverThread = new Thread(server::runServer);
      serverThread.start();
      for (int testerThreads = 1; testerThreads <= MAX_THREADS; testerThreads++) {
        testPerformance(serverThreads, testerThreads, words);
      }
      server.stopServer();
      serverThread.join();
      Thread.sleep(1000);
    }
  }

  private static void testPerformance(int numOfServerThreads, int numberOfThreads, List<String> words)
      throws Exception {
    ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
    LoadTester task = new LoadTester(SERVER_URL, words);

    long startTime = System.currentTimeMillis();
    Future<PerformanceMetrics> future = executor.submit(task);
    PerformanceMetrics metrics = future.get();
    long endTime = System.currentTimeMillis();

    executor.shutdownNow();
    executor.awaitTermination(30, TimeUnit.SECONDS);

    long testDuration = endTime - startTime;
    double throughput = (double) metrics.getSuccessfulRequests() / (testDuration / 1000.0);

    System.out.printf("Server Threads: %d, Tester Threads: %d, Average Latency: %d ns, Throughput: %.2f requests/sec\n",
        numOfServerThreads,
        numberOfThreads,
        metrics.getAverageLatency(),
        throughput);
  }

  private static List<String> loadWordsFromCSV(String csvFilePath) {
    try {
      return Files.lines(Paths.get(csvFilePath))
          .collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }
}
