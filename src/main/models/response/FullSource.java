package main.models.response;

/** Model for source field in newsapi.org sources response */
public class FullSource {
  private String id;
  private String name;
  private String description;
  private String url;
  private String category;
  private String language;
  private String country;

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getUrl() {
    return url;
  }

  public String getCategory() {
    return category;
  }

  public String getLanguage() {
    return language;
  }

  public String getCountry() {
    return country;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public void setCountry(String country) {
    this.country = country;
  }
}
