package com.lcsc.cs.lurkserver.server;

import com.lcsc.cs.lurkserver.Settings;
import com.lcsc.cs.lurkserver.game.Client;
import com.lcsc.cs.lurkserver.game.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

/**
 * Created by Jake on 3/26/2015.
 * This class handles the configuring, starting and stopping of the server. It also will accept client connections
 * and then delegate a new thread to talk with that client until the client is done.
 */
public class Server extends Thread {
    private static final Logger             _logger = LoggerFactory.getLogger(Server.class);
    private              boolean            _done;
    private              Game               _game;
    private              ClientListener     _listener;

    public Server() {
        _done = false;
        _game = new Game();
    }

    public void configureServer(Settings settings) {
        _game.loadGame(settings.loadGameFile());
        _listener = new ClientListener(this, settings.port);
        _listener.start();
    }

    @Override
    public void run() {
        while (!_done) {
            _game.update(1);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void addClient(Socket socket) {
        Client client = new Client(socket, _game);
        client.start();
        _game.clients.addClient(client);
    }

    public synchronized void stopServer() {
        _done = true;
        _listener.stopListener();
        _game.stopGame();
    }
}
