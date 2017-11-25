package com.docmala.server;

import com.docmala.parser.ISourceProvider;
import com.docmala.parser.LocalFileSourceProvider;
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
import java.nio.file.Paths;
import java.util.Base64;

public class Server extends WebSocketServer {
    public Server(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Any request = JsonIterator.deserialize(message);

        try {

            switch( request.toString("method" ) ) {
            case "render":
                render(conn, request);
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

    }

    @Override
    public void onStart() {

    }

    void render(WebSocket conn, Any data) throws IOException {
        RemoteSourceProvider sourceProvider = new RemoteSourceProvider();
        String base64 = data.toString("params", "data" );
        String fileName = data.toString("params", "filename");
        sourceProvider.setBaseContent(fileName, new String(Base64.getDecoder().decode(base64)) );
        Parser p = new Parser();
        p.parse(sourceProvider, fileName);
        Html htmlOutput = new Html();
        Html.HtmlDocument doc = htmlOutput.generate(p.document());

        RenderResult result = new RenderResult(doc.body().toString(), data.toInt("id"));
        String serialize = JsonStream.serialize(result);
        conn.send(serialize);
    }

    static class RenderResult {
        public final String jsonrpc = "2.0";
        public final String result;
        public final int id;

        public RenderResult(String result, int id) {
            this.result = result;
            this.id = id;
        }
    }

}
