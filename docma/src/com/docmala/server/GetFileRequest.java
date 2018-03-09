package com.docmala.server;

import com.jsoniter.any.Any;

import java.util.Base64;

class GetFileRequest extends Requester.Request {

    public GetFileRequest(Params params) {
        super("getFile");
        this.params = params;
    }

    public static class Params {
        final String fileName;

        public Params(String fileName) {
            this.fileName = fileName;
        }
    }
    public final Params params;

    static class Result extends Requester.SynchronizedResultHandler {
        public String content = null;

        @Override
        public void finished(boolean isError, Any data) {
            if( !isError ) {
                String base64 = data.toString("data");
                content = new String(Base64.getDecoder().decode(base64));
            }
            setFinished();
        }
    }
}
