package main.models.response;

/** Model for newsapi.org ok response */
public class NewsResponseOk implements NewsResponse {
  private String status = "ok";
  private int totalResults;
  private Article[] articles;

  @Override
  public String getStatus() {
    return status;
  }

  public int getTotalResults() {
    return totalResults;
  }

  public Article[] getArticles() {
    return articles;
  }

  @Override
  public void setStatus(String status) {
    this.status = status;
  }

  public void setTotalResults(int totalResults) {
    this.totalResults = totalResults;
  }

  public void setArticles(Article[] articles) {
    this.articles = articles;
  }
}
