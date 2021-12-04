package space.davidecolombo.noip;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import space.davidecolombo.noip.noip.NoIpSettings;

import java.io.File;
import java.io.IOException;

@UtilityClass
public class TestUtils {

    public static final String LOOPBACK_ADDRESS = "127.0.0.1";

    public static NoIpSettings createMockedNoIpSettings() throws IOException {
        NoIpSettings settings = new NoIpSettings();
        settings.setUserName("username");
        settings.setPassword("password");
        settings.setHostName("hostname");
        settings.setUserAgent("user-agent");
        new ObjectMapper()
                .readerForUpdating(settings)
                .readValue(new File(System.getProperty("user.dir")
                        + "/src/main/resources/responses.json"));
        return settings;
    }
}