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
 *
 * TODO Determine if multi-client messages are needed at all and how they should be handled if needed.
 * TODO Add an instance of the Game class here so it can given to the threads handling the client!
 * TODO A client listener class may need to be created to accept new client connections.
 *      The server may need to deal with other things such as events that happen to multiple players.
 * TODO An instance of the Server needs to be given to each of the Concierge instances.
 *      The clients will need to interact with other players. So the Concierge instances will need to
 *      talk to each other. This might be able to be done through a Game class that maps player names
 *      to the Concierge instances? It might also be able to be done through the Server.
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
        _listener = new ClientListener(this, settings.port);
        _listener.start();
    }

    @Override
    public void run() {
        while (!_done) {
            _game.update();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //This is where time based or triggered events that affect possibly more than one user should be processed.
            //This can be done using a Game class of some sort.
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
