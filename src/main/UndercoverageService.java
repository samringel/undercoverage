package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.models.request.NewsReaderRequestModel;
import main.models.response.NewsResponse;
import main.models.response.NewsResponseError;
import main.models.response.NewsResponseOk;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class UndercoverageService {
  private Scanner s = new Scanner(System.in);
  private ObjectMapper objectMapper = new ObjectMapper();

  public UndercoverageService() {}

  public void run() {
    System.out.println(
        "Welcome to Undercoverage!\nTo run Undercoverage, you must have a valid API key from https://newsapi.org/register.");
    String apiKey = getApiKey();
    System.out.println(
        "That key is valid! For this initial version of Undercoverage, we will look at New York Times articles over the past month.\n"
            + "Given your term, we will tell you how many times The New York Times has published an article containing that term.");
    while (true) {
      NewsReaderRequestModel requestModel = getRequestModel();
      int numArticles = getTotalResults(requestModel, apiKey);
      System.out.println(
          String.format(
              "The New York Times has published %d articles in the past month containing that term!",
              numArticles));
    }
  }

  /**
   * Prompts for api key and reads value from {@code s}. Returns that value if it is a valid API key
   * accepted by newsapi.org, otherwise reprompts.
   *
   * @return valid API key
   */
  private String getApiKey() {
    s.reset();
    System.out.print("Please provide your API key:\n> ");
    String apiKey = s.nextLine().trim();

    if (!apiKey.matches("[a-zA-Z0-9]+")) {
      System.out.println("That API key has invalid characters. Try again with a valid key.");
      return getApiKey();
    }
    try {
      HttpURLConnection connection =
          (HttpURLConnection)
              new URL(
                      String.format(
                          "https://newsapi.org/v2/top-headlines?country=us&apiKey=%s", apiKey))
                  .openConnection();

      if (connection.getResponseCode() == 200) {
        return apiKey;
      }

      InputStream responseStream = connection.getErrorStream();
      NewsResponseError response =
          (NewsResponseError) objectMapper.readValue(responseStream, NewsResponse.class);
      printApiKeyError(response.getCode());
      System.out.println("Try again with a different key.");
      return getApiKey();
    } catch (MalformedURLException e) {
      System.out.println("Error building request url");
      System.exit(1);
      return null;
    } catch (IOException e) {
      System.out.println("Error connecting to newsapi.org. Check your connection and try again.");
      return getApiKey();
    }
  }

  /**
   * Prints a message for handled API key errors
   *
   * @param code response code as provided by newsapi.org
   */
  private void printApiKeyError(String code) {
    switch (code) {
      case "apiKeyDisabled":
        System.out.print("That API key has been disabled. ");
        break;
      case "apiKeyExhausted":
        System.out.print("That API key has been exhausted and is no longer valid. ");
        break;
      case "apiKeyInvalid":
        System.out.print("That API key is invalid. ");
        break;
      case "rateLimited":
        System.out.print("That API key has been rate limited (it may be valid again soon). ");
        break;
      default:
        System.err.print(
            String.format(
                "API key verification failed due to an unhandled reason (%s). Contact the developer if this problem persists. ",
                code));
        break;
    }
  }

  /**
   * Prompts for term and builds {@code NewsReaderRequestModel} to search for that term in New York Times articles
   *
   * @return built request model
   */
  private NewsReaderRequestModel getRequestModel() {
    s.reset();
    System.out.print("Look up a term:\n> ");
    String term = s.nextLine().trim();
    if (term.startsWith("\"") && term.endsWith("\"")) {
      term = term.substring(1, term.length() - 1);
    }
    return new NewsReaderRequestModel(term, new String[] {"the-new-york-times"});
  }

  /**
   * Gets the total number of articles from newsapi.org request
   *
   * @param requestModel model containing request parameters
   * @param apiKey API key to access newsapi.org
   * @return number of articles
   */
  private int getTotalResults(NewsReaderRequestModel requestModel, String apiKey) {
    try {
      InputStream responseStream = requestModel.buildUrl(apiKey).openStream();
      NewsResponseOk response =
          (NewsResponseOk) objectMapper.readValue(responseStream, NewsResponse.class);
      return response.getTotalResults();
    } catch (IOException e) {
      System.err.println("Error fetching data. Contact the developer if this problem persists.");
      System.exit(1);
      return 0;
    }
  }
}
