package com.lcsc.cs.lurkserver.server;

import com.lcsc.cs.lurkserver.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Jake on 3/26/2015.
 * This class handles the configuring, starting and stopping of the server. It also will accept client connections
 * and then delegate a new thread to talk with that client until the client is done.
 *
 * TODO Determine if multi-client messages are needed at all and how they should be handled if needed.
 * TODO Add an instance of the Game class here so it can given to the threads handling the client!
 */
public class Server {
    private static final Logger _logger      = LoggerFactory.getLogger(Server.class);
    private ServerSocket _serverSocket;

    public Server() {}

    public void configureServer(Settings settings) {
        try {
            _serverSocket = new ServerSocket(settings.port);
        } catch (IOException e) {
            _logger.error("Couldn't start the server", e);
        }
    }

    public void start() {
        try {
            Socket sock = _serverSocket.accept();
            System.out.println("Just connected to "
                    + sock.getRemoteSocketAddress());
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            while (true) {
                char[] buf = new char[1000];
                in.read(buf);

                String msg = new String(buf);
                System.out.println(msg.trim());
            }
        } catch(IOException e) {
            _logger.error("Something happened when dealing with clients", e);
        }
    }

    public void stop() {
        try {
            _serverSocket.close();
        } catch (IOException e) {
            _logger.error("Couldn't close server socket", e);
        }
    }
}
