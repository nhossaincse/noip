package com.davidecolombo.noip.json;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NoIpResponse {

	private String status, description;

	private boolean successful;

	private int exitcode;
}
