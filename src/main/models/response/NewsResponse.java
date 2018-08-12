package main.models.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/** Model for newsapi.org response */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "status")
@JsonSubTypes({
    @Type(value = NewsResponseError.class, name = "error"),
    @Type(value = NewsResponseOk.class, name = "ok") })
public interface NewsResponse {
  @JsonIgnore
  String getStatus();
  @JsonIgnore
  void setStatus(String status);
}
