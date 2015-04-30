package com.lcsc.cs.lurkserver.game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import org.eclipse.jetty.util.ajax.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMap {
    private static final Logger _logger = LoggerFactory.getLogger(Map.class);

    private Map<String, Room> _rooms;
    private String            _startingRoom;
    private ClientPool        _clients;
    
    public GameMap() {
        _rooms         = new HashMap<String, Room>();
        //This is the default room for when the map file didn't get loaded correctly.
        _startingRoom  = "Purgatory";
    }

    public synchronized String getStartingRoom() {
        return _startingRoom;
    }

    /**
     * This will put a new player into the starting room.
     * @param newPlayer This is a player that has just started the game.
     */
    public synchronized void spawnPlayer(Player newPlayer) {
        _rooms.get(_startingRoom).addPlayer(newPlayer);
    }

    /**
     * This will move a player from one room to another
     * @param player This is the player who is leaving one room to go to another.
     * @param newRoom This is the room that the player is going to.
     * @return This is the amount of gold that the player has collected.
     */
    public synchronized int changeRoom(Player player, String newRoom) {
        _rooms.get(player.currentRoom()).removePlayer(player.name);
        return _rooms.get(newRoom).addPlayer(player);
    }

    /**
     * Checks to see if the given rooms are connected.
     * @param roomA A room's name
     * @param roomB Another room's name
     * @return A boolean informing if the rooms are connected or not.
     */
    public synchronized boolean areRoomsConnected(String roomA, String roomB) {
        Room room = _rooms.get(roomA);
        return room.isConnected(roomB);
    }

    /**
     * This is meant to get a list of information about the room that will be sent back to the client. In addition
     * to the room's info it will include players and monsters in the room.
     * @param room This is the room that the info will be about.
     * @return A list of strings that are meant to be sent to the client in separate INFOM messages.
     */
    public synchronized List<String> getRoomInfo(String room) {
        return _rooms.get(room).getRoomInfo();
    }

    /**
     * This triggers monsters and players in a room to fight!
     * @param room All players and monsters will fight in the current room.
     */
    public synchronized void fightMonsters(String room) {
        _rooms.get(room).fight();
    }

    /**
     * This will load a map for the game from a json file.
     * @param gameDir This is the directory (relative to the data
     *                directory) that contains the game description
     *                along with the map.
     */
    public void loadMap(String gameDir) {
        String projRoot = new File("").getAbsolutePath();
        FileReader reader = null;
        
        try {
            File gameDataDir        = new File(projRoot, "data/"+gameDir+"/map.mdef");
            reader                  = new FileReader(gameDataDir);
            
            Map<String, Object> map = (Map<String, Object>)JSON.parse(reader);
            
            _startingRoom           = (String)map.get("starting_room");
            
            //Each item maps a room name to some map of room data.
            Map<String, Object> rooms = (Map<String, Object>)map.get("rooms");
            
            for (Map.Entry<String, Object> room : rooms.entrySet()) {
                Room newRoom = new Room(room.getKey(), (Map<String, Object>)room.getValue());
                _rooms.put(room.getKey(), newRoom);
            }
        } catch (FileNotFoundException e) {
            _logger.error("Problem loading the player data file", e);
        } catch (IOException e) {
            _logger.error("Problem loading the player data file", e);
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {}
        }
    }
}

