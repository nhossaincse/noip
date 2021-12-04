package space.davidecolombo.noip;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import retrofit2.mock.Calls;
import space.davidecolombo.noip.ipify.IpifyResponse;
import space.davidecolombo.noip.noip.INoIpApi;
import space.davidecolombo.noip.noip.NoIpSettings;
import space.davidecolombo.noip.utils.ObjectMapperUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Slf4j
class AppTest {

    @Test
    @ExpectSystemExitWithStatus(1)
    void testSystemExitWithStatus() throws IOException {

        try (MockedStatic<INoIpApi> mockedApi = Mockito.mockStatic(INoIpApi.class);
             MockedStatic<ObjectMapperUtils> mockedJsonUtils = Mockito.mockStatic(ObjectMapperUtils.class)) {

            INoIpApi api = Mockito.mock(INoIpApi.class);
            Mockito.when(api.update(
                    Mockito.anyString(), // hostname
                    Mockito.anyString() // ip
            )).thenReturn(Calls.response(String.format("%s %s", "nochg", TestUtils.LOOPBACK_ADDRESS)));

            mockedApi.when(() -> INoIpApi.build(
                    Mockito.anyString(), // username
                    Mockito.anyString(), // password
                    Mockito.anyString() // user-agent
            )).thenReturn(api);

            ObjectMapper objectMapper = Mockito.mock(
                    ObjectMapper.class,
                    Mockito.RETURNS_DEEP_STUBS);

            // fake settings
            Mockito.when(objectMapper.readValue(
                            Mockito.any(File.class),
                            Mockito.eq(NoIpSettings.class)))
                    .thenReturn(TestUtils.createMockedNoIpSettings());

            // fake ipify
            IpifyResponse mockedIpifyResponse = new IpifyResponse();
            mockedIpifyResponse.setIp(TestUtils.LOOPBACK_ADDRESS);
            Mockito.when(objectMapper.readValue(
                            Mockito.any(URL.class),
                            Mockito.eq(IpifyResponse.class)))
                    .thenReturn(mockedIpifyResponse);

            mockedJsonUtils
                    .when(ObjectMapperUtils::createObjectMapper)
                    .thenReturn(objectMapper);
            Assertions.assertNotNull(ObjectMapperUtils.createObjectMapper());

            App.main(new String[]{"-settings", "dummy.json"});
            // wait for system exit with exit code
        }
    }
}