package com.zkdcloud.shadowsocks.server;

import com.zkdcloud.shadowsocks.server.chananelHandler.inbound.CryptInitInHandler;
import com.zkdcloud.shadowsocks.server.chananelHandler.inbound.DecodeCipherStreamInHandler;
import com.zkdcloud.shadowsocks.server.chananelHandler.inbound.TcpProxyInHandler;
import com.zkdcloud.shadowsocks.server.chananelHandler.outbound.EncodeCipherStreamOutHandler;
import com.zkdcloud.shadowsocks.server.config.ServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * server start
 *
 * @author zk
 * @since 2018/8/11
 */
public class ServerStart {
    /**
     * static logger
     */
    private static Logger logger = LoggerFactory.getLogger(ServerStart.class);

    /**
     * boosLoopGroup
     */
    private static EventLoopGroup bossLoopGroup = new NioEventLoopGroup();
    /**
     * worksLoopGroup
     */
    private static EventLoopGroup worksLoopGroup = new NioEventLoopGroup();
    /**
     * serverBootstrap
     */
    private static ServerBootstrap serverBootstrap = new ServerBootstrap();

    public static void main(String[] args) throws InterruptedException {
        startupTCP();
    }

    public static void startupTCP() throws InterruptedException {
        serverBootstrap.group(bossLoopGroup, worksLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new IdleStateHandler(0, 0, 1, TimeUnit.MINUTES))
                                .addLast(new CryptInitInHandler())
                                .addLast(new DecodeCipherStreamInHandler())
                                .addLast(new TcpProxyInHandler())
                                .addLast(new EncodeCipherStreamOutHandler());
                    }
                });
        String localIp = ServerConfig.serverConfig.getLocal_address();
        int localPort = ServerConfig.serverConfig.getLocal_port();

        InetSocketAddress localAddress = "0.0.0.0".equals(localIp) || "::0".equals(localIp) ? new InetSocketAddress(localPort) : new InetSocketAddress(localIp, localPort);

        ChannelFuture channelFuture = serverBootstrap.bind(localAddress).sync();
        logger.info("shadowsocks tcp server running at {}:{}", localAddress.getHostName(), localAddress.getPort());
        channelFuture.channel().closeFuture().sync();
    }


    private static Options OPTIONS = new Options();
    private static CommandLine commandLine;
    private static String HELP_STRING = null;
    /**
     * init args
     *
     * @param args args
     */
    private static void initCliArgs(String[] args) {
        // validate args
        {
            CommandLineParser commandLineParser = new DefaultParser();
            // help
            OPTIONS.addOption("help","usage help");
            // address
            OPTIONS.addOption(Option.builder("d").longOpt("address").argName("ip").required(false).type(String.class).desc("address bind").build());
            // port
            OPTIONS.addOption(Option.builder("P").longOpt("port").hasArg(true).type(Long.class).desc("port bind").build());
            // password
            OPTIONS.addOption(Option.builder("p").longOpt("password").required().hasArg(true).type(String.class).desc("password of ssserver").build());
            // method
            OPTIONS.addOption(Option.builder("m").longOpt("method").required().hasArg(true).type(String.class).desc("Encryption method").build());
            try {
                commandLine = commandLineParser.parse(OPTIONS, args);
            } catch (ParseException e) {
                logger.error(e.getMessage() + "\n" + getHelpString());
                System.exit(0);
            }
        }

        // init serverConfigure
        {
            if(commandLine.hasOption("help")){
                logger.error("\n" + getHelpString());
                System.exit(1);
            }

            // address
            String hostAddress = commandLine.getOptionValue("h") == null || "".equals(commandLine.getOptionValue("h")) ? "0.0.0.0" : commandLine.getOptionValue("h");
            ServerConfig.serverConfig.setLocal_address(hostAddress);
            // port
            String portOptionValue = commandLine.getOptionValue("P");
            short port = portOptionValue == null || "".equals(portOptionValue) ? 1080 : Short.parseShort(portOptionValue);
            ServerConfig.serverConfig.setLocal_port(port);
            // password
            ServerConfig.serverConfig.setPassword(commandLine.getOptionValue("p"));
            // method
            ServerConfig.serverConfig.setMethod(commandLine.getOptionValue("m"));
        }

    }

    /**
     * get string of help usage
     *
     * @return help string
     */
    private static String getHelpString() {
        if (HELP_STRING == null) {
            HelpFormatter helpFormatter = new HelpFormatter();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
            helpFormatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH, "ss -help", null,
                    OPTIONS, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
            printWriter.flush();
            HELP_STRING = new String(byteArrayOutputStream.toByteArray());
            printWriter.close();
        }
        return HELP_STRING;
    }
}
