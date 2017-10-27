package com.davidecolombo.noip;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.davidecolombo.noip.api.INoipApi;
import com.davidecolombo.noip.json.Ipify;
import com.davidecolombo.noip.json.NoipResponse;
import com.davidecolombo.noip.json.Settings;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;
import retrofit2.Response;

public class Noip implements Function<Settings, Integer> {

	private static Logger logger = LoggerFactory.getLogger(Noip.class);

	/*
	 * IPIFY is a simple public IP address API.
	 */
	private static final String IPIFY_JSON = "https://api.ipify.org/?format=json";

	/*
	 * When making an update it is important to include an HTTP User-Agent to
	 * help No-IP identify different clients that access the system. Clients
	 * that do not supply a User-Agent risk being blocked from the system.
	 */
	// private static final String USER_AGENT = "NameOfUpdateProgram/VersionNumber maintainercontact@domain.com";

	/*
	 * Setup a response for any unknown status.
	 */
	private static final NoipResponse RESPONSE_UNKNOWN = new NoipResponse()
			.setStatus("unknown")
			.setDescription("Unknown response has been received, please review No-IP API.")
			.setSuccessful(false)
			.setExitcode(Integer.MAX_VALUE);

	public static final int ERROR_CODE = -1;

	/**
	 * Automatically updates the DNS at No-IP whenever it changes.
	 * 
	 * @param settings
	 * @return A No-IP exit code.
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * 
	 */
	private Integer update(@NonNull Settings settings) throws
			JsonParseException,
			JsonMappingException,
			MalformedURLException,
			IOException,
			InterruptedException,
			ExecutionException {

		/*
		 * Get IP address from IPIFY.
		 */
		Ipify ipify = new ObjectMapper().readValue(new URL(IPIFY_JSON), Ipify.class);

		/*
		 * Build API and synchronously update No-IP.
		 */
		Response<String> response = INoipApi.build(
				settings.getUsername(),
				settings.getPassword(),
				settings.getUseragent()
			).update(
				settings.getHostname(),
				ipify.getIp()
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
					.orElse(RESPONSE_UNKNOWN)
					.getExitcode();
		} else {
			logger.error("No-IP response is empty.");
		}
		return ERROR_CODE;
	}

	@Override
	public Integer apply(@NonNull Settings settings) {
		try {
			return update(settings);
		} catch (IOException | InterruptedException | ExecutionException e) {
			logger.error(e.getMessage(), e);
		}
		return ERROR_CODE;
	}

	/**
	 * Parse JSON settings from fileName.
	 * 
	 * @param fileName
	 * @return A No-IP exit code.
	 */
	public static Integer applyFromFile(@NonNull String fileName) {
		try {
			return new Noip().apply(new ObjectMapper().readValue(new File(fileName), Settings.class));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return ERROR_CODE;
	}
}
