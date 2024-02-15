package com.andmcadams.wikisync;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

@Slf4j
public class WikiSyncWebSocketServer extends WebSocketServer {
    public WikiSyncWebSocketServer(InetSocketAddress inetSocketAddress) {
        super(inetSocketAddress);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        log.info("onOpen", conn, handshake);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        log.info("onClose", conn, code, reason, remote);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        log.info("onMessage(" + conn + ", " + message + ")");
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        log.info("onError!", conn, ex);
    }

    @Override
    public void onStart() {
        log.info("onStart WSWSS!");
    }
}
