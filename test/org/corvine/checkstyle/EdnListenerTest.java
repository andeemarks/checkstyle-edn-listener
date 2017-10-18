package org.corvine.checkstyle;

import static org.junit.Assert.assertEquals;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class EdnListenerTest {
    private EdnListener listener;
    private ByteArrayOutputStream stub;
    private PrintWriter stubWriter;

    @Before
    public void setup() {
        listener = new EdnListener();
        stub = new ByteArrayOutputStream();
        stubWriter = new PrintWriter(stub);
    }
    @Test
    public void shouldEmitAllAuditEventFieldswithDefaultNamesInEdn() {
        AuditEvent event = new AuditEvent("src", "file", new LocalizedMessage(23, 45, "", "", null, "module-id", org.corvine.checkstyle.EdnListener.class, "Hello"));
        listener.printEvent(stubWriter, event);
        stubWriter.flush();
        assertEquals("{:source-file \"file\" :line 23 :column 45 :severity \"error\" :message \"Hello\" :source \"org.corvine.checkstyle.EdnListener\"}", stub.toString().trim());
    }

    @Test
    public void shouldOnlyIncludeFieldsWhenSpecified() {
        listener.setFields(Arrays.asList("source-file", "message", "line"));
        AuditEvent event = new AuditEvent("src", "file", new LocalizedMessage(23, 45, "", "", null, "module-id", org.corvine.checkstyle.EdnListener.class, "Hello"));
        listener.printEvent(stubWriter, event);
        stubWriter.flush();
        assertEquals("{:source-file \"file\" :line 23 :message \"Hello\"}", stub.toString().trim());
    }
}