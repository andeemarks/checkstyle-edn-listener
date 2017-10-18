package org.corvine.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.AutomaticBean;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class EdnListener extends AutomaticBean implements AuditListener {
    private PrintWriter mWriter = new PrintWriter(System.out);
    private boolean mCloseOut = false;
    private int mTotalErrors;
    private int mErrors;
    private List fieldsToRetain = Arrays.asList("source-file", "line", "column", "severity", "message", "source");

    public void setFile(String aFileName) throws FileNotFoundException {
        final OutputStream out = new FileOutputStream(aFileName);
        mWriter = new PrintWriter(out);
        mCloseOut = true;
    }

    public void auditStarted(AuditEvent aEvt) {
        mTotalErrors = 0;
    }

    public void auditFinished(AuditEvent aEvt) {
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

    public void addError(AuditEvent aEvt) {
        printEvent(mWriter, aEvt);
        if (SeverityLevel.ERROR.equals(aEvt.getSeverityLevel())) {
            mErrors++;
            mTotalErrors++;
        }
    }

    public void addException(AuditEvent aEvt, Throwable aThrowable) {
        printEvent(mWriter, aEvt);
        aThrowable.printStackTrace(System.out);
        mErrors++;
        mTotalErrors++;
    }

    private String quote(Object eventItem) {
        return "\"" + eventItem + "\"";
    }

    private String ifAllowed(String fieldId, Object fieldValue) {
        if (fieldsToRetain.contains(fieldId)) {
            return ":" + fieldId + " " + fieldValue + " ";
        }

        return "";
    }

    void printEvent(PrintWriter mWriter, AuditEvent aEvt) {
        mWriter.println("{"
                      + (ifAllowed("source-file",  quote(aEvt.getFileName()))
                      + ifAllowed("line", aEvt.getLine())
                      + ifAllowed("column", aEvt.getColumn())
                      + ifAllowed("severity", quote(aEvt.getSeverityLevel()))
                      + ifAllowed("message", quote(aEvt.getMessage()))
                      + ifAllowed("source", quote(aEvt.getSourceName()))).trim()
                      + "}");
    }

    public void setFields(List fields) {
        fieldsToRetain = fields;
    }
}
