package org.corvine.checkstyle;

import static org.junit.Assert.assertEquals;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

public class EdnListenerTest {
    @Test
    public void shouldEmitAllAuditEventFieldswithDefaultNamesInEdn() {
        EdnListener listener = new EdnListener();
        AuditEvent event = new AuditEvent("src", "file");
        ByteArrayOutputStream stub = new ByteArrayOutputStream();
        PrintWriter stubWriter = new PrintWriter(stub);
        listener.printEvent(stubWriter, event);
        stubWriter.flush();
        assertEquals("{:source-file \"file\" :severity \"info\"}", stub.toString().trim());
    }
}
