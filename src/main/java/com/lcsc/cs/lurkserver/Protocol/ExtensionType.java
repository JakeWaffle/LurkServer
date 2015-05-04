package com.lcsc.cs.lurkserver.Protocol;

/**
 * Created by Jake on 3/29/2015.
 * This class contains the Extension headers that are supported specifically by this server!
 */
public enum ExtensionType {
    /*
    Extension: LOOTP
    NiceName: loot
    Type: ACTON
    Description: Loot gold from a dead player!
    Parameter: Player Name*/

    PICK_UP("PCKUP", "pick up", "ACTON", "This picks up a key that's in the room.", "Key Name"),
    UNLOCK("UNLCK", "unlock", "ACTON", "This will use one of your keys to unlock a door to a room.", "Room Name");

    public final String extensionHeader;
    public final String niceName;
    public final String type;
    public final String description;
    public final String parameter;

    private ExtensionType(String extensionHeader, String niceName, String type, String description, String parameter) {
        this.extensionHeader    = extensionHeader;
        this.niceName           = niceName;
        this.type               = type;
        this.description        = description;
        this.parameter          = parameter;
    }

    /**
     * @return This returns a list of information about the extension so it can be sent to the user.
     */
    public String getInfoBlock() {
        return  String.format("Extension: %s\n", extensionHeader) +
                String.format("NiceName: %s\n", niceName) +
                String.format("Type: %s\n", type) +
                String.format("Description: %s\n", description) +
                String.format("Parameter: %s\n\n", parameter);
    }

    public static ExtensionType fromString(String extensionHeader) {
        if (extensionHeader != null) {
            for (ExtensionType e : ExtensionType.values()) {
                if (extensionHeader.equalsIgnoreCase(e.extensionHeader)) {
                    return e;
                }
            }
        }
        return null;
    }

    /**
     * This will return a regex pattern that will find the extension headers that the server supports.
     * @return A regex pattern to search for extension headers with.
     */
    public static String getExtensionTypePattern() {
        return "PCKUP|UNLCK";
    }


    /**
     * THis gets all of the information about each extension. THis will be returned to the user in the
     * QUERY response.
     * @return An info block for each supports extension type.
     */
    public static String getAllExtensionInfo() {
        String extensionInfo = "";
        for (ExtensionType e : ExtensionType.values()) {
            extensionInfo += e.getInfoBlock();
        }
        return extensionInfo;
    }
}
