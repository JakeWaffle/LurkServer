package com.lcsc.cs.lurkserver.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Jake on 3/30/2015.
 */
public class ClientListener extends Thread {
    private static final Logger         _logger      = LoggerFactory.getLogger(ClientListener.class);
    private              ServerSocket   _serverSocket;
    private              Server         _server;
    private              boolean        _done;

    public ClientListener(Server server, int port) {
        _done   = false;
        _server = server;
        try {
            _serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            _logger.error("Couldn't start the server", e);
        }
    }

    @Override
    public void run() {
        while (!_done) {
            try {
                Socket sock = _serverSocket.accept();
                _server.addClient(sock);
            } catch (IOException e) {
                _logger.error("The server was shutdown or something happened with the clients", e);
            }
        }
    }

    public synchronized void stopListener() {
        try {
            _done = true;
            _serverSocket.close();
        } catch (IOException e) {
            _logger.error("Couldn't close server socket", e);
        }
    }
}
