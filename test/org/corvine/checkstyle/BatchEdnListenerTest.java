package org.corvine.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import static org.junit.Assert.assertEquals;

public class BatchEdnListenerTest {
    private BatchingEdnListener listener;
    private ByteArrayOutputStream stub;
    private PrintWriter stubWriter;

    @Before
    public void setup() {
        listener = new BatchingEdnListener();
        stub = new ByteArrayOutputStream();
        stubWriter = new PrintWriter(stub);
        listener.mWriter = stubWriter;
    }

    @Test
    public void shouldRemoveDuplicateErrorsForSameLineAndRetainFirstOnly() {
        AuditEvent event1 = new AuditEvent("src", "file1", new LocalizedMessage(23, 45, "", "", null, "module-id", EdnListener.class, "First"));
        AuditEvent event2 = new AuditEvent("src", "file1", new LocalizedMessage(23, 45, "", "", null, "module-id", EdnListener.class, "Second"));

        listener.addError(event1);
        listener.addError(event2);

        listener.auditFinished(event1);
        stubWriter.flush();

        assertEquals("{ :key \"file1#23\" :source-file \"file1\" :line 23 :org.corvine.checkstyle.EdnListener \"First\" }", stub.toString().trim());
    }

    @Test
    public void shouldBatchAllEventsForTheSameFileAndLineNumberWhenAuditFinished() {
        AuditEvent event1 = new AuditEvent("src", "file1", new LocalizedMessage(23, 45, "", "", null, "module-id", EdnListener.class, "Hello"));
        AuditEvent event2 = new AuditEvent("src", "file1", new LocalizedMessage(23, 45, "", "", null, "module-id", EdnListener.class, "Hello"));
        AuditEvent event3 = new AuditEvent("src", "file1", new LocalizedMessage(24, 45, "", "", null, "module-id", EdnListener.class, "Hello"));
        AuditEvent event4 = new AuditEvent("src", "file2", new LocalizedMessage(23, 45, "", "", null, "module-id", EdnListener.class, "Hello"));
        AuditEvent event5 = new AuditEvent("src", "file2", new LocalizedMessage(24, 45, "", "", null, "module-id", EdnListener.class, "Hello"));

        listener.addError(event1);
        listener.addError(event2);
        listener.addError(event3);
        listener.addError(event4);
        listener.addError(event5);

        listener.auditFinished(event1);
        stubWriter.flush();

        assertEquals("{ :key \"file1#24\" :source-file \"file1\" :line 24 :org.corvine.checkstyle.EdnListener \"Hello\" }\n" +
                                "{ :key \"file1#23\" :source-file \"file1\" :line 23 :org.corvine.checkstyle.EdnListener \"Hello\" }\n" +
                                "{ :key \"file2#24\" :source-file \"file2\" :line 24 :org.corvine.checkstyle.EdnListener \"Hello\" }\n" +
                                "{ :key \"file2#23\" :source-file \"file2\" :line 23 :org.corvine.checkstyle.EdnListener \"Hello\" }", stub.toString().trim());
    }
}
