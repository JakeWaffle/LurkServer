package com.lcsc.cs.lurkserver.Protocol;

/**
 * Created by Jake on 3/29/2015.
 * This class contains the Extension headers that are supported specifically by this server!
 */
public enum ExtensionType {
    SOMETHING("dunno");
    //TODO Figure out some extensions! Yeah!

    private final String _extensionHeader;

    private ExtensionType(String extensionHeader) {
        _extensionHeader = extensionHeader;
    }

    public String getExtensionHeader() {
        return _extensionHeader;
    }

    /**
     * This will return a regex pattern that will find the extension headers that the server supports.
     * @return A regex pattern to search for extension headers with.
     */
    public static String getExtensionTypePattern() {
        return "asdfasdf";
    }
}
