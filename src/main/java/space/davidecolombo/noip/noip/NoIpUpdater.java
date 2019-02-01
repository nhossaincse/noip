package space.davidecolombo.noip.noip;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.BiFunction;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;
import space.davidecolombo.noip.ipify.IpifyResponse;
import space.davidecolombo.noip.utils.IpUtils;

@Slf4j
public class NoIpUpdater implements BiFunction<NoIpSettings, String, Integer> {

	public static final int ERROR_RETURN_CODE = -1;

	/*
	 * IPIFY is a simple public IP address API
	 */
	private static final String IPIFY_URL = "https://api.ipify.org/?format=json";

	/*
	 * Used to load No-IP responses
	 */
	private static final String RESPONSES_FILE = "responses.json";

	/*
	 * Setup a response for unknown status
	 */
	private static final NoIpResponse UNKNOWN_RESPONSE = NoIpResponse.builder()
			.successful(false)
			.exitCode(ERROR_RETURN_CODE)
		.build();

	private NoIpUpdater() {}

	/**
	 * Automatically updates the DNS at No-IP whenever it changes
	 * 
	 * @param noIpSettings
	 * @param ip
	 * @return
	 * @throws IOException
	 */
	private Integer doApply(@NonNull NoIpSettings noIpSettings, @NonNull String ip) throws IOException {

		if (!IpUtils.isIPv4Address(ip)) {
			throw new RuntimeException("IP '" + ip + "' isn't a valid IPv4 address!");
		}
		
		/*
		 * Build API and synchronously update No-IP
		 */
		Response<String> response = INoIpApi.build(
				noIpSettings.getUserName(),
				noIpSettings.getPassword(),
				noIpSettings.getUserAgent()
			).update(
				noIpSettings.getHostName(), ip
			).execute();

		logger.info("HTTP status code: " + response.code());
		logger.info("HTTP status message: " + response.message());

		/*
		 * Process No-IP response
		 */
		String message = response.isSuccessful() ?
				response.body() : response.errorBody().string();

		if (StringUtils.isEmpty(message)) {
			throw new RuntimeException("No-IP response is empty!");
		}

		message = message.trim();
		logger.info("No-IP response: " + message);

		/*
		 * Match No-IP string with known responses
		 */
		String status = message.split(" ")[0];
		return noIpSettings.getResponses().parallelStream()
				.filter(item -> status.equals(item.getStatus())).findAny()
				.orElse(UNKNOWN_RESPONSE)
				.getExitCode();
	}

	@Override
	public Integer apply(NoIpSettings noIpSettings, String ip) {
		try {
			return doApply(noIpSettings, ip);
		} catch (Exception e) {
			throw new RuntimeException(e.getLocalizedMessage(), e);
		}
	}

	public static Integer updateFromIpify(@NonNull String fileName)
			throws JsonParseException, JsonMappingException, MalformedURLException, IOException {
		
		/*
		 * Build settings
		 */
		ObjectMapper objectMapper = new ObjectMapper();
		NoIpSettings noIpSettings = objectMapper.readValue(new File(fileName), NoIpSettings.class);
		objectMapper.readerForUpdating(noIpSettings).readValue(NoIpUpdater.class.getClassLoader().getResource(RESPONSES_FILE));

		/*
		 * Get Ipify response
		 */
		IpifyResponse ipifyResponse = new ObjectMapper().readValue(new URL(IPIFY_URL), IpifyResponse.class);
		logger.info(ipifyResponse.toString());

		/*
		 * Update DNS at No-IP
		 */
		return new NoIpUpdater().apply(noIpSettings, ipifyResponse.getIp());
	}
}
