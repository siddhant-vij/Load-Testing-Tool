package loadTestingTool;

import loadTestingTool.LoadTester.PerformanceMetrics;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.util.List;
import java.util.concurrent.Callable;

public class LoadTester implements Callable<PerformanceMetrics> {
  private final String serverUrl;
  private final List<String> words;

  public LoadTester(String serverUrl, List<String> words) {
    this.serverUrl = serverUrl;
    this.words = words;
  }

  @Override
  public PerformanceMetrics call() throws Exception {
    long totalLatency = 0;
    int successfulRequests = 0;

    for (String word : words) {
      long startTime = System.nanoTime();
      URL url = new URI(serverUrl + word).toURL();
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      if (connection.getResponseCode() == 200) {
        successfulRequests++;
      }

      long endTime = System.nanoTime();
      totalLatency += endTime - startTime;
      connection.disconnect();
    }

    long averageLatency = successfulRequests > 0 ? totalLatency / successfulRequests : 0;
    return new PerformanceMetrics(successfulRequests, averageLatency);
  }

  public class PerformanceMetrics {
    private final int successfulRequests;
    private final long averageLatency;

    public PerformanceMetrics(int successfulRequests, long averageLatency) {
      this.successfulRequests = successfulRequests;
      this.averageLatency = averageLatency;
    }

    public int getSuccessfulRequests() {
      return successfulRequests;
    }

    public long getAverageLatency() {
      return averageLatency;
    }
  }
}
