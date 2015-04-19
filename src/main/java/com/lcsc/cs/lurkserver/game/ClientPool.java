package com.lcsc.cs.lurkserver.game;

import com.lcsc.cs.lurkserver.Protocol.Response;
import com.lcsc.cs.lurkserver.Protocol.ResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Jake on 4/6/2015.
 */
public class ClientPool {
    private static final Logger _logger = LoggerFactory.getLogger(ClientPool.class);

    private Game _game;

    //This maps a client's player name to the Client object.
    private ConcurrentMap<String, Client> _connectedClients;

    //Some random id that was given to the client when it was instantiated will be the string that is used
    //to look up that client.
    private ConcurrentMap<String, Client> _unconnectedClients;

    //These are a list of the tasks that need to be done
    private ConcurrentLinkedQueue<Task>   _stagedTasks;

    public ClientPool(Game game) {
        _game               = game;
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
     * This is called whenever a user actually connects to the game with a player name that hasn't already
     * been taken.
     *
     * @param playerName    This is the player name that the client is connecting with. This will be the key
     *                      for the Client when it is put into _connectedClients.
     * @param unconnectedId This is the id of the client assuming it hasn't connected yet. This will be the key
     *                      of the Client that is already within _unconnectedClients.
     * @return The response that is to be sent back to the player will be returned here.
     */
    public synchronized Response connectClient(final String playerName, final String unconnectedId) {
        Response response = null;

        if (_connectedClients.containsKey(playerName)) {
            response = new Response(ResponseType.REJECTED, "Name Already Taken");
        }
        //else if (player has existed, but isn't connected and is dead) {
        //  response = new Response(ResponseType.REJECTED, "Dead Without Health");
        //}
        else if (_unconnectedClients.containsKey(unconnectedId)) {
            Client client = _unconnectedClients.remove(unconnectedId);
            _connectedClients.put(playerName, client);
            client.id = playerName;

            boolean notLoggedIn = _game.players.loadPlayer(playerName);

            if (notLoggedIn) {
                if (Player.playerExists(playerName)) {
                    response = new Response(ResponseType.ACCEPTED, "Reprising Player");
                }
                else {
                    response = new Response(ResponseType.ACCEPTED, "New Player");
                }
            }
            else {
                response = new Response(ResponseType.ACCEPTED, "Name Already Taken");
            }
        }
        else {
            response = new Response(ResponseType.REJECTED, "Incorrect State");
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
        final Client client;
        if (_connectedClients.containsKey(clientId)) {
            client = _connectedClients.get(clientId);
        }
        else if (_unconnectedClients.containsKey(clientId)) {
            client = _unconnectedClients.get(clientId);
        }
        else {
            return;
        }

        _stagedTasks.add(new Task() {
            @Override
            public void doTask() {
                disconnectClient(client);
            }
        });
    }

    /**
     * This will remove the Client from the ClientPool and also make the Client's thread get joined and cleaned up.
     * @param client This is the Client object that is to be disconnected!
     */
    public synchronized void disconnectClient(Client client) {
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