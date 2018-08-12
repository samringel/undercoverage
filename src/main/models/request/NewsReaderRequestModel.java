package main.models.request;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.stream.Stream;

public class NewsReaderRequestModel {
  private final String url = "https://newsapi.org/v2/everything?q=\"%s\"&sources=%s&from=%s&apiKey=%s";

  private String term;
  private String[] sources;
  private String date;

  public NewsReaderRequestModel(String term, String[] sources) {
    setTerm(term);
    setSources(sources);
    initDate();
  }

  /**
   * Builds url for newsapi.org request from fields in class
   *
   * @param apiKey API key for newsapi.org request
   * @return built url from fields
   */
  public URL buildUrl(String apiKey) {
    String sourcesString = String.join(",", sources);
    try {
      return new URL(String.format(url, term, sourcesString, date, apiKey));
    } catch (MalformedURLException e) {
      System.err.println("Error building request url. Contact the developer if this problem persists.");
      System.exit(1);
      return null;
    }
  }

  private void setTerm(String term) {
    this.term = encodeParam(term);
  }

  private void setSources(String[] sources) {
    this.sources = Stream.of(sources)
        .map(this::encodeParam)
        .toArray(String[]::new);
  }

  /**
   * Prepares url parameter with UTF-8
   *
   * @param param raw url parameter
   * @return encoded parameter
   */
  private String encodeParam(String param) {
    try {
      return URLEncoder.encode(param, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      System.err.println("Error building request url. Contact the developer if this problem persists.");
      System.exit(1);
      return null;
    }
  }

  /**
   * Sets {@code date} to 1 month before current instant in ISO 8601 format
   */
  private void initDate() {
    //while newsapi.org documentation says results from over a month ago will not be returned,
    //actual results have included ones from earlier - this ensures a consistent range among calls
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, -1);
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    date = dateFormat.format(calendar.getTime());
  }
}
