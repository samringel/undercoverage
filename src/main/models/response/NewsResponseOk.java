package main.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Model for newsapi.org ok response */
@JsonIgnoreProperties(value={"status"})
public class NewsResponseOk {
  private int totalResults;
  private Article[] articles;

  public int getTotalResults() {
    return totalResults;
  }

  public Article[] getArticles() {
    return articles;
  }

  public void setTotalResults(int totalResults) {
    this.totalResults = totalResults;
  }

  public void setArticles(Article[] articles) {
    this.articles = articles;
  }
}
