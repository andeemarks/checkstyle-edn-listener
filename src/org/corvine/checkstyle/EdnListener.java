package org.corvine.checkstyle;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.AutomaticBean;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;

public class EdnListener extends AutomaticBean implements AuditListener {
    private PrintWriter mWriter = new PrintWriter(System.out);
    private boolean mCloseOut = false;
    private int mTotalErrors;
    private int mErrors;

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
        printEvent(aEvt);
        if (SeverityLevel.ERROR.equals(aEvt.getSeverityLevel())) {
            mErrors++;
            mTotalErrors++;
        }
    }

    public void addException(AuditEvent aEvt, Throwable aThrowable) {
        printEvent(aEvt);
        aThrowable.printStackTrace(System.out);
        mErrors++;
        mTotalErrors++;
    }

    private String quote(Object eventItem) {
        return "\"" + eventItem + "\"";
    }
    
    private void printEvent(AuditEvent aEvt) {
        mWriter.println("{:source-file " + quote(aEvt.getFileName())
                      + " :line " + aEvt.getLine()
                      + " :column " + aEvt.getColumn()
                      + " :severity " + quote(aEvt.getSeverityLevel())
                      + " :message " + quote(aEvt.getMessage())
                      + " :source " + quote(aEvt.getSourceName()) + "}");
    }
}
