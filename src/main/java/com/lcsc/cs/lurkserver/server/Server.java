package com.lcsc.cs.lurkserver.server;

import com.lcsc.cs.lurkserver.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jake on 3/26/2015.
 * This class handles the configuring, starting and stopping of the server. It also will accept client connections
 * and then delegate a new thread to talk with that client until the client is done.
 *
 * TODO Determine if multi-client messages are needed at all and how they should be handled if needed.
 * TODO Add an instance of the Game class here so it can given to the threads handling the client!
 */
public class Server extends Thread {
    private static final Logger             _logger      = LoggerFactory.getLogger(Server.class);
    private              ServerSocket       _serverSocket;
    private              boolean            _done;
    private              List<Concierge>    _concierges;

    public Server() {
        _done = false;
        _concierges = new ArrayList<Concierge>();
    }

    public void configureServer(Settings settings) {
        try {
            _serverSocket = new ServerSocket(settings.port);
        } catch (IOException e) {
            _logger.error("Couldn't start the server", e);
        }
    }

    @Override
    public void run() {
        while (!_done) {
            try {
                Socket sock = _serverSocket.accept();
                Concierge concierge   = new Concierge(sock);
                concierge.start();
                _concierges.add(concierge);
            } catch (IOException e) {
                _logger.error("The server was shutdown or something happened with the clients", e);
            }
        }
    }

    public synchronized void stopServer() {
        try {
            _done = true;
            _serverSocket.close();

            for (Concierge concierge : _concierges) {
                concierge.dropClient();
                try {
                    concierge.join();
                    _logger.debug("Joined Concierge thread!");
                } catch (InterruptedException e) {
                    _logger.error("Interrupted when joining the Concierge's thread", e);
                }
            }
        } catch (IOException e) {
            _logger.error("Couldn't close server socket", e);
        }
    }
}
