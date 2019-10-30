package com.zkdcloud.shadowsocks.server;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.zkdcloud.shadowsocks.server.chananelHandler.ExceptionDuplexHandler;
import com.zkdcloud.shadowsocks.server.chananelHandler.inbound.CryptInitInHandler;
import com.zkdcloud.shadowsocks.server.chananelHandler.inbound.DecodeSSHandler;
import com.zkdcloud.shadowsocks.server.chananelHandler.inbound.TcpProxyInHandler;
import com.zkdcloud.shadowsocks.server.chananelHandler.outbound.EncodeSSOutHandler;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.zkdcloud.shadowsocks.server.config.ServerContextConstant.DEFAULT_IDLE_TIMEOUT_SECOND;

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
    private static EventLoopGroup bossLoopGroup = new NioEventLoopGroup(ServerConfig.serverConfig.getBossThreadNumber());
    /**
     * worksLoopGroup
     */
    private static EventLoopGroup worksLoopGroup = new NioEventLoopGroup(ServerConfig.serverConfig.getWorkersThreadNumber());
    /**
     * serverBootstrap
     */
    private static ServerBootstrap serverBootstrap = new ServerBootstrap();

    public static void main(String[] args) throws InterruptedException {
        initCliArgs(args);
        startupTCP();
    }

    private static void startupTCP() throws InterruptedException {
        serverBootstrap.group(bossLoopGroup, worksLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    protected void initChannel(Channel ch) {
                        ch.pipeline()
                                .addLast(new IdleStateHandler(ServerConfig.serverConfig.getCriTime(), ServerConfig.serverConfig.getCwiTime(),
                                        ServerConfig.serverConfig.getCaiTime(), TimeUnit.SECONDS))
                                .addLast(new CryptInitInHandler())
                                .addLast(new DecodeSSHandler())
                                .addLast(new TcpProxyInHandler())
                                .addLast(new EncodeSSOutHandler())
                                .addLast(new ExceptionDuplexHandler());
                    }
                });
        InetSocketAddress bindAddress = getAddress(ServerConfig.serverConfig.getLocalAddress());

        ChannelFuture channelFuture = serverBootstrap.bind(bindAddress).sync();
        logger.info("ss server [tcp] running at {}", bindAddress.toString());
        channelFuture.channel().closeFuture().sync();
    }


    private static Options OPTIONS = new Options();
    private static CommandLine commandLine;

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
            OPTIONS.addOption("h", "usage help");
            OPTIONS.addOption("help", "usage full help");
            // address and port
            OPTIONS.addOption(Option.builder("s").longOpt("address").hasArg(true).type(String.class).desc("address bind").build());
            // password
            OPTIONS.addOption(Option.builder("p").longOpt("password").hasArg(true).type(String.class).desc("password of ssserver").build());
            // method
            OPTIONS.addOption(Option.builder("m").longOpt("method").hasArg(true).type(String.class).desc("encrypt method").build());

            // number of boss thread
            OPTIONS.addOption(Option.builder("bn").longOpt("boss_number").hasArg(true).type(Integer.class).desc("boss thread number").build());
            // number of workers thread
            OPTIONS.addOption(Option.builder("wn").longOpt("workers_number").hasArg(true).type(Integer.class).desc("workers thread number").build());
            // client readIdle time(second)
            OPTIONS.addOption(Option.builder("cri").longOpt("client_read_idle").hasArg(true).type(Long.class).desc("client readIdle time(second)").build());
            // client writeIdle time(second)
            OPTIONS.addOption(Option.builder("cwi").longOpt("client_write_idle").hasArg(true).type(Long.class).desc("client writeIdle time(second)").build());
            // client allIdle time(second)
            OPTIONS.addOption(Option.builder("cai").longOpt("client_all_idle").hasArg(true).type(Long.class).desc("client allIdle time(second)").build());
            // remote readIdle time(second)
            OPTIONS.addOption(Option.builder("rri").longOpt("remote_read_idle").hasArg(true).type(Long.class).desc("remote readIdle time(second)").build());
            // remote writeIdle time(second)
            OPTIONS.addOption(Option.builder("rwi").longOpt("remote_write_idle").hasArg(true).type(Long.class).desc("remote writeIdle time(second)").build());
            // remote allIdle time(second)
            OPTIONS.addOption(Option.builder("rai").longOpt("remote_all_idle").hasArg(true).type(Long.class).desc("remote allIdle time(second)").build());

            // set log level
            OPTIONS.addOption(Option.builder("level").longOpt("log_level").hasArg(true).type(String.class).desc("log level").build());
            try {
                commandLine = commandLineParser.parse(OPTIONS, args);
            } catch (ParseException e) {
                logger.error(e.getMessage() + "\n" + getShortHelpString());
                System.exit(0);
            }
        }

        // init serverConfigure
        {
            if (commandLine.hasOption("h")) {
                logger.info("\n" + getShortHelpString());
                System.exit(1);
            }
            if (commandLine.hasOption("help")) {
                logger.info("\n" + getFullHelpString());
                System.exit(1);
            }

            // address
            if (commandLine.getOptionValue("s") == null || "".equals(commandLine.getOptionValue("s"))) {
                logger.info("server address is required\n" + getShortHelpString());
                System.exit(1);
            }
            ServerConfig.serverConfig.setLocalAddress(commandLine.getOptionValue("s"));
            // password
            if (commandLine.getOptionValue("p") == null || "".equals(commandLine.getOptionValue("p"))) {
                logger.info("server password is required\n" + getShortHelpString());
                System.exit(1);
            }
            ServerConfig.serverConfig.setPassword(commandLine.getOptionValue("p"));
            // method
            if (commandLine.getOptionValue("m") == null || "".equals(commandLine.getOptionValue("m"))) {
                logger.info("method is required\n" + getShortHelpString());
                System.exit(1);
            }
            ServerConfig.serverConfig.setMethod(commandLine.getOptionValue("m"));

            // boss thread number
            String bossThreadNumber = commandLine.getOptionValue("bn") == null || "".equals(commandLine.getOptionValue("bn")) ? String.valueOf(Runtime.getRuntime().availableProcessors() * 2) : commandLine.getOptionValue("bn");
            ServerConfig.serverConfig.setBossThreadNumber(Integer.parseInt(bossThreadNumber));
            // workers thread number
            String workersThreadNumber = commandLine.getOptionValue("wn") == null || "".equals(commandLine.getOptionValue("wn")) ? String.valueOf(Runtime.getRuntime().availableProcessors() * 2) : commandLine.getOptionValue("wn");
            ServerConfig.serverConfig.setWorkersThreadNumber(Integer.parseInt(workersThreadNumber));
            // client readIdle time(second)
            String clientReadIdleTime = commandLine.getOptionValue("cri") == null || "".equals(commandLine.getOptionValue("cri")) ? String.valueOf(DEFAULT_IDLE_TIMEOUT_SECOND) : commandLine.getOptionValue("cri");
            ServerConfig.serverConfig.setCriTime(Long.valueOf(clientReadIdleTime));
            // client writeIdle time(second)
            String clientWriteIdleTime = commandLine.getOptionValue("cwi") == null || "".equals(commandLine.getOptionValue("cwi")) ? String.valueOf(DEFAULT_IDLE_TIMEOUT_SECOND) : commandLine.getOptionValue("cwi");
            ServerConfig.serverConfig.setCwiTime(Long.valueOf(clientWriteIdleTime));
            // client allIdle time(second)
            String clientAllIdleTime = commandLine.getOptionValue("cai") == null || "".equals(commandLine.getOptionValue("cai")) ? String.valueOf(0) : commandLine.getOptionValue("cai");
            ServerConfig.serverConfig.setCaiTime(Long.valueOf(clientAllIdleTime));
            // remote readIdle time(second)
            String remoteReadIdleTime = commandLine.getOptionValue("rri") == null || "".equals(commandLine.getOptionValue("rri")) ? String.valueOf(DEFAULT_IDLE_TIMEOUT_SECOND) : commandLine.getOptionValue("rri");
            ServerConfig.serverConfig.setRriTime(Long.valueOf(remoteReadIdleTime));
            // remote writeIdle time(second)
            String remoteWriteIdleTime = commandLine.getOptionValue("rwi") == null || "".equals(commandLine.getOptionValue("rwi")) ? String.valueOf(DEFAULT_IDLE_TIMEOUT_SECOND) : commandLine.getOptionValue("rwi");
            ServerConfig.serverConfig.setRwiTime(Long.valueOf(remoteWriteIdleTime));
            // remote allIdle time(second)
            String remoteAllIdleTime = commandLine.getOptionValue("rai") == null || "".equals(commandLine.getOptionValue("rai")) ? String.valueOf(0) : commandLine.getOptionValue("rai");
            ServerConfig.serverConfig.setRaiTime(Long.valueOf(remoteAllIdleTime));

            String levelName = commandLine.getOptionValue("level");
            if (levelName != null && !"".equals(levelName)) {
                Level level = Level.toLevel(levelName, Level.INFO);
                logger.info("set log level to " + level.toString());

                LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
                List<ch.qos.logback.classic.Logger> loggerList = loggerContext.getLoggerList();
                for (ch.qos.logback.classic.Logger logger1 : loggerList) {
                    logger1.setLevel(level);
                }
            }
        }

    }

    /**
     * get string of short  help usage
     *
     * @return help string
     */
    private static String getShortHelpString() {
        HelpFormatter helpFormatter = new HelpFormatter();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);

        Options shortOptions = new Options();
        shortOptions.addOption(OPTIONS.getOption("s"));
        shortOptions.addOption(OPTIONS.getOption("p"));
        shortOptions.addOption(OPTIONS.getOption("m"));
        shortOptions.addOption(OPTIONS.getOption("h"));
        shortOptions.addOption(OPTIONS.getOption("help"));

        helpFormatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH, "java -jar shadowsocks.jar -h", null,
                shortOptions, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
        printWriter.flush();
        String result = new String(byteArrayOutputStream.toByteArray());
        printWriter.close();
        return result;
    }

    /**
     * get string of help usage
     *
     * @return help string
     */
    private static String getFullHelpString() {
        HelpFormatter helpFormatter = new HelpFormatter();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
        helpFormatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH, "java -jar shadowsocks.jar -help", null,
                OPTIONS, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
        printWriter.flush();
        String result = new String(byteArrayOutputStream.toByteArray());
        printWriter.close();
        return result;
    }

    private static InetSocketAddress getAddress(String address) {
        if (!address.contains(":")) {
            throw new IllegalArgumentException("illegal address: " + address);
        }
        String host = address.substring(0, address.indexOf(":"));
        int port = Integer.parseInt(address.substring(address.indexOf(":") + 1));
        return new InetSocketAddress(host, port);
    }
}
