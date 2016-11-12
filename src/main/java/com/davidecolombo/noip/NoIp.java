package com.davidecolombo.noip;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.davidecolombo.noip.json.Ipify;
import com.davidecolombo.noip.json.NoIpResponse;
import com.davidecolombo.noip.json.Settings;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;

public class NoIp implements Function<Settings, Integer> {

	private static Logger logger = LoggerFactory.getLogger(NoIp.class);

	/*
	 * IPIFY is a simple public IP address API.
	 */
	private static final String IPIFY_JSON = "https://api.ipify.org/?format=json";

	/*
	 * Updates are performed by making an HTTP request to the following URL.
	 */
	private static final String NOIP_URL = "http://dynupdate.no-ip.com/nic/update?hostname=%s&myip=%s";

	/*
	 * When making an update it is important to include an HTTP User-Agent to
	 * help No-IP identify different clients that access the system. Clients
	 * that do not supply a User-Agent risk being blocked from the system.
	 */
	// private static final String USER_AGENT = "NameOfUpdateProgram/VersionNumber maintainercontact@domain.com";

	/*
	 * Setup a response for any unknown status.
	 */
	private static final NoIpResponse RESPONSE_UNKNOWN = new NoIpResponse()
			.setStatus("unknown")
			.setDescription("Unknown response has been received, please review No-IP API.")
			.setSuccessful(false)
			.setExitcode(Integer.MAX_VALUE);

	public static final int FATAL_ERROR = -1;

	/**
	 * Automatically updates the DNS at No-IP whenever it changes.
	 * 
	 * @param settings
	 * @return A No-IP {@link NoIpResponse} instance.
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private Integer update(Settings settings) throws JsonParseException, JsonMappingException, IOException {
		/*
		 * Get IP address from IPIFY and append to settings.
		 */
		Ipify ipify = new ObjectMapper().readValue(new URL(IPIFY_JSON), Ipify.class);

		/*
		 * Build No-IP request URL and Authorization.
		 */
		String url = String.format(NOIP_URL, settings.getHostname(), ipify.getIp());
		String userPass = settings.getUsername() + ":" + settings.getPassword();
		String basicAuth = "Basic " + Base64.getEncoder().encodeToString(userPass.getBytes("utf-8"));

		/*
		 * Setup/Open HttpURLConnection.
		 */
		HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
		httpURLConnection.setRequestMethod("GET");
		httpURLConnection.setRequestProperty("User-Agent", settings.getUseragent());
		httpURLConnection.setRequestProperty("Authorization", basicAuth);
		int responseCode = httpURLConnection.getResponseCode();
		logger.debug("Response Code : " + responseCode);

		/*
		 * Fetch No-IP response string.
		 */
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
		StringBuffer stringBuffer = new StringBuffer();
		String inputLine;
		while ((inputLine = bufferedReader.readLine()) != null) {
			stringBuffer.append(inputLine);
		}
		bufferedReader.close();
		String responseStr = stringBuffer.toString().trim();
		logger.debug("No-IP response: " + responseStr);

		/*
		 * Match No-IP string with known responses.
		 */
		String responseStatus = responseStr.split(" ")[0];
		NoIpResponse response = settings.getResponses().parallelStream()
				.filter(item -> responseStatus.equals(item.getStatus())).findAny().orElse(RESPONSE_UNKNOWN);
		logger.debug(response.getDescription());
		return response.getExitcode();
	}

	@Override
	public Integer apply(@NonNull Settings settings) {
		try {
			return update(settings);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return FATAL_ERROR;
	}

	/**
	 * Parse JSON settings from fileName.
	 * 
	 * @param fileName
	 * @return
	 */
	public static Integer applyFromFile(@NonNull String fileName) {
		try {
			return new NoIp().apply(new ObjectMapper().readValue(new File(fileName), Settings.class));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return FATAL_ERROR;
	}
}
