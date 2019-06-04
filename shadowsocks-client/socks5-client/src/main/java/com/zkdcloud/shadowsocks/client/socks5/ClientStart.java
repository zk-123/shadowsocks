package com.zkdcloud.shadowsocks.client.socks5;

import com.zkdcloud.shadowsocks.client.socks5.channelHandler.inbound.CryptInitInHandler;
import com.zkdcloud.shadowsocks.client.socks5.channelHandler.inbound.Socks5ServerDoorHandler;
import com.zkdcloud.shadowsocks.client.socks5.config.ClientConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * client Start
 *
 * @author zk
 * @since 2018/8/20
 */
public class ClientStart{
    /**
     * static logger
     */
    private static Logger logger = LoggerFactory.getLogger(ClientStart.class);

    /**
     * boosLoopGroup
     */
    private static EventLoopGroup bossLoopGroup = new NioEventLoopGroup(1);
    /**
     * worksLoopGroup
     */
    private static EventLoopGroup worksLoopGroup = new NioEventLoopGroup(1);
    /**
     * clientBootstrap
     */
    private static ServerBootstrap clientBootstrap = new ServerBootstrap();

    public static void main(String[] args) throws InterruptedException {
        initCliArgs(args);
        startupTCP();
    }

    public static void startupTCP() throws InterruptedException {
        clientBootstrap.group(bossLoopGroup, worksLoopGroup)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast("idle", new IdleStateHandler(20, 20, 0, TimeUnit.MINUTES))
                                .addLast("crypt-init",new CryptInitInHandler())
                                .addLast("socks5-door", new Socks5ServerDoorHandler());
                    }
                });
        int port = ClientConfig.clientConfig.getLocal_port();
        ChannelFuture channelFuture = clientBootstrap.bind(port).sync();

        //start log
        logger.info("shadowsocks socks5 client [TCP] running at {}", port);
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
            // remote ip
            OPTIONS.addOption(Option.builder("s").longOpt("server").argName("ip").required(true).type(String.class).desc("remote ip").build());
            // remote port
            OPTIONS.addOption(Option.builder("P").longOpt("port").hasArg(true).type(Integer.class).desc("remote port").build());
            // remote password
            OPTIONS.addOption(Option.builder("p").longOpt("password").required().hasArg(true).type(String.class).desc("remote secret key").build());
            // remote encrypt method
            OPTIONS.addOption(Option.builder("m").longOpt("method").required().hasArg(true).type(String.class).desc("encrypt method").build());
            // local port
            OPTIONS.addOption(Option.builder("l").longOpt("local_port").required().hasArg(true).type(String.class).desc("local expose port").build());
            try {
                commandLine = commandLineParser.parse(OPTIONS, args);
            } catch (ParseException e) {
                logger.error(e.getMessage() + "\n" + getHelpString());
                System.exit(0);
            }
        }

        // init clientConfigure
        {
            if(commandLine.hasOption("help")){
                logger.error("\n" + getHelpString());
                System.exit(1);
            }

            // remote ip
            ClientConfig.clientConfig.setServer(commandLine.getOptionValue("s"));
            // remote port
            ClientConfig.clientConfig.setServer_port(Integer.parseInt(commandLine.getOptionValue("P")));
            // remote secret key
            ClientConfig.clientConfig.setPassword(commandLine.getOptionValue("p"));
            // encrypt key
            ClientConfig.clientConfig.setMethod(commandLine.getOptionValue("m"));
            // method
            ClientConfig.clientConfig.setLocal_port(Integer.parseInt(commandLine.getOptionValue("l")));
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
            helpFormatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH, "java -jar shadowsocks-socks5-client-xxx.jar -help", null,
                    OPTIONS, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
            printWriter.flush();
            HELP_STRING = new String(byteArrayOutputStream.toByteArray());
            printWriter.close();
        }
        return HELP_STRING;
    }
}
