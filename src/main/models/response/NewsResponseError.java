package main.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Model for newsapi.org error response */
@JsonIgnoreProperties(value={"status"})
public class NewsResponseError {
  private String code;
  private String message;


  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
