package com.davidecolombo.noip.json;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Ipify {

	private String ip;
}
