package com.danielpclin;

import com.danielpclin.helpers.Broadcastable;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class Client implements Runnable, Broadcastable {

    private String serverName;
    private int serverPort;
    private Socket clientSocket;
    private Consumer<String> messageFunction;

    public Client(String name, int port, Consumer<String> function) {
        serverName = name;
        serverPort = port;
        messageFunction = function;
    }

    public Client(String name, int port) {
        this(name, port, (msg)->{});
    }

    public Client(Consumer<String> function) {
        this("127.0.0.1", 12000, function);
    }

    public Client(int port, Consumer<String> function) {
        this("127.0.0.1", port, function);
    }

    @Override
    public void run(){
        try {
            SocketAddress severSocketAddress = new InetSocketAddress(serverName, serverPort);
            clientSocket = new Socket();
            //connect to server in the specific timeout 3000 ms
            System.out.println("Connecting to server " + serverName + ":" + serverPort);
            clientSocket.connect(severSocketAddress, 3000);
            System.out.println(clientSocket);

            //get client address and port at local host
            InetSocketAddress socketAddress = (InetSocketAddress)clientSocket.getLocalSocketAddress();
            String clientAddress = socketAddress.getAddress().getHostAddress();
            int clientPort = socketAddress.getPort();
            System.out.println("Client " + clientAddress + ":" + clientPort);
            System.out.println("Connected to server " + serverName + ":" + serverPort);
            try {
                InputStream inputStream = clientSocket.getInputStream();
                byte[] buf = new byte[1024];
                int length = inputStream.read(buf);
                while(length > 0) {
                    String message = new String(buf, 0, length);
                    messageFunction.accept(message);
                    length = inputStream.read(buf);
                }
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch(IOException e) {
                System.out.println("Server closed connection (unexpectedly) - READ");
            }
        } catch (ConnectException e) {
            System.out.println("Connection failed");
        } catch (IOException e) {
             e.printStackTrace();
        }
    }

    @Override
    public void broadcast(String message) throws IOException{
        if (clientSocket!=null) {
                OutputStream outputStream = clientSocket.getOutputStream();
                outputStream.write(message.getBytes());
        }
    }

    public String getClientAddress(){
        InetSocketAddress socketAddress = (InetSocketAddress)clientSocket.getLocalSocketAddress();
        String clientAddress = socketAddress.getAddress().getHostAddress();
        int clientPort = socketAddress.getPort();
        return clientAddress + ":" + clientPort;
    }
}