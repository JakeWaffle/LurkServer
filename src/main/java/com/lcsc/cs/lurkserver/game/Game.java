package com.lcsc.cs.lurkserver.game;

import com.lcsc.cs.lurkserver.Protocol.ExtensionType;
import com.lcsc.cs.lurkserver.Protocol.MailMan;
import com.lcsc.cs.lurkserver.Protocol.Response;
import com.lcsc.cs.lurkserver.Protocol.ResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Jake on 3/30/2015.
 * This class handles the logic for the game. It is used by the Server to actually use the game.
 * Many client threads will have access to this class by the way.
 */
public class Game {
    private static final Logger _logger = LoggerFactory.getLogger(Game.class);
    public  final ClientPool    clients;
    public  final PlayerPool    players;
    public  final GameMap       map;

    private       String        _gameDescription;

    public Game() {
        map     = new GameMap();
        players = new PlayerPool(map);
        clients = new ClientPool(players);
    }

    public synchronized void update(int secondsPassed) {
        clients.update();
        map.update(secondsPassed);
    }

    /**
     * This should be used when the server first is started up. It will load up the game's information.
     * The map is loaded, the game's description is loaded, the player's are loaded and whatever else.
     * @param gameDef This map defines the game that has been loaded.
     */
    public void loadGame(Map<String, Object> gameDef) {
        if (gameDef.containsKey("game_description")) {
            _gameDescription = "GameDescription: " + (String)gameDef.get("game_description");
            map.loadMap(gameDef);
        }
        else {
            _logger.error("The game_description wasn't found in the game definition file.");
            System.exit(1);
        }
    }

    /**
     * This will be what is sent back to the client when it sends the 'QUERY' command.
     * @param playerName The name of the player.
     * @return The response to the QUERY command for a specific player.
     */
    public synchronized Response generateQueryResponse(String playerName) {
        return new Response(ResponseHeader.INFORM,
                String.format("%s\n\n%s%s\n\n%s",
                        _gameDescription,
                        ExtensionType.getAllExtensionInfo(),
                        players.getPlayer(playerName).getInfo(),
                        players.getPlayerList(playerName)));
    }

    /**
     * This will move a player from one room to another. This assumes that the room change is possible!
     * @param player This is the player that is changing rooms.
     * @param newRoom This is the name of the room that's being switched to.
     * @return The response that will be sent back to the client. It will either be
     *         "REJEC No Connection", "RESLT Collected (int) Gold" or "RESLT Enter No Gold".
     */
    public synchronized List<Response> changeRoom(Player player, String newRoom) {
        List<Response> responses = new ArrayList<Response>();
        String neededKeyName = map.isDoorUnlocked(player.currentRoom(), newRoom);
        if (map.areRoomsConnected(player.currentRoom(), newRoom) && neededKeyName == null) {
            int goldReceived = map.changeRoom(player, newRoom);
            player.changeRoom(newRoom);
            player.pickedUpGold(goldReceived);

            if (goldReceived == 0)
                responses.add(ResponseMessage.NO_GOLD.getResponse());
            else
                responses.add(new Response(ResponseHeader.RESULT, String.format("Collected %d Gold", goldReceived)));
        }
        else if (neededKeyName != null) {
            responses.add(ResponseMessage.NO_CONNECTION.getResponse());
            responses.add(new Response(ResponseHeader.RESULT, "The door to "+newRoom+" is locked and requires a key: '"+neededKeyName+"'"));
        }
        else {
            responses.add(ResponseMessage.NO_CONNECTION.getResponse());
        }

        return responses;
    }


    public synchronized void stopGame() {
        players.savePlayers();
        clients.dropClients();
    }
}
