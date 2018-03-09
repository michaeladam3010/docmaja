package com.docmala.server;

public class JsonRpcError {

    public static class Error {
        public final int code;
        public final String message;

        public Error(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    public final String jsonrpc = "2.0";
    public final Error error;

    public JsonRpcError(Error error) {
        this.error = error;
    }
}
