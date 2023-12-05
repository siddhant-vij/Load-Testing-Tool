import java.io.IOException;

import targetServer.Server;

public class PerformanceCheck {
  public static void main(String[] args) {
    int numberOfThreads = 1;
    try {
      new Server(numberOfThreads).runServer();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
