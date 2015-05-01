package com.lcsc.cs.lurkserver.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by Jake on 4/26/2015.
 */
public class Monster implements Being {
    private static final Logger _logger         = LoggerFactory.getLogger(Monster.class);
    public  final   String      name;
    private         String      _description;
    private         int         _gold;
    private         int         _attack;
    private         int         _defense;
    private         int         _regen;
    private         BeingStatus _status;
    private         int         _maxHealth;
    private         int         _health;
    private         boolean     _started;

    public Monster(Map<String, Object> monsterData) {
        name        = (String)monsterData.get("name");
        _description = (String)monsterData.get("description");
        _gold        = ((Long)monsterData.get("gold")).intValue();
        _attack      = ((Long)monsterData.get("attack")).intValue();
        _defense     = ((Long)monsterData.get("defense")).intValue();
        _regen       = ((Long)monsterData.get("regen")).intValue();
        _maxHealth  = ((Long)monsterData.get("max_health")).intValue();
        _health      = _maxHealth;
        _status      = BeingStatus.ALIVE;
    }

    public synchronized boolean isDead() {
        return _health <= 0;
    }

    /**
     * This gets the attack of the player so damage can be done to an enemy.
     * @return The value of the player's attack or 0 if the player is dead.
     */
    @Override
    public synchronized int getAttack() {
        return isDead() ? 0 : _attack;
    }

    /**
     * This is called when an enemy is attacking the monster.
     * @param damage This is the attack of the enemy in addition to the d20 roll that was obtained.
     *               If a 1 was rolled, this method will not be called and damage will be done to the user
     *               instead.
     * @return The amount of gold dropped is returned. Gold is only dropped if the player has died.
     */
    @Override
    public synchronized int doDamage(int damage) {
        if (damage > _defense)
            _health -= damage-_defense;

        int gold = 0;
        if (_health <= 0) {
            _status = BeingStatus.DEAD;
            gold    = _gold;
            _gold   = 0;
        }
        return gold;
    }

    /**
     * This is called each time this Being does damage to another Being.
     * @param gold This is the gold that is picked up after damage is done to another Being. If the other
     *             Being isn't dead, then zero gold will be passed to this method.
     */
    @Override
    public synchronized void pickedUpGold(int gold) {
        _gold += gold;
    }

    /**
     * This returns the information for the monster so it can be sent to the client.
     * @return
     */
    public synchronized String getInfo() {
        /*
        Name:  Glog
        Description: A slimy and toothy character
        Gold: 50
        Attack: 20
        Defense: 30
        Regen: 100
        Monster
        Health: 100
        */

        String info = String.format("Name: %s\n", name)+
                String.format("Description: %s\n", _description)+
                String.format("Gold: %s\n", _gold)+
                String.format("Attack: %s\n", _attack)+
                String.format("Defense: %s\n", _defense)+
                String.format("Regen: %s\n", _regen);

        if (_status == BeingStatus.ALIVE)
            info += "Monster\n";
        //TODO Make sure the 'dead monster' part of the info is correct!
        else
            info += "Dead Monster\n";

        info += String.format("Health: %s", _health);

        return info;
    }
}
