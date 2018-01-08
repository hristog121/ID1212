package com.tictactoe.server;

import com.tictactoe.communication.HttpServerHandler;
import com.tictactoe.controller.GameController;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

/** A HTTP server which serves Web Socket requests at:
 *
 * http://localhost:9000/
 */
public class TicTacToeServer {

    private final GameController gameController;
    private final WebSocketServerHandshakerFactory wsFactory;

    public TicTacToeServer() {
        this.gameController = new GameController();
        this.wsFactory = new WebSocketServerHandshakerFactory(
                "ws://localhost:9000/websocket",
                null,
                false);
    }

    public static void main(String[] args) {
        TicTacToeServer ticTacToeServer = new TicTacToeServer();
        ticTacToeServer.run();
    }

    public void run() {
        // Event groups to listen for incoming requests and serve them - 
        // either establishing web socket connection or serve client requests
        // Boss group is used for accepting connections and establishing 
        // separate channels for each client
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // Worker group is used to handle the communication in the newly created
        // client channels
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // NioServerSocketChannel will create channels
                    .option(ChannelOption.SO_BACKLOG, 100) // Maximum queue length for incoming connection requests
                    .localAddress(9000)
                    .childOption(ChannelOption.TCP_NODELAY, true) // Enables sending each network packet no matter how small it is (No waiting for accumulation of packets)
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // Keep their connections open with keepalive packets (Ping - Pong)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // Initializes a channel
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast("httpResponseEncoder", new HttpResponseEncoder()); // Add encoder from HttpResponse to ByteBuf
                            ch.pipeline().addLast("httpRequestDecoder", new HttpRequestDecoder());  // Add decoded from ByteBuf to HttpResponse
                            ch.pipeline().addLast("httpServerHandler", new HttpServerHandler(gameController, wsFactory)); // Add custom defined HttpServerHandler
                        }
                    });

            // Start the server.
            ChannelFuture f = b.bind().sync(); // Creates a new channel and binds it to the specified address

            System.out.println("TicTacToe Server: Listening on port 9000");
            // Wait until the server socket is closed.
            f.channel().closeFuture().sync(); // To gracefully shut down the server
        } catch (InterruptedException ex) {
            System.out.println("An exception has occurred while operating server " + ex);
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

            try {
                // Wait until all threads are terminated.
                bossGroup.terminationFuture().sync();
                workerGroup.terminationFuture().sync();
            } catch (InterruptedException ex) {
                System.out.println("An exception has occurred while shutting down event loop groups " + ex);
            }
        }
    }
}
