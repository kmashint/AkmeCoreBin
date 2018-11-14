package akme.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class ExceptionUtil {

	public static String getStackTrace(Throwable ex) {
		StringWriter sw = new StringWriter(1024);
		ex.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
	public static String getShortStackTraceAndCause(Throwable ex) {
		StringWriter sw = new StringWriter(1024);
		PrintWriter pw = new PrintWriter(sw);
		StackTraceElement[] trace = ex.getStackTrace();
		Throwable cause = ex.getCause();
		// Find the root cause.
		if (cause != null) while (cause.getCause() != null) cause = cause.getCause();
		StackTraceElement[] causeTrace = cause != null ? cause.getStackTrace() : null;
		pw.println(ex.toString());
		if (trace.length > 0) pw.println("\tat " + trace[0].toString());
		if (cause != null) {
			pw.println("Caused by: " + cause.toString());
			if (causeTrace.length > 0) pw.println("\tat " + causeTrace[0].toString());
		}
		return sw.toString();
	}
	
}
