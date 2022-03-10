import java.io.*;
import java.nio.*;
import java.nio.channels.DatagramChannel;
import java.net.*;


public class AsyncUDPEchoServer {
  public static void main(String[] args) {
    int port = 9000;
    System.out.println("UDP Echo Server Started");

    // byte[] buffer = new byte[bufferSize];
    try (DatagramChannel channel = DatagramChannel.open()) {
      DatagramSocket socket = channel.socket();
      SocketAddress address = new InetSocketAddress(port);
      socket.bind(address);
      ByteBuffer buffer = ByteBuffer.allocateDirect(65507);

      while (true) {
        // Get Message
        SocketAddress client = channel.receive(buffer);
        buffer.flip();

        // Display Message
        buffer.mark();
        StringBuilder message = new StringBuilder();
        while (buffer.hasRemaining()) {
          message.append((char) buffer.get());
        }
        System.out.println("Received: [ " + message.toString().trim() + "]");
        buffer.reset();
        try { Thread.sleep(5000); } catch (Exception e) {}

        // Return Message
        channel.send(buffer, client);
        System.out.println("Sent: [ " + message.toString().trim() + " ]");
        buffer.clear();
      }

    } catch (Exception e) {
      System.out.println("Error: " + e);
    }
  }
}
