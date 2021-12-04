package space.davidecolombo.noip.noip;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Response;
import space.davidecolombo.noip.ipify.IpifyResponse;
import space.davidecolombo.noip.utils.IpUtils;
import space.davidecolombo.noip.utils.ObjectMapperUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.function.BiFunction;

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

    NoIpUpdater() {
    }

    /**
     * Automatically updates the DNS at No-IP whenever it changes
     *
     * @param noIpSettings No-IP settings
     * @param ip           IP address that must be set
     * @return return code mapped to the received response
     * @throws IOException I/O exception has occurred
     */
    private Integer doApply(@NonNull NoIpSettings noIpSettings, @NonNull String ip) throws IOException {

        if (!IpUtils.isIPv4Address(ip)) {
            throw new IllegalArgumentException("IP '" + ip + "' isn't a valid IPv4 address!");
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
        String message = null;
        if (response.isSuccessful()) {
            message = response.body();
        } else if (response.errorBody() != null) {
            message = response.errorBody().string();
        }

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

    public static Integer updateFromIpify(@NonNull String fileName) throws IOException {

        /*
         * Build settings
         */
        ObjectMapper objectMapper = ObjectMapperUtils.createObjectMapper();
        NoIpSettings noIpSettings = objectMapper.readValue(new File(fileName), NoIpSettings.class);
        objectMapper.readerForUpdating(noIpSettings).readValue(NoIpUpdater.class.getClassLoader().getResource(RESPONSES_FILE));

        /*
         * Get Ipify response
         */
        IpifyResponse ipifyResponse = ObjectMapperUtils
                .createObjectMapper()
                .readValue(new URL(IPIFY_URL), IpifyResponse.class);
        logger.info(ipifyResponse.toString());

        /*
         * Update DNS at No-IP
         */
        return new NoIpUpdater().apply(noIpSettings, ipifyResponse.getIp());
    }
}