package com.lcsc.cs.lurkserver.game;

import com.lcsc.cs.lurkserver.Protocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.List;

/**
 * Created by Jake on 3/29/2015.
 * This class will listen to a client's messages and will respond to it with the respective information.
 * So this class will need to have access to the game's map, player list, monsters and pretty much every part of the
 * game.
 */
public class Client extends Thread {
    private static final Logger         _logger = LoggerFactory.getLogger(Client.class);
    private              MailMan        _mailMan;
    private              boolean        _done;
    private              ClientState    _clientState;
    private              Game           _game;
    //The id for the client is represented by a random UUID string (if not connected yet) or the player's name!
    public               String         id = null;

    public Client(Socket socket, Game game) {
        _logger.debug("Just connected to " + socket.getRemoteSocketAddress());
        _done           = false;
        _game           = game;
        _clientState    = ClientState.NOT_CONNECTED;
        _mailMan        = new MailMan(socket);
        _mailMan.start();

        _mailMan.registerListener(new CommandListener() {
            @Override
            public void notify(List<Command> commands) {
                if (Client.this._clientState != ClientState.QUIT) {
                    for (Command command : commands) {
                        if (command.type == CommandType.CONNECT) {
                            if (_clientState == ClientState.NOT_CONNECTED) {
                                Response response = _game.clients.connectClient(command.parameter, Client.this.id);
                                _mailMan.sendMessage(response);
                            }
                            else {
                                _mailMan.sendMessage(new Response(ResponseType.REJECTED, "Incorrect State"));
                            }
                        } else if (command.type == CommandType.LEAVE) {
                            _game.clients.stageClientDisconnect(id);
                            Client.this._clientState = ClientState.QUIT;
                        } else if (command.type == CommandType.QUERY) {
                            if (_clientState != ClientState.NOT_CONNECTED) {
                                Response response = _game.generateQueryResponse(Client.this.id);
                                _mailMan.sendMessage(response);
                            }
                            else {
                                _mailMan.sendMessage(new Response(ResponseType.REJECTED, "Incorrect State"));
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void run() {
        while(!_done) {
            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                _logger.error("Interrupted while sleeping! I'm really mad!", e);
            }
        }
    }

    public synchronized ClientState getClientState() {
        return _clientState;
    }

    public synchronized void dropClient() {
        _done = true;
        _logger.debug("Dropping client!");
        _mailMan.disconnect();
        try {
            this._mailMan.join();
            _logger.debug("Joined MailMan thread!");
        } catch (InterruptedException e) {
            _logger.error("Interrupted when joining the MailMan's thread", e);
        }
    }
}
