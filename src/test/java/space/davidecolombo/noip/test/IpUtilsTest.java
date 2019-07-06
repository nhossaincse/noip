package space.davidecolombo.noip.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import space.davidecolombo.noip.utils.IpUtils;

public class IpUtilsTest {

	private static final String FILENAME = "src/test/resources/isIPv4Address.json";

	@Test
	public void isIPv4Address() throws JsonParseException, JsonMappingException, IOException {

		Map<String, Boolean> map = new ObjectMapper().readValue(new File(FILENAME),
				new TypeReference<Map<String, Boolean>>() {
				});

		for (Entry<String, Boolean> entry : map.entrySet()) {
			String ip = entry.getKey();
			boolean expected = entry.getValue();
			boolean actual = IpUtils.isIPv4Address(ip);
			assertEquals(expected, actual);
		}
	}
}
