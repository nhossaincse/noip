package com.davidecolombo.noip.noip;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.function.BiFunction;

import org.apache.commons.lang3.StringUtils;

import com.davidecolombo.noip.Settings;
import com.davidecolombo.noip.ipify.IpifyResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

@Slf4j
public class NoipUpdater implements BiFunction<Settings, IpifyResponse, Integer> {

	public static final int ERROR_CODE = -1;

	/*
	 * IPIFY is a simple public IP address API.
	 */
	private static final String ipifyUrl = "https://api.ipify.org/?format=json";

	/*
	 * Setup a response for unknown status.
	 */
	private static final NoipResponse unknownResponse = new NoipResponse()
			.setStatus("unknown")
			.setDescription("Unknown response, please review No-IP API.")
			.setSuccessful(false)
			.setExitCode(Integer.MAX_VALUE);

	/**
	 * Automatically updates the DNS at No-IP whenever it changes.
	 * 
	 * @param settings
	 * @param ipifyResponse
	 * @return An exit code.
	 * @throws IOException
	 */
	private Integer doApply(Settings settings, IpifyResponse ipifyResponse) throws IOException {

		/*
		 * Build API and synchronously update No-IP.
		 */
		Response<String> response = INoipApi.build(
				settings.getUserName(),
				settings.getPassword(),
				settings.getUserAgent()
			).update(
				settings.getHostName(),
				ipifyResponse.getIp()
			).execute();

		logger.debug("HTTP status code: " + response.code());
		logger.debug("HTTP status message: " + response.message());

		/*
		 * Process No-IP response.
		 */
		String message = response.isSuccessful() ?
				response.body() : response.errorBody().string();

		if (StringUtils.isNotEmpty(message)) {
			message = message.trim();
			logger.debug("No-IP response: " + message);

			/*
			 * Match No-IP string with known responses.
			 */
			String status = message.split(" ")[0];
			return settings.getResponses().parallelStream()
					.filter(item -> status.equals(item.getStatus()))
					.findAny()
					.orElse(unknownResponse)
					.getExitCode();
		} else {
			logger.error("No-IP response is empty!");
		}
		return ERROR_CODE;
	}
	
	@Override
	public Integer apply(@NonNull Settings settings, @NonNull IpifyResponse ipifyResponse) {
		try {
			return doApply(settings, ipifyResponse);
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return ERROR_CODE;
	}

	public static Integer updateByIpify(@NonNull File settings) {
		try {
			return new NoipUpdater().apply(
					new ObjectMapper().readValue(settings, Settings.class),
					new ObjectMapper().readValue(new URL(ipifyUrl), IpifyResponse.class)
				);
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return ERROR_CODE;
	}
}
