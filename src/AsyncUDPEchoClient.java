import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.*;

public class AsyncUDPEchoClient {
  public volatile static boolean flag = true;

  public static void main(String[] args) throws IOException {
    System.out.println("UDP Echo Client Started");
    SocketAddress remote = new InetSocketAddress("127.0.0.1", 9000);
    DatagramChannel channel = DatagramChannel.open();
    channel.connect(remote);

    while (true) {
      getInputAndSendToServer(channel); // SENDER
      getResponseFromServer(channel);   // RECEIVER
    }
  }

  /*
   * SENDER
   */
  public static void getInputAndSendToServer(DatagramChannel channel) throws IOException {
    // Get and process input from user to send to server
    System.out.print("> ");
    ByteBuffer bufferIn = ByteBuffer.allocate(256);
    ReadableByteChannel in = Channels.newChannel(System.in);
    in.read(bufferIn);
    bufferIn.flip();

    StringBuilder messageIn = new StringBuilder();
    while (bufferIn.hasRemaining()) {
      char s = ((char) bufferIn.get());
      messageIn.append(s);
    }

    String message = messageIn.toString();
    ByteBuffer buffer = ByteBuffer.allocate(256);
    buffer.put(message.getBytes());
    buffer.flip();

    // Send user input to server
    channel.write(buffer);
    buffer.clear();
    System.out.println("Sent: [ " + message.trim() + " ]");
  }


  /*
   * RECEIVER
   */
  public static void getResponseFromServer(DatagramChannel channel) throws IOException {
    // Call our Addition class to begin performing work asynchronously
    new Addition().start();

    // Process response from server
    ByteBuffer buffer = ByteBuffer.allocate(256);
    buffer.clear();
    channel.read(buffer);
    buffer.flip();

    // Set sentinal flag to stop the addition program/thread
    flag = false;

    // Continue processing response from server
    StringBuilder receivedMsg = new StringBuilder();
    while (buffer.hasRemaining()) {
      char s = ((char) buffer.get());
      receivedMsg.append(s);
    }

    // Wait addition thread to finish its final iteration
    while (flag == false) {
     try { Thread.sleep(1000); } catch (Exception e) {}
    }

    System.out.print("Received: [ " + receivedMsg.toString().trim() + " ]\n\n");
  }
}


/*
 * Addition Class:
 * Takes two numbers from user and returns the sum.
 * The purpose of this function is just to prove that we can do work
 * asynchronously while we are waiting for a response from the server.
 */
class Addition extends Thread {

  public void run() {
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String nums;

    try {
      do {
        System.out.print("Enter two numbers (ex:3 7): ");
        System.out.flush();
        nums = in.readLine();
        int num1, num2, sum;
        try {
          num1 = Integer.parseInt(nums.split(" ")[0]);
          num2 = Integer.parseInt(nums.split(" ")[1]);
          sum = num1 + num2;
          System.out.println(num1 + " + " + num2 + " = " + sum);
        } catch (Exception e) {
          System.out.println("Invalid number entry. Please try Again.");
        }
      } while (AsyncUDPEchoClient.flag);

    } catch (IOException x) {
      x.printStackTrace();
    }
    AsyncUDPEchoClient.flag = true;
  }
}
