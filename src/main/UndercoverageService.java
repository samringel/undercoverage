package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import main.models.request.NewsReaderRequestModel;
import main.models.response.FullSource;
import main.models.response.NewsResponseError;
import main.models.response.NewsResponseOk;
import main.models.response.NewsSources;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Scanner;

class UndercoverageService {
  private Scanner s = new Scanner(System.in);
  private ObjectMapper objectMapper = new ObjectMapper();
  private String apiKey;
  private ImmutableBiMap<String, String> sourceNameIdMap;

  UndercoverageService() {}

  void run() {
    System.out.println(
        "Welcome to Undercoverage!\nTo run Undercoverage, you must have a valid API key from https://newsapi.org/register.");
    apiKey = getApiKeyAndSetSources();
    System.out.println(
        "That key is valid! Using Undercoverage, you can compare the amount of coverage dedicated to different subjects by two different media outlets.\n");
    String source1Id = promptForSource(0, ImmutableSet.of());
    String source2Id = promptForSource(1, ImmutableSet.of(source1Id));
    System.out.println(
        String.format(
            "We will be comparing coverage by %s and %s. Given a term you input, we will tell you what percentage of both outlets' articles include that term.",
            sourceNameIdMap.inverse().get(source1Id), sourceNameIdMap.inverse().get(source2Id)));
    Double source1Total = getTotalResults(NewsReaderRequestModel.buildSearch(source1Id));
    Double source2Total = getTotalResults(NewsReaderRequestModel.buildSearch(source2Id));
    if (source1Total == null || source2Total == null) {
      System.exit(1);
    }
    while (true) {
      String term = promptForTerm();
      System.out.println("Reading through news articles...");
      Double relevantSource1 =
          getTotalResults(NewsReaderRequestModel.buildTermSearch(term, source1Id));
      Double relevantSource2 =
          getTotalResults(NewsReaderRequestModel.buildTermSearch(term, source2Id));
      if (relevantSource1 == null || relevantSource2 == null) {
        continue;
      }
      double percentSource1 = relevantSource1 / source1Total * 100;
      double percentSource2 = relevantSource2 / source2Total * 100;
      System.out.printf(
          "%.3f%% of %s articles and %.3f%% of %s articles in the past month covered \"%s\".\n",
          percentSource1,
          sourceNameIdMap.inverse().get(source1Id),
          percentSource2,
          sourceNameIdMap.inverse().get(source2Id),
          term);
    }
  }

  /**
   * Prompts for api key and reads value from {@code s}. Returns that value if it is a valid API key
   * accepted by newsapi.org, otherwise reprompts.
   *
   * <p>If api key is valid, also sets {@code sourceNameIdMap} and {@code sourceIds} to sources
   * retrieved from newsapi.org
   *
   * @return valid API key
   */
  private String getApiKeyAndSetSources() {
    s.reset();
    System.out.print("Please provide your API key:\n> ");
    String apiKey = s.nextLine().trim();

    if (!apiKey.matches("[a-zA-Z0-9]+")) {
      System.out.println("That API key has invalid characters. Try again with a valid key.");
      return getApiKeyAndSetSources();
    }

    String error = setSourcesIfValidApiKey(apiKey);

    if (error == null) {
      return apiKey;
    } else {
      printApiKeyError(error);
      System.out.println("Try again with a different key.");
      return getApiKeyAndSetSources();
    }
  }

