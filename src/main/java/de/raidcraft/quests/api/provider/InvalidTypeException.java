package de.raidcraft.quests.api.provider;

import de.raidcraft.api.RaidCraftException;

/**
 * @author Silthus
 */
public class InvalidTypeException extends RaidCraftException {

    public InvalidTypeException(String message) {

        super(message);
    }
}
