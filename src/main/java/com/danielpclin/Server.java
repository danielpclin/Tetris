package com.danielpclin;

import com.danielpclin.helpers.Broadcastable;

import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class Server implements Runnable, Broadcastable {
    private final int port;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ByteBuffer buf = ByteBuffer.allocate(256);
    private Function<String, String> messageFunction;
    private InetSocketAddress serverAddress;

    Server(int port) throws IOException {
        this(port, (message)->message);
    }

    Server(int port, Function<String, String> function) throws IOException {
        this.port = port;
        this.messageFunction = function;
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.socket().bind(new InetSocketAddress(port));
        this.serverSocketChannel.configureBlocking(false);
        this.selector = Selector.open();
        this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    Server() throws IOException {
        this(12000, (message)->message);
    }

    Server(Function<String, String> function) throws IOException {
        this(12000, function);
    }

    Server(Runnable runnable) throws IOException {
        this(12000, (message)->message);
    }

    @Override
    public void run() {
        try {
            System.out.println("Server starting on port " + this.port);

            Iterator<SelectionKey> iter;
            SelectionKey key;
            while(this.serverSocketChannel.isOpen()) {
                selector.select();
                iter=this.selector.selectedKeys().iterator();
                while(iter.hasNext()) {
                    key = iter.next();
                    iter.remove();

                    if(key.isAcceptable()) this.handleAccept(key);
                    if(key.isReadable()) this.handleRead(key);
                }
            }
        } catch(IOException e) {
            System.out.println("IOException, server of port " + this.port + " terminating. Stack trace:");
            e.printStackTrace();
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
        String address = sc.socket().getInetAddress().getHostAddress() + ":" + sc.socket().getPort();
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ, address);
        messageFunction.apply("EST- [" + address + "]");
        System.out.println("accepted connection from: " + address);
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel ch = (SocketChannel) key.channel();
        StringBuilder sb = new StringBuilder();

        buf.clear();
        int read;
        try {
            while( (read = ch.read(buf)) > 0 ) {
                buf.flip();
                byte[] bytes = new byte[buf.limit()];
                buf.get(bytes);
                sb.append(new String(bytes));
                buf.clear();
            }
        } catch (Exception e) {
            key.cancel();
            e.printStackTrace();
            read = -1;
        }
        String msg;
        if(read<0) {
            msg = key.attachment() + " left connection.\n";
            ch.close();
            messageFunction.apply("DSC- [" + key.attachment() + "]");
            System.out.println(msg);
        } else {
            msg = sb.toString();
            broadcast(messageFunction.apply(msg));
        }
    }

    @Override
    public void broadcast(String msg) throws IOException {
        ByteBuffer msgBuf = ByteBuffer.wrap(msg.getBytes());
        for(SelectionKey key : selector.keys()) {
            if(key.isValid() && key.channel() instanceof SocketChannel) {
                SocketChannel sch=(SocketChannel) key.channel();
                sch.write(msgBuf);
                msgBuf.rewind();
            }
        }
    }

    public int getPort() {
        return port;
    }
}