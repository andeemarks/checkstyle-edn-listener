package org.corvine.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.AutomaticBean;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

public class BatchingEdnListener extends AutomaticBean implements AuditListener {
    private PrintWriter mWriter = new PrintWriter(System.out);
    private boolean mCloseOut = false;
    private int mTotalErrors;
    private int mErrors;
    private Hashtable<String, List<AuditEvent>> errorHistory = new Hashtable<>();

    public void setFile(String aFileName) throws FileNotFoundException {
        final OutputStream out = new FileOutputStream(aFileName);
        mWriter = new PrintWriter(out);
        mCloseOut = true;
    }

    public void auditStarted(AuditEvent aEvt) {
        mTotalErrors = 0;
    }

    public void auditFinished(AuditEvent aEvt) {
        writeHistory(errorHistory);
        mWriter.flush();
        if (mCloseOut) {
            mWriter.close();
        }
    }

    public void fileStarted(AuditEvent aEvt) {
        mErrors = 0;
    }

    public void fileFinished(AuditEvent aEvt) {
    }

    private void writeHistory(Hashtable<String, List<AuditEvent>> errorHistory) {
        Set<String> historyKeys = errorHistory.keySet();
        for (String key: historyKeys) {
            String batchedEvent = "";
            List<AuditEvent> events = errorHistory.get(key);
            batchedEvent = "{ " + formatKey(key) + formatHeader(events.get(0));
            for (AuditEvent auditEvent : events) {
                batchedEvent += formatEventDelta(auditEvent) + " ";
            }
            batchedEvent = batchedEvent.trim() + " }";
            mWriter.println(batchedEvent);
        }
    }

    private String formatKey(String key) {
        return format("key",  quote(key));
    }

    private String formatHeader(AuditEvent auditEvent) {
        return format("source-file",  quote(auditEvent.getFileName()))
                + format("line", auditEvent.getLine());
    }

    public void addError(AuditEvent aEvt) {
        String historyKey = aEvt.getFileName() + "#" + aEvt.getLine();
        if (errorHistory.containsKey(historyKey)) {
            List<AuditEvent> history = (List<AuditEvent>) errorHistory.get(historyKey);
            history.add(aEvt);

            errorHistory.put(historyKey, history);
        } else {
            errorHistory.put(historyKey, new ArrayList<AuditEvent>(Collections.singletonList(aEvt)));
        }

        if (SeverityLevel.ERROR.equals(aEvt.getSeverityLevel())) {
            mErrors++;
            mTotalErrors++;
        }
    }

    public void addException(AuditEvent aEvt, Throwable aThrowable) {
        aThrowable.printStackTrace(System.out);
        mErrors++;
        mTotalErrors++;
    }

    private String quote(Object eventItem) {
        return "\"" + eventItem + "\"";
    }

    private String format(String fieldId, Object fieldValue) { return ":" + fieldId + " " + fieldValue + " "; }

    private String formatEvent(AuditEvent aEvt) {
        return format("source-file",  quote(aEvt.getFileName()))
          + format("line", aEvt.getLine())
          + format("column", aEvt.getColumn())
          + format("severity", quote(aEvt.getSeverityLevel()))
          + format("message", quote(aEvt.getMessage()))
          + format("source", quote(aEvt.getSourceName()));
    }

    private String formatEventDelta(AuditEvent aEvt) {
        return ":" + aEvt.getSourceName() + " " + quote(aEvt.getMessage()) + " ";
    }
}