  /**
   * Attempts to get list of sources from newsapi.org using {@code apiKey}. If request succeeds,
   * sets {@code sourceNameIdMap}
   *
   * @param apiKey api key to use
   * @return error code if request failed, null if it succeeded
   */
  private String setSourcesIfValidApiKey(String apiKey) {
    try {
      HttpURLConnection connection =
          (HttpURLConnection)
              new URL(String.format("https://newsapi.org/v2/sources?&apiKey=%s", apiKey))
                  .openConnection();

      if (connection.getResponseCode() == 200) {
        InputStream responseStream = connection.getInputStream();
        NewsSources response = objectMapper.readValue(responseStream, NewsSources.class);

        sourceNameIdMap =
            response
                .getSources()
                .stream()
                .collect(
                    ImmutableBiMap.toImmutableBiMap(
                        source -> normalize(source.getName()), FullSource::getId));

        return null;
      } else {
        InputStream responseStream = connection.getErrorStream();
        NewsResponseError response =
            objectMapper.readValue(responseStream, NewsResponseError.class);
        return response.getCode();
      }
    } catch (MalformedURLException e) {
      System.out.println("Error building request url");
      System.exit(1);
      return null;
    } catch (IOException e) {
      System.out.println("Error connecting to newsapi.org. Check your connection and try again.");
      e.printStackTrace();
      return getApiKeyAndSetSources();
    }
  }

  /**
   * Trims and lowercases string for easier lookup
   *
   * @param s string to normalize
   * @return normalized string
   */
  private String normalize(String s) {
    return s.trim().toLowerCase();
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
   * Prompts for and retrieves a news source to analyze. Ensures source is in {@code
   * sourceNameIdMap} and has not already been entered.
   *
   * @param index which number source this is for the user. 0-indexed. currently only supports 0 and
   *     1.
   * @param sourceIds sources user has already inputted
   * @return id of source
   */
  private String promptForSource(int index, Collection<String> sourceIds) {
    s.reset();
    System.out.print(
        String.format(
            "Please enter the %s news source you want to analyze. You can either use the source's name or id on newsapi.org.\n> ",
            index == 0 ? "first" : "second"));
    String potentialSource = s.nextLine().trim().toLowerCase();
    if (sourceNameIdMap.containsKey(potentialSource)) {
      if (sourceIds.contains(sourceNameIdMap.get(potentialSource))) {
        System.out.println(
            String.format(
                "You have already entered %s. Try again with a different source.",
                potentialSource));
        return promptForSource(index, sourceIds);
      } else {
        return sourceNameIdMap.get(potentialSource);
      }
    } else if (sourceNameIdMap.containsValue(potentialSource)) {
      if (sourceIds.contains(potentialSource)) {
        System.out.println(
            String.format(
                "You have already entered %s. Try again with a different source.",
                sourceNameIdMap.inverse().get(potentialSource)));
        return promptForSource(index, sourceIds);
      } else {
        return potentialSource;
      }
    } else {
      System.out.println(
          "Source not recognized. The spelling must be completely correct, look up your source on newsapi.org if you are unsure.");
      return promptForSource(index, sourceIds);
    }
  }

  /**
   * Prompts for term
   *
   * @return entered term
   */
  private String promptForTerm() {
    s.reset();
    System.out.print("Look up a term:\n> ");
    String term = s.nextLine().trim();
    if (term.startsWith("\"") && term.endsWith("\"")) {
      term = term.substring(1, term.length() - 1);
    }
    return term;
  }

  /**
   * Gets the total number of articles from newsapi.org request
   *
   * @param requestModel model containing request parameters
   * @return number of articles. null if request failed
   */
  private Double getTotalResults(NewsReaderRequestModel requestModel) {
    try {
      HttpURLConnection connection =
          (HttpURLConnection) requestModel.getUrl(apiKey).openConnection();

      if (connection.getResponseCode() == 200) {
        InputStream responseStream = connection.getInputStream();
        NewsResponseOk response = objectMapper.readValue(responseStream, NewsResponseOk.class);
        return (double) response.getTotalResults();
      } else {
        InputStream responseStream = connection.getErrorStream();
        NewsResponseError response =
            objectMapper.readValue(responseStream, NewsResponseError.class);
        System.err.println(
            String.format(
                "Error fetching data: \"%s\"\nTry again, and contact the developer if this problem persists.",
                response.getMessage()));
        return null;
      }
    } catch (IOException e) {
      System.err.println(
          "Error fetching data from newsapi.org. Check your internet connection and contact the developer if this problem persists.");
      return null;
    }
  }
}
