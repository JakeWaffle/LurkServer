package com.lcsc.cs.lurkserver.game;

import com.lcsc.cs.lurkserver.Protocol.Response;
import com.lcsc.cs.lurkserver.Protocol.ResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class  Room {
    private static final Logger     _logger = LoggerFactory.getLogger(Room.class);

    private Random                  _randomGenerator;
    private String                  _roomName;
    private String                  _description;
    private String                  _roomInfo;
    private List<String>            _connections;
    private List<Monster>           _monsters;
    private int                     _gold;
    //Maps current locked room names to the names of the key that opens them.
    //A room is unlocked when removed from this map!
    private Map<String, String>     _lockedRooms;
    private List<String>            _keys;
    private Map<String, Player>     _players;

    public Room(String roomName, Map<String, Object> roomData) {
        if (roomData.containsKey("description") &&
                roomData.containsKey("connections") &&
                roomData.containsKey("monsters") &&
                roomData.containsKey("locked_rooms") &&
                roomData.containsKey("keys") &&
                roomData.containsKey("gold")) {
            _randomGenerator = new Random();
            _roomName = roomName;
            _description = (String) roomData.get("description");
            _connections = new ArrayList<String>();
            Object[] connections = (Object[]) roomData.get("connections");

            for (Object connection : connections) {
                _connections.add((String) connection);
            }

            _monsters = new ArrayList<Monster>();
            Object[] monsters = (Object[]) roomData.get("monsters");

            for (Object monsterObj : monsters) {
                Map<String, Object> monsterData = (Map<String, Object>) monsterObj;
                _monsters.add(new Monster(monsterData));
            }

            _lockedRooms = new HashMap<String, String>();
            Map<String, Object> lockedRooms = (Map<String, Object>) roomData.get("locked_rooms");

            for (Map.Entry<String, Object> lockedRoom : lockedRooms.entrySet()) {
                _lockedRooms.put(lockedRoom.getKey(), (String) lockedRoom.getValue());
            }

            _gold           = ((Long)roomData.get("gold")).intValue();

            _keys           = new ArrayList<String>();
            Object[] keys   = (Object[])roomData.get("keys");

            for (Object key : keys) {
                _keys.add((String)key);
            }

            _players = new HashMap<String, Player>();

            //The room info will later be used when a player enters this room and needs its information.
            _roomInfo = String.format("Name: %s\n", _roomName) +
                    String.format("Description: %s\n", _description);

            for (int i = 0; i < _connections.size(); i++) {
                if (i == _connections.size() - 1 && _monsters.size() == 0)
                    _roomInfo += String.format("Connection: %s", _connections.get(_connections.size() - 1));
                else
                    _roomInfo += String.format("Connection: %s\n", _connections.get(i));
            }

            for (int i = 0; i < _monsters.size(); i++) {
                if (i == _monsters.size() - 1)
                    _roomInfo += String.format("Monster: %s", _monsters.get(i).name);
                else
                    _roomInfo += String.format("Monster: %s\n", _monsters.get(i).name);
            }
        }
        else {
            _logger.error("The 'description', 'connections', 'monsters', 'locked_rooms', 'gold' or 'keys' weren't defined for the room, "+roomName);
            System.exit(1);
        }
    }


    /**
     * This will make sure that each player and monster is updated.
     * @param secondsPassed This is how many seconds has passed since the last update.
     */
    public synchronized void update(int secondsPassed) {
        for (Monster monster : _monsters)
            monster.regenHealth(secondsPassed);

        for (Player player : _players.values())
            player.regenHealth(secondsPassed);
    }

    /**
     * @param otherRoom This is the other room that may or may not be connected to this one.
     * @return A boolean informing if the given room is connected to this room or not.
     */
    public synchronized boolean isConnected(String otherRoom) {
        return _connections.contains(otherRoom);
    }

    /**
     * This triggers players and monsters to battle each other.
     */
    public synchronized void fight() {
        if (!areMonstersDead()) {
            for (Player player : _players.values()) {
                if (!player.isDead()) {
                    Monster randomMonster = randomMonster();

                    //Selects whether player or monster attacks first.
                    //Player == 0, Monster == 1.
                    int firstAttacker = _randomGenerator.nextInt(2);

                    if (firstAttacker == 0)
                        oneVsOne(player, randomMonster);
                    else
                        oneVsOne(randomMonster, player);

                    player.sendRoomInfo(getRoomInfo(player.name));
                }
            }
        }
    }

    /**
     * This utilizes the Being interface to make two Beings do damage to each other using the
     * d20 system that has been designed for this game.
     * @param person1 This is the first person to attack.
     * @param person2 This is the second person to attack.
     */
    private void oneVsOne(Being person1, Being person2) {
        //Person1 attacks person2.
        int atk     = person1.getAttack();
        int d20Roll = rollD20();
        //This is the gold dropped if a person happens to die.
        int gold;

        if (d20Roll == 1) {
            gold = person1.doDamage(atk);
            person2.pickedUpGold(gold);
        }
        else if (d20Roll == 20) {
            gold = person2.doDamage((2 * atk) + d20Roll);
            person1.pickedUpGold(gold);
        }
        else {
            gold = person2.doDamage(atk + d20Roll);
            person1.pickedUpGold(gold);
        }

        //Person2 attacks person1.
        atk         = person2.getAttack();
        d20Roll     = rollD20();

        if (d20Roll == 1) {
            gold = person2.doDamage(atk);
            person1.pickedUpGold(gold);
        }
        else if (d20Roll == 20) {
            gold = person1.doDamage((2 * atk) + d20Roll);
            person2.pickedUpGold(gold);
        }
        else {
            gold = person1.doDamage(atk + d20Roll);
            person2.pickedUpGold(gold);
        }
    }

    /**
     * This will roll a d20 and return the results.
     * @return An integer in [1,20]
     */
    private int rollD20() {
        return _randomGenerator.nextInt(20)+1;
    }

    private boolean areMonstersDead() {
        boolean monstersAreDead = true;
        for (Monster monster : _monsters) {
            if (!monster.isDead()) {
                monstersAreDead = false;
                break;
            }
        }
        return monstersAreDead;
    }

    /**
     * @return A random monster in the room that isn't dead.
     */
    private Monster randomMonster() {
        Monster monster;
        do {
            int index   = _randomGenerator.nextInt(_monsters.size());
            monster     = _monsters.get(index);
        } while(monster.isDead());
        return monster;
    }

    /**
     * This is called when a user uses the UNLCK extension to unlock something.
     * @param player This is the player that may have the key.
     * @param roomName This is the room that is locked.
     * @return A response that will be sent to the user.
     */
    public synchronized Response unlockDoor(Player player, String roomName) {
        Response response;
        if (_lockedRooms.containsKey(roomName) && player.hasKey(_lockedRooms.get(roomName))) {
            _lockedRooms.remove(roomName);
            response = new Response(ResponseHeader.RESULT, "The door to "+roomName+" has been unlocked!");
        }
        else if (_lockedRooms.containsKey(roomName))
            response = new Response(ResponseHeader.RESULT, "The "+_lockedRooms.get(roomName)+" key is needed " +
                    "to unlocked the door to "+roomName+".");
        else
            response = new Response(ResponseHeader.RESULT, "The door to "+roomName+" is already unlocked!");

        return response;
    }

    /**
     * Checks to see if the door is unlocked.
     * @param roomName THe name of the room that may be locked.
     * @return The key that will unlock the door or null.
     */
    public synchronized String isDoorUnlocked(String roomName) {
        return _lockedRooms.getOrDefault(roomName, null);
    }

    /**
     * This will have a player pickup a key that is in the room.
     * @param player The player picking up the key.
     * @param keyName The name of the key (found in the description of the room.)
     * @return A response for the user saying if it was successful.
     */
    public synchronized Response pickUpKey(Player player, String keyName) {
        Response response;

        if (_keys.contains(keyName)) {
            boolean success = player.pickUpKey(keyName);
            if (success)
                response = new Response(ResponseHeader.RESULT, "You have picked up "+keyName+".");
            else
                response = new Response(ResponseHeader.RESULT, "You already have "+keyName+".");
        }
        else {
            response = new Response(ResponseHeader.RESULT, _roomName+" does not have a key called "+keyName+".");
        }
        return response;
    }

    /**
     * This is meant to get a list of information about this room, that will be sent back to the client. In addition
     * to the room's info it will include players and monsters in the room.
     * @param excludedPlayerName This player will be excluded from the list of players in the room.
     * @return A list of strings that are meant to be sent to the client in separate INFOM messages.
     */
    public synchronized List<String> getRoomInfo(String excludedPlayerName) {
        List<String> infoList = new ArrayList<String>();

        for (Player player : _players.values()) {
            if (!player.name.equals(excludedPlayerName))
                infoList.add(player.getInfo());
        }

        for (Monster monster : _monsters) {
            infoList.add(monster.getInfo());
        }

        infoList.add(_roomInfo);

        return infoList;
    }


    /**
     * This will add a player to this room.
     * @param player This is the player that has entered the room.
     * @return The amount of gold that was picked up.
     */
    public synchronized int addPlayer(Player player) {
        int gold    = _gold;
        _gold       = 0;
        _players.put(player.name, player);
        return gold;
    }

    /**
     * This will remove a player from this room.
     * @param playerName This is the player that has left the room.
     */
    public synchronized void removePlayer(String playerName) {
        _players.remove(playerName);
    }
}