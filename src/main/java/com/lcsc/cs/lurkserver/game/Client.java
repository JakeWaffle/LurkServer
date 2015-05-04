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

    //The id for the client is represented by a random UUID string (if not connected yet) or the player's name!
    public               String         id = null;

    private              MailMan        _mailMan;
    private              boolean        _done;
    private              ClientState    _clientState;
    private              Game           _game;
    private              Player         _player = null;

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
                        if (command.type == CommandType.LEAVE) {
                            _game.players.removePlayer(id);
                            _game.clients.stageClientDisconnect(id);
                            Client.this._clientState = ClientState.QUIT;
                        }
                        else if (_clientState == ClientState.NOT_CONNECTED) {
                            if (command.type == CommandType.CONNECT) {
                                ResponseMessage response = _game.clients.connectClient(command.parameter, id);

                                if (response == ResponseMessage.NEW_PLAYER) {
                                    _clientState = ClientState.NOT_STARTED;
                                    _player = _game.players.getPlayer(id);
                                    _player.setClient(Client.this);
                                }
                                else if (response == ResponseMessage.REPRISING_PLAYER) {
                                    _clientState = ClientState.STARTED;
                                    _player = _game.players.getPlayer(id);
                                    _player.setClient(Client.this);
                                }

                                _mailMan.sendMessage(response.getResponse());
                            }
                            else {
                                _mailMan.sendMessage(ResponseMessage.INCORRECT_STATE.getResponse());
                            }
                        }
                        else if (command.type == CommandType.QUERY) {
                            if (_clientState != ClientState.NOT_CONNECTED) {
                                Response response = _game.generateQueryResponse(Client.this.id);
                                _mailMan.sendMessage(response);
                            }
                            else {
                                _mailMan.sendMessage(ResponseMessage.INCORRECT_STATE.getResponse());
                            }
                        }
                        else if (_clientState == ClientState.NOT_STARTED) {
                            Client.this.handleNotStartedState(command);
                        }
                        else if (_clientState == ClientState.STARTED) {
                            Client.this.handleStartedState(command);
                        }
                        else {
                            _mailMan.sendMessage(ResponseMessage.INCORRECT_STATE.getResponse());
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

    /**
     * This is for relaying the room info to the user without the user asking for it.
     * This is called if a player joins in on a fight.
     * @param infoList This is a list of INFOM messages that must be sent to the user.
     */
    public synchronized void sendRoomInfo(List<String> infoList) {
        for (String info : infoList) {
            _mailMan.sendMessage(new Response(ResponseHeader.INFORM, info));
        }
    }

    /**
     * This will send a message to the user from another user.
     * @param message The message that is to be sent.
     */
    public synchronized void sendMessage(String message) {
        _mailMan.sendMessage(new Response(ResponseHeader.MESSAGE, message));
    }

    /**
     * This relays the status of the player after being involved in a fight.
     * @param health The health of the player after the fight.
     * @param goldCollected The gold collected during the fight.
     */
    public synchronized void sendStatus(int health, int goldCollected) {
        _mailMan.sendMessage(new Response(ResponseHeader.NOTIFY, String.format("Collected %d Gold", goldCollected)));
        _mailMan.sendMessage(new Response(ResponseHeader.NOTIFY, String.format("Health %d", health)));
    }

    /**
     * This handles the commands sent from the client while the client is in the NOT_STARTED state.
     * @param command This is the command that the client has sent.
     */
    public synchronized void handleNotStartedState(Command command) {
        if (command.type == CommandType.SET_PLAYER_DESC ||
                command.type == CommandType.SET_ATTACK_STAT ||
                command.type == CommandType.SET_DEFENSE_STAT ||
                command.type == CommandType.SET_REGEN_STAT) {
            ResponseMessage responseMsgType = _player.setStat(command.type, command.parameter);
            _mailMan.sendMessage(responseMsgType.getResponse());
        }
        else if (command.type == CommandType.START) {
            if (_player.start()) {
                List<String> infoList = _game.map.getRoomInfo(_player.currentRoom(), _player.name);
                for (String info : infoList) {
                    _mailMan.sendMessage(new Response(ResponseHeader.INFORM, info));
                }
                _player.saveData();

                _clientState = ClientState.STARTED;
            }
            else
                _mailMan.sendMessage(ResponseMessage.NOT_READY.getResponse());
        }
        else
            _mailMan.sendMessage(ResponseMessage.INCORRECT_STATE.getResponse());
    }

    public synchronized void handleStartedState(Command command) {
        if (command.type == CommandType.ACTION) {
            if (command.actionType == ActionType.CHANGE_ROOM) {
                for (Response response : _game.changeRoom(_player, command.parameter)) {
                    _mailMan.sendMessage(response);
                }
                if (_player.currentRoom().equals(command.parameter)) {
                    List<String> infoList = _game.map.getRoomInfo(_player.currentRoom(), _player.name);
                    sendRoomInfo(infoList);
                }
            }
            else if (command.actionType == ActionType.FIGHT) {
                _game.map.fightMonsters(_player.currentRoom());
            }
            else if (command.actionType == ActionType.MESSAGE) {
                int firstSpaceIndx  = command.parameter.indexOf(" ");

                String playerName   = command.parameter.substring(0, firstSpaceIndx);
                String message      = command.parameter.substring(firstSpaceIndx+1);
                _mailMan.sendMessage(_game.clients.sendMessage(playerName, message));
            }
            else
                _mailMan.sendMessage(ResponseMessage.INCORRECT_STATE.getResponse());
        }
        else if (command.extension == ExtensionType.PICK_UP) {
            _mailMan.sendMessage(_game.map.pickupKey(_player, command.parameter));
        }
        else if (command.extension == ExtensionType.UNLOCK) {
            _mailMan.sendMessage(_game.map.unlockDoor(_player, command.parameter));
        }
        else if (command.type == CommandType.START) {
            List<String> infoList = _game.map.getRoomInfo(_player.currentRoom(), _player.name);
            sendRoomInfo(infoList);
        }
        else
            _mailMan.sendMessage(ResponseMessage.INCORRECT_STATE.getResponse());
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
