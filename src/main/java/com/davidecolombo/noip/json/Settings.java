package com.davidecolombo.noip.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"username",
	"password",
	"hostname",
	"useragent",
	"responses"
})
public class Settings {

	@JsonProperty("username") private String username;
	@JsonProperty("password") private String password;
	@JsonProperty("hostname") private String hostname;
	@JsonProperty("useragent") private String useragent;
	@JsonProperty("responses") private List<NoipResponse> responses;
}
