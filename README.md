# LurkServer
This server is the second project in my networking class. It will host a MUD that uses the Lurk Protocol and will be compatible with the Lurk Client that I've also made.

# How to Build and Run it?!
The program can be built using the following command in the root directory:
```
gradle clean jar
```

Then a jar including all of its depencencies should be placed within build/libs/ in the project. The manifest
attribute for the main class is already set, so run the jar with the following command:
```
java -jar LurkServer-1.0.jar
```