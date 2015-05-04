package com.lcsc.cs.lurkserver.game;

import com.lcsc.cs.lurkserver.Protocol.Response;
import com.lcsc.cs.lurkserver.Protocol.ResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jake on 4/6/2015.
 */
public class ClientPool {
    private static final Logger _logger = LoggerFactory.getLogger(ClientPool.class);

    private PlayerPool _players;

    //This maps a client's player name to the Client object.
    private ConcurrentMap<String, Client> _connectedClients;

    //Some random id that was given to the client when it was instantiated will be the string that is used
    //to look up that client.
    private ConcurrentMap<String, Client> _unconnectedClients;

    //These are a list of the tasks that need to be done
    private ConcurrentLinkedQueue<Task>   _stagedTasks;

    public ClientPool(PlayerPool players) {
        _players            = players;
        _connectedClients   = new ConcurrentHashMap<String, Client>();
        _unconnectedClients = new ConcurrentHashMap<String, Client>();
        _stagedTasks        = new ConcurrentLinkedQueue<Task>();
    }


    /**
     * This will add a new Client to the shared Game object. This makes all of the Client objects
     * 'connected' since they all have access to this Game object.
     *
     * @param client This is a recently initialized Client that we're adding to the game!
     */
    public synchronized void addClient(Client client) {
        String randId = UUID.randomUUID().toString();
        _unconnectedClients.put(randId, client);
        client.id = randId;
    }

    /**
     * This will send a message to another user. This will deny any messages with server message headers in them.
     * @param playerName The destination player.
     * @param message The payload for the message.
     * @return The response to the user which will either be an ACEPT Fine or REJEC Cannot Message Player.
     */
    public synchronized Response sendMessage(String playerName, String message) {
        Response response;

        Pattern pattern = Pattern.compile(ResponseHeader.getResponseTypePattern());
        Matcher matcher = pattern.matcher(message);

        //Has a server response header been found?
        if (matcher.find())
            response = ResponseMessage.CANNOT_MESSAGE_PLAYER.getResponse();
        else if (_connectedClients.containsKey(playerName)) {
                response = ResponseMessage.FINE.getResponse();
                _connectedClients.get(playerName).sendMessage(message);
        }
        else
            response = ResponseMessage.CANNOT_MESSAGE_PLAYER.getResponse();

        return response;
    }

    /**
     * This is called whenever a user actually connects to the game with a player name that hasn't already
     * been taken.
     *
     * @param playerName    This is the player name that the client is connecting with. This will be the key
     *                      for the Client when it is put into _connectedClients.
     * @param unconnectedId This is the id of the client assuming it hasn't connected yet. This will be the key
     *                      of the Client that is already within _unconnectedClients.
     * @return The response that is to be sent back to the player will be returned here.
     */
    public synchronized ResponseMessage connectClient(final String playerName, final String unconnectedId) {
        ResponseMessage response = null;

        //First check the username for any server response headers or spaces.
        Pattern pattern = Pattern.compile(ResponseHeader.getResponseTypePattern());
        Matcher matcher = pattern.matcher(playerName);

        //Has a server response header been found?
        if (matcher.find())
            response = ResponseMessage.NAME_TAKEN;
        else if (_connectedClients.containsKey(playerName))
            response = ResponseMessage.NAME_TAKEN;
        //else if (player has existed, but isn't connected and is dead) {
        //  response = new Response(ResponseType.REJECTED, "Dead Without Health");
        //}
        else if (_unconnectedClients.containsKey(unconnectedId)) {
            Client client = _unconnectedClients.remove(unconnectedId);
            _connectedClients.put(playerName, client);
            client.id = playerName;

            response = _players.loadPlayer(playerName);
        }
        else {
            response = ResponseMessage.INCORRECT_STATE;
            _logger.error("The client should only be calling ClientPool.connectClient() when the client" +
                    "isn't connected yet!!!!");
        }

        return response;
    }

    /**
     * This stage a Client to be disconnected from the ClientPool. Then the Client will be disconnected when update()
     * is called.
     * @param clientId This will be a player name or the random id depending on whether the Client was connected or not.
     */
    public synchronized void stageClientDisconnect(final String clientId) {
        _stagedTasks.add(new Task() {
            @Override
            public void doTask() {
                disconnectClient(clientId);
            }
        });
    }

    /**
     * This will remove the Client from the ClientPool and also make the Client's thread get joined and cleaned up.
     * @param clientId This is the id of the client that will be disconnected.
     */
    public synchronized void disconnectClient(String clientId) {
        final Client client;
        if (_connectedClients.containsKey(clientId)) {
            client = _connectedClients.remove(clientId);
        }
        else if (_unconnectedClients.containsKey(clientId)) {
            client = _unconnectedClients.remove(clientId);
        }
        else {
            return;
        }

        client.dropClient();
        try {
            client.join();
            _logger.debug("Joined Client thread!");
        } catch (InterruptedException e) {
            _logger.error("Interrupted when joining the Client's thread", e);
        }
    }

    /**
     * This will update the ClientPool! This should ONLY be used by one of the main server threads! This shouldn't
     * be used in a Client thread at all.
     */
    public synchronized void update() {
        Task curTask = _stagedTasks.poll();
        while (curTask != null) {
            curTask.doTask();
            curTask = _stagedTasks.poll();
        }
    }

    /**
     * This is for stopping the ClientPool along with all of the Clients within it. This should ONLY be used by one
     * of the main server threads! This shouldn't be used in a Client thread at all.
     */
    public synchronized void dropClients() {
        for (Client client : _connectedClients.values()) {
            stageClientDisconnect(client.id);
        }

        for (Client client : _unconnectedClients.values()) {
            stageClientDisconnect(client.id);
        }

        update();
    }
}