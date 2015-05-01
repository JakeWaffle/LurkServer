package com.lcsc.cs.lurkserver.game;

/**
 * Created by Jake on 4/30/2015.
 * This is an interface for the Monster and Player classes. It's for making sure they are interchangable
 * when it comes to battles.
 */
public interface Being {
    /**
     * @return Returns the attack of the Being.
     */
    public int getAttack();

    /**
     * This does damage to the Being but after first subtracting the defense of the Being.
     * @param damage This is the attack + d20 roll of the attacker. (Criticals double the attack.)
     * @return The amount of gold if any that was received from the Being. Gold is dropped only when
     * the Being has first died.
     */
    public int doDamage(int damage);

    /**
     * This is called each time this Being does damage to another Being.
     * @param gold This is the gold that is picked up after damage is done to another Being. If the other
     *             Being isn't dead, then zero gold will be passed to this method.
     */
    public void pickedUpGold(int gold);
}
