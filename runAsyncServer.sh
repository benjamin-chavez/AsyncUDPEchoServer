#!/bin/sh

# chmod 700 ./runAsyncServer.sh

javac -d bin src/AsyncUDPEchoServer.java
javac -d bin src/AsyncUDPEchoClient.java


x-terminal-emulator -e java -cp bin AsyncUDPEchoServer
x-terminal-emulator -e java -cp bin AsyncUDPEchoClient
