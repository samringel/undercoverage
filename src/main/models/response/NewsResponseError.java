package main.models.response;

/** Model for newsapi.org error response */
public class NewsResponseError implements NewsResponse {
  private String status = "error";
  private String code;
  private String message;

  @Override
  public String getStatus() {
    return status;
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public void setStatus(String status) {
    this.status = status;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
