package com.docmala.server;

import com.docmala.Error;
import com.docmala.parser.Parser;
import com.docmala.plugins.ouput.Html;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends WebSocketServer {
    Map<WebSocket, Requester> requesters = new HashMap<>();
    ExecutorService executor = Executors.newFixedThreadPool(4);
    public Server(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.printf("Connected\n");
        requesters.put(conn, new Requester(conn));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.printf("Disconnected\n");
        requesters.remove(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        executor.submit(() -> {
            System.out.printf("Received message %s\n", message);
            Any request = JsonIterator.deserialize(message);

            if (request.keys().contains("result") || request.keys().contains("error")) {
                requesters.get(conn).receive(request);
            } else {
                try {

                    switch (request.toString("method")) {
                        case "render":
                            render(conn, request);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

    }

    @Override
    public void onStart() {

    }

    void render(WebSocket conn, Any data) {
        RenderResult result;
        try {
            RemoteSourceProvider sourceProvider = new RemoteSourceProvider(requesters.get(conn));
            String fileName = data.toString("params", "filename");
            Parser p = new Parser();
            p.parse(sourceProvider, fileName);

            ArrayDeque<RenderError> errors = null;
            if (!p.errors().isEmpty()) {
                errors = new ArrayDeque<>();
                for (Error error : p.errors()) {
                    errors.push(new RenderError(error));
                }
            }

            Html htmlOutput = new Html();
            Html.HtmlDocument doc = htmlOutput.generate(p.document());

            result = new RenderResult(doc, errors, data.toInt("id"));
        } catch (Exception e) {
            Html.HtmlDocument doc = new Html.HtmlDocument();
            doc.body().append("<h1>Exception occured:").append(e.toString()).append("</h1>");
            for(StackTraceElement element : e.getStackTrace() ) {
                doc.body().append(element.toString()).append("</br>");
            }
            result = new RenderResult(doc, new ArrayDeque<>(), data.toInt("id"));
        }
        String serialize = JsonStream.serialize(result);
        System.out.printf("Sending rendering result %s\n", serialize);
        conn.send(serialize);
    }

    static class RenderError {
        public final int line;
        public final int column;
        public final String file;
        public final String message;

        RenderError(Error error) {
            line = error.position().line();
            column = error.position().column();
            file = error.position().fileName();
            message = error.message();
        }
    }

    static class RenderErrors extends JsonRpcError.Error {
        public final ArrayDeque<RenderError> data;

        public RenderErrors(ArrayDeque<Error> errors) {
            super(-231, "Errors during rendering.");
            data = new ArrayDeque<RenderError>();
            for (Error error : errors) {
                data.push(new RenderError(error));
            }
        }
    }

    static class RenderResult {
        public final String jsonrpc = "2.0";
        public final Result result;
        public final int id;
        public RenderResult(com.docmala.plugins.ouput.Html.HtmlDocument html, ArrayDeque<RenderError> errors, int id) {
            this.result = new Result(html.head().toString(), html.body().toString(), errors);
            this.id = id;
        }

        static class Result {
            public final String head;
            public final String body;
            public final ArrayDeque<RenderError> errors;

            Result(String head, String body, ArrayDeque<RenderError> errors) {
                this.head = head;
                this.body = body;
                this.errors = errors;
            }
        }
    }

}
