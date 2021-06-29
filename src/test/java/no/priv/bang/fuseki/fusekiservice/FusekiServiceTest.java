package no.priv.bang.fuseki.fusekiservice;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class FusekiServiceTest {
    private Appender mockAppender;
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;
    private Logger rootLogger;

    @Before
    public void setup() {
        mockAppender = mock(Appender.class);
        when(mockAppender.getName()).thenReturn("MockAppender");
        captorLoggingEvent = ArgumentCaptor.forClass(LoggingEvent.class);
        rootLogger = Logger.getRootLogger();
    }

    @Test
    public void test() throws InterruptedException {
        FusekiService endpoint = new FusekiService();
        int linenumberOfLogLineWithDataDirectory = 3;
        int linenumberOfLogLineWithPortNumber = 7;
        rootLogger.addAppender(mockAppender); // Appender must be added here because Jena init in the FusekiService constructor clears the appender list
        endpoint.startDaemon();
        verify(mockAppender, times(linenumberOfLogLineWithPortNumber)).doAppend(captorLoggingEvent.capture());
        List<LoggingEvent> allLogEventsSoFar = captorLoggingEvent.getAllValues();
        assertThat(allLogEventsSoFar.get(linenumberOfLogLineWithDataDirectory - 1).getRenderedMessage()).endsWith("target\\test");
        assertThat(allLogEventsSoFar.get(linenumberOfLogLineWithPortNumber - 1).getRenderedMessage()).endsWith("port 3030");
        Thread.sleep(10000);
        endpoint.stopDaemon();
    }

}
