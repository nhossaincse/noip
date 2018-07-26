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
import space.davidecolombo.noip.Settings;
import space.davidecolombo.noip.ipify.IpifyResponse;

@Slf4j
public class NoipUpdater implements BiFunction<Settings, String, Integer> {

	/*
	 * IPIFY is a simple public IP address API
	 */
	private static final String ipifyUrl = "https://api.ipify.org/?format=json";

	/*
	 * Setup a response for unknown status
	 */
	private static final NoipResponse unknownResponse = new NoipResponse()
			.setStatus("unknown")
			.setDescription("Unknown response, please review No-IP API.")
			.setSuccessful(false)
			.setExitCode(Integer.MAX_VALUE);

	private NoipUpdater() {}

	/**
	 * Automatically updates the DNS at No-IP whenever it changes
	 * 
	 * @param settings
	 * @param ipifyResponse
	 * @return An exit code
	 * @throws IOException
	 */
	private Integer doApply(@NonNull Settings settings, @NonNull String ip) throws IOException {

		/*
		 * Build API and synchronously update No-IP
		 */
		Response<String> response = INoipApi.build(
				settings.getUserName(),
				settings.getPassword(),
				settings.getUserAgent()
			).update(settings.getHostName(), ip).execute();

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
		return settings.getResponses().parallelStream()
				.filter(item -> status.equals(item.getStatus())).findAny()
				.orElse(unknownResponse)
				.getExitCode();
	}

	@Override
	public Integer apply(Settings settings, String ip) {
		try {
			return doApply(settings, ip);
		} catch (Exception e) {
			throw new RuntimeException(e.getLocalizedMessage(), e);
		}
	}

	public static Integer updateByIpify(@NonNull String fileName)
			throws JsonParseException, JsonMappingException, MalformedURLException, IOException {
		
		/*
		 * Build settings
		 */
		ObjectMapper objectMapper = new ObjectMapper();
		Settings settings = objectMapper.readValue(new File(fileName), Settings.class);
		objectMapper.readerForUpdating(settings).readValue(NoipUpdater.class.getClassLoader().getResource("responses.json"));

		/*
		 * Get Ipify response
		 */
		IpifyResponse ipifyResponse = new ObjectMapper().readValue(new URL(ipifyUrl), IpifyResponse.class);

		/*
		 * Update DNS at No-IP
		 */
		return new NoipUpdater().apply(settings, ipifyResponse.getIp());
	}
}
