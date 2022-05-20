package space.davidecolombo.noip.noip;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import retrofit2.mock.Calls;
import space.davidecolombo.noip.TestUtils;

import java.io.IOException;

@Slf4j
class NoIpUpdaterTest {

    @Test
    void shouldUpdateNoIpAddress() throws IOException {

        String status = "good";
        int exitCode = 0;

        try (MockedStatic<INoIpApi> mockedApi = Mockito.mockStatic(INoIpApi.class)) {

            INoIpApi api = Mockito.mock(INoIpApi.class);
            Mockito.when(api.update(
                    Mockito.anyString(), // hostname
                    Mockito.anyString() // ip
            )).thenReturn(Calls.response(
                    String.format("%s %s", status, TestUtils.LOOPBACK_ADDRESS)
            ));

            mockedApi.when(() -> INoIpApi.build(
                    Mockito.anyString(), // username
                    Mockito.anyString(), // password
                    Mockito.anyString() // user-agent
            )).thenReturn(api);

            Assertions.assertEquals(exitCode,
                    NoIpUpdater.update(
                            TestUtils.createMockedNoIpSettings(),
                            TestUtils.LOOPBACK_ADDRESS));
        }
    }

    @Test
    void shouldFailOnBadAuth() throws IOException {

        String status = "badauth";
        int exitCode = 3;

        try (MockedStatic<INoIpApi> mockedApi = Mockito.mockStatic(INoIpApi.class)) {

            INoIpApi api = Mockito.mock(INoIpApi.class);
            Mockito.when(api.update(
                    Mockito.anyString(), // hostname
                    Mockito.anyString() // ip
            )).thenReturn(Calls.response(status));

            mockedApi.when(() -> INoIpApi.build(
                    Mockito.anyString(), // username
                    Mockito.anyString(), // password
                    Mockito.anyString() // user-agent
            )).thenReturn(api);

            Assertions.assertEquals(exitCode,
                    NoIpUpdater.update(
                            TestUtils.createMockedNoIpSettings(),
                            TestUtils.LOOPBACK_ADDRESS));
        }
    }

    @Test
    void shouldThrowRuntimeExceptionOnEmptyResponse() {
        try (MockedStatic<INoIpApi> mockedApi = Mockito.mockStatic(INoIpApi.class)) {

            INoIpApi api = Mockito.mock(INoIpApi.class);
            Mockito.when(api.update(
                    Mockito.anyString(), // hostname
                    Mockito.anyString() // ip
            )).thenReturn(Calls.response(StringUtils.EMPTY));

            mockedApi.when(() -> INoIpApi.build(
                    Mockito.anyString(), // username
                    Mockito.anyString(), // password
                    Mockito.anyString() // user-agent
            )).thenReturn(api);

            RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                    () -> NoIpUpdater.update(
                            TestUtils.createMockedNoIpSettings(),
                            TestUtils.LOOPBACK_ADDRESS));

            Assertions.assertEquals("No-IP response is empty!",
                    exception.getCause().getMessage());
        }
    }

    @Test
    void shouldReturnUnknownResponseExitCode() throws IOException {
        int exitCode = NoIpUpdater.ERROR_RETURN_CODE;
        try (MockedStatic<INoIpApi> mockedApi = Mockito.mockStatic(INoIpApi.class)) {

            INoIpApi api = Mockito.mock(INoIpApi.class);
            Mockito.when(api.update(
                    Mockito.anyString(), // hostname
                    Mockito.anyString() // ip
            )).thenReturn(Calls.response("this is not a response"));

            mockedApi.when(() -> INoIpApi.build(
                    Mockito.anyString(), // username
                    Mockito.anyString(), // password
                    Mockito.anyString() // user-agent
            )).thenReturn(api);

            Assertions.assertEquals(exitCode,
                    NoIpUpdater.update(
                            TestUtils.createMockedNoIpSettings(),
                            TestUtils.LOOPBACK_ADDRESS));
        }
    }

    @Test
    void shouldThrowIllegalArgumentException() {
        NoIpSettings noIpSettings = new NoIpSettings();
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> NoIpUpdater.update(noIpSettings, "this is not an ip"));
        Assertions.assertEquals(IllegalArgumentException.class,
                exception.getCause().getClass());
    }

    @Test
    void shouldThrowNullPointerException() {
        Assertions.assertEquals(NullPointerException.class,
                Assertions.assertThrows(RuntimeException.class,
                        () -> NoIpUpdater.update(new NoIpSettings(), null)
                ).getCause().getClass());

        Assertions.assertEquals(NullPointerException.class,
                Assertions.assertThrows(RuntimeException.class,
                        () -> NoIpUpdater.update(null, "127.0.0.1")
                ).getCause().getClass());
    }
}