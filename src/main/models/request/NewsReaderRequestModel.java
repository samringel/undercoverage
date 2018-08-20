package main.models.request;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/** Immutable representation of request to newspai.org */
public class NewsReaderRequestModel {
  private static final String url = "https://newsapi.org/v2/everything?sources=%s&from=%s&apiKey=%s";
  private static final String termParam = "&q=\"%s\"";

  private final String term; //nullable
  private final String source;
  private final String date;

  private NewsReaderRequestModel(String term, String source) {
    if (term != null) {
      this.term = encodeParam(term);
    } else {
      this.term = null;
    }
    this.source = encodeParam(source);
    date = getNewDate();
  }

  /**
   * Build newsapi.org request model for searching news source by term
   *
   * @param term term to search for
   * @param source news source id
   * @return built request model
   */
  public static NewsReaderRequestModel buildTermSearch(String term, String source) {
    return new NewsReaderRequestModel(term, source);
  }

  /**
   * Build newsapi.org request model for searching all articles from news source
   *
   * @param source news source id
   * @return built request model
   */
  public static NewsReaderRequestModel buildSearch(String source) {
    return new NewsReaderRequestModel(null, source);
  }


  /**
   * Gets url for newsapi.org request from fields in class
   *
   * @param apiKey API key for newsapi.org request
   * @return url from fields
   */
  public URL getUrl(String apiKey) {
    try {
      String builtUrl = String.format(url, source, date, apiKey);
      if (term != null) {
        builtUrl = builtUrl.concat(String.format(termParam, term));
      }
      return new URL(builtUrl);
    } catch (MalformedURLException e) {
      System.err.println("Error building request url. Contact the developer if this problem persists.");
      System.exit(1);
      return null;
    }
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
   * Gets date of 1 month before current instant in ISO 8601 format
   *
   * @return calculated date
   */
  private String getNewDate() {
    //while newsapi.org documentation says results from over a month ago will not be returned,
    //actual results have included ones from earlier - this ensures a consistent range among calls
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, -1);
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    return dateFormat.format(calendar.getTime());
  }
}
