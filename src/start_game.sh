#!/bin/bash
echo "Deleting all .class files..."
find ./main -name "*.class" -type f -delete


# Compile the Java files
echo "Compiling the Java files..."
javac main/GameServer.java main/ClientFrame.java main/SnakeSprite.java main/util/Constants.java


# Start the GameServer
echo "Starting the GameServer..."
osascript -e 'tell app "Terminal" to do script "cd '$(pwd)'; java main.GameServer"' &

# Wait a bit to make sure the server starts
sleep 5

# Start two ClientFrames in separate tabs
echo "Starting the ClientFrame..."
osascript -e 'tell app "Terminal" to do script "cd '$(pwd)'; java main.ClientFrame"'
osascript -e 'tell app "Terminal" to do script "cd '$(pwd)'; java main.ClientFrame"'

# Print instruction to the user
echo "The GameServer and ClientFrames are running. Please check the terminal tabs."
