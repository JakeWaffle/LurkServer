package com.lcsc.cs.lurkserver.game;

import com.lcsc.cs.lurkserver.Protocol.Response;
import com.lcsc.cs.lurkserver.Protocol.ResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

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

    public synchronized void update() {
        clients.update();
    }

    /**
     * This should be used when the server first is started up. It will load up the game's information.
     * The map is loaded, the game's description is loaded, the player's are loaded and whatever else.
     */
    public void loadGame(String gameDir) {
        //This will load the game description! Whew!
        String projRoot     = new File("").getAbsolutePath();
        File gameDescrFile  = new File(projRoot, "data/"+gameDir+"/game_description.txt");

        String gameDescription = "No one knows anything about this game... because the file does not exist!";
        if (gameDescrFile.exists()) {
            try {
                RandomAccessFile file = new RandomAccessFile(gameDescrFile, "r");
                byte[] description = new byte[(int)file.length()];
                file.read(description);
                gameDescription = new String(description).trim();
            } catch (FileNotFoundException e) {
                _logger.error("The game description file doesn't exist: " + gameDescrFile.getAbsolutePath(), e);
            } catch (IOException e) {
                _logger.error("Problem reading the game description file", e);
            }
        }
        _gameDescription = "GameDescription: "+gameDescription;
        
        map.loadMap(gameDir);
    }

    /**
     * This will be what is sent back to the client when it sends the 'QUERY' command.
     * @param playerName The name of the player.
     * @return The response to the QUERY command for a specific player.
     */
    public synchronized Response generateQueryResponse(String playerName) {
        /*return new Response(ResponseType.INFORM,
                String.format("%s\n\n%s%s%s",
                _gameDescription,
                //TODO Extension stats string construction.
                players.getPlayer(playerName).getStats(),
                players.getPlayerList()));*/
        return new Response(ResponseHeader.INFORM,
                String.format("%s\n\n%s\n\n%s",
                        _gameDescription,
                        players.getPlayer(playerName).getStats(),
                        players.getPlayerList()));
    }

    /**
     * This will move a player from one room to another. This assumes that the room change is possible!
     * @param player This is the player that is changing rooms.
     * @param newRoom This is the name of the room that's being switched to.
     * @return The response that will be sent back to the client. It will either be
     *         "REJEC No Connection", "RESLT Collected (int) Gold" or "RESLT Enter No Gold".
     */
    public synchronized Response changeRoom(Player player, String newRoom) {
        Response response;
        if (map.areRoomsConnected(player.currentRoom(), newRoom)) {
            int goldReceived = map.changeRoom(player, newRoom);
            player.changeRoom(newRoom);

            if (goldReceived == 0)
                response = ResponseMessage.NO_GOLD.getResponse();
            else
                response = new Response(ResponseHeader.RESULT, String.format("Collected %d Gold", goldReceived));
        }
        else
            response = ResponseMessage.NO_CONNECTION.getResponse();

        return response;
    }


    public synchronized void stopGame() {
        clients.dropClients();
    }
}
