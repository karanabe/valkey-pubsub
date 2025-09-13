package com.example.valkey.cli;

final class ExceptionReporter {

    private ExceptionReporter() {}

    static void print(Throwable t) {
        Throwable root = rootCause(t);
        StackTraceElement[] trace = root.getStackTrace();
        String place;
        if (trace != null && trace.length > 0) {
            StackTraceElement e = trace[0];
            place = e.getClassName() + "." + e.getMethodName() +
                    "(" + e.getFileName() + ":" + e.getLineNumber() + ")";
        } else {
            place = "unknown";
        }
        String message = root.getClass().getSimpleName() +
                (root.getMessage() != null ? ": " + root.getMessage() : "");
        System.err.println(message + " at " + place);
    }

    private static Throwable rootCause(Throwable t) {
        Throwable r = t;
        while (r.getCause() != null) {
            r = r.getCause();
        }
        return r;
    }
}
