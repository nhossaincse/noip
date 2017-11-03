package com.davidecolombo.noip;

import java.util.List;

import com.davidecolombo.noip.noip.NoipResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"userName",
	"password",
	"hostName",
	"userAgent",
	"responses"
})
public class Settings {

	@JsonProperty("userName") private String userName;
	@JsonProperty("password") private String password;
	@JsonProperty("hostName") private String hostName;
	@JsonProperty("userAgent") private String userAgent;
	@JsonProperty("responses") private List<NoipResponse> responses;
}
