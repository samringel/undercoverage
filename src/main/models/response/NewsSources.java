package main.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** Model for list of sources given by newsapi.org */
@JsonIgnoreProperties(value={"status"})
public class NewsSources {
  private List<FullSource> sources;

  public List<FullSource> getSources() {
    return sources;
  }

  public void setSources(List<FullSource> sources) {
    this.sources = sources;
  }
}
