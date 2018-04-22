package com.docmala.server;

import com.jsoniter.ValueType;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import org.java_websocket.WebSocket;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Requester {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);
    WebSocket connection;
    int idCounter = 0;
    Map<Integer, ResultHandler> openRequests = new HashMap<>();

    public Requester(WebSocket connection) {
        this.connection = connection;
    }

    public void receive(Any message) {
        int id = message.toInt("id");
        synchronized (openRequests) {
            ResultHandler handler = null;
            synchronized (openRequests) {
                if (openRequests.containsKey(id)) {
                    handler = openRequests.get(id);
                    openRequests.remove(id);
                }
            }

            if (handler != null) {
                if (message.get("result").valueType() != ValueType.INVALID) {
                    handler.finished(false, message.get("result"));
                } else {
                    handler.finished(true, message.get("error"));
                }
            } else {
                System.out.printf("Result received but handler was not found.\n");
            }
        }

    }

    public void sendRequest(Request request, ResultHandler resultHandler) {
        idCounter++;
        request.id = idCounter;
        openRequests.put(idCounter, resultHandler);
        // Todo: currently the timeout timer is used to remove the request from the request queue.
        //         Maybe it would be better to store the future and cancel it when the result is received

        final int capIdCounter = idCounter;
        scheduler.schedule(() -> {
            ResultHandler handler = null;
            synchronized (openRequests) {
                if (openRequests.containsKey(capIdCounter)) {
                    handler = openRequests.get(capIdCounter);
                    openRequests.remove(capIdCounter);
                }
            }

            if (handler != null) {
                handler.finished(true, null);
                System.out.printf("Timeout occurred for request:" + String.valueOf(capIdCounter) + "\n");
            }
        }, 1000, TimeUnit.MILLISECONDS);
        String s = JsonStream.serialize(request);
        System.out.printf("Sending message %s\n", s);

        connection.send(s);
    }

    public interface ResultHandler {
        void finished(boolean isError, Any data);
    }

    static public class Request {
        final public String jsonrpc = "2.0";
        final public String method;
        public int id;

        public Request(String method) {
            this.method = method;
        }
    }

    public static abstract class SynchronizedResultHandler implements ResultHandler {
        boolean isFinished = false;

        void setFinished() {
            synchronized (this) {
                isFinished = true;
                this.notifyAll();
            }
        }

        void waitForFinished() {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
