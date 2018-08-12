package main;

import java.io.IOException;

public class UndercoverageMain {
  public static void main(String[] args) throws IOException {
    //required for free non-commercial use
    System.out.println("[Powered by News API: https://newsapi.org/]\n");
    UndercoverageService service = new UndercoverageService();
    service.run();
  }
}
