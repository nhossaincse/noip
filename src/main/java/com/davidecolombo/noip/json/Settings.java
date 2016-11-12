package com.davidecolombo.noip.json;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Settings {

	private String username, password, hostname, useragent;

	private List<NoIpResponse> responses;
}
