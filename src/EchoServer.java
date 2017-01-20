/**
 * Created by Marek on 2017-01-18.
 */
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EchoServer {
    public EchoServer( String bindAddr, int bindPort ) throws IOException {

        InetSocketAddress sockAddr = new InetSocketAddress(bindAddr, bindPort);
        System.out.println( "Socket creation.");
        //create a socket channel and bind to local bind address
        AsynchronousServerSocketChannel serverSock =  AsynchronousServerSocketChannel.open().bind(sockAddr);

        //start to accept the connection from client
        serverSock.accept(serverSock, new CompletionHandler<AsynchronousSocketChannel,AsynchronousServerSocketChannel >() {

            @Override
            public void completed(AsynchronousSocketChannel sockChannel, AsynchronousServerSocketChannel serverSock ) {
                //a connection is accepted, start to accept next connection
                serverSock.accept( serverSock, this );
                //start to read message from the client
                startRead( sockChannel );
                System.out.println( "Connection accepted");

            }

            @Override
            public void failed(Throwable exc, AsynchronousServerSocketChannel serverSock) {
                System.out.println( "fail to accept a connection");
            }

        } );

    }

    private void startRead( AsynchronousSocketChannel sockChannel ) {
        final ByteBuffer buf = ByteBuffer.allocate(4096);

        //read message from client
        sockChannel.read( buf, sockChannel, new CompletionHandler<Integer, AsynchronousSocketChannel >() {

            /**
             * some message is read from client, this callback will be called
             */
            @Override
            public void completed(Integer result, AsynchronousSocketChannel channel  ) {

                byte bytes[];
                bytes = buf.array();
                Charset cs = Charset.forName("UTF-8");
                String msg = new String(bytes, cs);
                buf.clear();
                StringDecoder sd = new StringDecoder();
                String messageTest = sd.decodeMessage(msg);
                if(messageTest != null)
                {
                    buf.put(messageTest.getBytes());
                    buf.flip();
                    // echo the message
                    startWrite( channel, buf );
                }
                //start to read next message again
                startRead( channel );
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel channel ) {
                System.out.println( "fail to read message from client");
            }
        });
    }

    private void startWrite( AsynchronousSocketChannel sockChannel, final ByteBuffer buf) {
        sockChannel.write(buf, sockChannel, new CompletionHandler<Integer, AsynchronousSocketChannel >() {

            @Override
            public void completed(Integer result, AsynchronousSocketChannel channel) {
                //finish to write message to client, nothing to do
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                //fail to write message to client
                System.out.println( "Fail to write message to client");
            }

        });
    }

    public static void main( String[] args ) {
        String defaultIP = "192.168.43.37";
        int port = 8989;
        System.out.println("Server ip: " + defaultIP + "/" + port);
        try {
            new EchoServer(defaultIP, port);
            for( ; ; ) {
                Thread.sleep(10*1000);
            }
        } catch (Exception ex) {
            Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}