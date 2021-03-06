package main.models.response;

/** Model for element of articles field in newsapi.org ok response */
public class Article {
  private BriefSource source;
  private String author;
  private String title;
  private String description;
  private String url;
  private String urlToImage;
  private String publishedAt;

  public BriefSource getSource() {
    return source;
  }

  public String getAuthor() {
    return author;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getUrl() {
    return url;
  }

  public String getUrlToImage() {
    return urlToImage;
  }

  public String getPublishedAt() {
    return publishedAt;
  }

  public void setSource(BriefSource source) {
    this.source = source;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setUrlToImage(String urlToImage) {
    this.urlToImage = urlToImage;
  }

  public void setPublishedAt(String publishedAt) {
    this.publishedAt = publishedAt;
  }
}
