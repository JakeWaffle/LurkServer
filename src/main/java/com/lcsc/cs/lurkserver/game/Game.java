package com.lcsc.cs.lurkserver.game;

import com.lcsc.cs.lurkserver.Protocol.Response;
import com.lcsc.cs.lurkserver.Protocol.ResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Jake on 3/30/2015.
 * This class handles the logic for the game. It is used by the Server to actually use the game.
 * Many client threads will have access to this class by the way.
 */
public class Game {
    private static final Logger _logger = LoggerFactory.getLogger(Game.class);
    public final ClientPool clients;
    public final PlayerPool players;

    public Game() {
        clients = new ClientPool();
        players = new PlayerPool();
    }

    public synchronized void update() {
        clients.update();
    }

    /**
     * This should be used when the server first is started up. It will load up the game's information.
     * The map is loaded, the game's description is loaded, the player's are loaded and whatever else.
     */
    public void loadGame() {

    }

    /**
     * This will be what is sent back to the client when it sends the 'QUERY' command.
     * @param playerName The name of the player.
     * @return The response to the QUERY command for a specific player.
     */
    public Response generateQueryResponse(String playerName) {
        //TODO Finish the query response (make sure INFOM's work also)
        return new Response(ResponseType.INFORM, "Imma response");
    }


    public synchronized void stopGame() {
        clients.dropClients();
    }
}
