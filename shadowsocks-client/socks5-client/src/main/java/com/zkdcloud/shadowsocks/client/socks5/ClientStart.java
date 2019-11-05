package com.zkdcloud.shadowsocks.client.socks5;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import com.zkdcloud.shadowsocks.client.socks5.channelHandler.inbound.CryptInitInHandler;
import com.zkdcloud.shadowsocks.client.socks5.channelHandler.inbound.Socks5ServerDoorHandler;
import com.zkdcloud.shadowsocks.client.socks5.config.ClientConfig;
import com.zkdcloud.shadowsocks.common.cipher.CipherProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.internal.StringUtil;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * client Start
 *
 * @author zk
 * @since 2018/8/20
 */
public class ClientStart {
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

    private static void startupTCP() throws InterruptedException {
        clientBootstrap.group(bossLoopGroup, worksLoopGroup)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline()
                                .addLast("idle", new IdleStateHandler(0, 0, ClientConfig.clientConfig.getIdleTime(), TimeUnit.SECONDS))
                                .addLast("crypt-init", new CryptInitInHandler())
                                .addLast("socks5-door", new Socks5ServerDoorHandler());
                    }
                });
        InetSocketAddress localAddress = getLocalAddress(ClientConfig.clientConfig.getLocal());
        ChannelFuture channelFuture = clientBootstrap.bind(localAddress).sync();

        //start log
        logger.info("shadowsocks socks5 client [TCP] running at {}", localAddress.toString());
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
            // remote ip
            OPTIONS.addOption(Option.builder("s").longOpt("server_address").argName("ip:port").hasArg(true).type(String.class).desc("server connect address. e.g: ip:port").build());
            // remote password
            OPTIONS.addOption(Option.builder("p").longOpt("password").argName("password").hasArg(true).type(String.class).desc("server password").build());
            // remote encrypt method
            OPTIONS.addOption(Option.builder("m").longOpt("method").argName("methodName").hasArg(true).type(String.class).desc("encrypt method. support method: " + CipherProvider.getSupportCiphersNames()).build());
            // local address
            OPTIONS.addOption(Option.builder("c").longOpt("local_address").argName("ip:port").hasArg(true).type(String.class).desc("local expose address. e.g: 0.0.0.0:1080").build());

            // number of boss thread
            OPTIONS.addOption(Option.builder("bn").longOpt("boss_number").argName("proc*2").hasArg(true).type(Integer.class).desc("boss thread number").build());
            // number of workers thread
            OPTIONS.addOption(Option.builder("wn").longOpt("workers_number").argName("proc*2").hasArg(true).type(Integer.class).desc("workers thread number").build());
            // idle time
            OPTIONS.addOption(Option.builder("i").longOpt("idleTime").argName("600").hasArg(true).type(Integer.class).desc("idle time(second), default 600").build());
            // set log level
            OPTIONS.addOption(Option.builder("level").longOpt("log_level").argName("INFO").hasArg(true).type(String.class).desc("log level").build());
            // help
            OPTIONS.addOption("h", "usage help");
            OPTIONS.addOption("help", "usage full help");
            try {
                commandLine = commandLineParser.parse(OPTIONS, args);
            } catch (ParseException e) {
                logger.error(e.getMessage() + "\n" + getShortHelpString());
                System.exit(0);
            }
        }

        // init clientConfigure
        {
            if (commandLine.hasOption("h")) {
                logger.info("\n" + getShortHelpString());
                System.exit(1);
            }
            if (commandLine.hasOption("help")) {
                logger.info("\n" + getFullHelpString());
                System.exit(1);
            }

            // server address
            if (StringUtil.isNullOrEmpty(commandLine.getOptionValue("s"))) {
                logger.error("server address is required\n" + getShortHelpString());
                System.exit(1);
            }
            ClientConfig.clientConfig.setServer(commandLine.getOptionValue("s"));
            // server password
            if (StringUtil.isNullOrEmpty(commandLine.getOptionValue("p"))) {
                logger.error("server password is required\n" + getShortHelpString());
                System.exit(1);
            }
            ClientConfig.clientConfig.setPassword(commandLine.getOptionValue("p"));
            // encrypt method
            if (StringUtil.isNullOrEmpty(commandLine.getOptionValue("m"))) {
                ClientConfig.clientConfig.setMethod(commandLine.getOptionValue("m"));
                System.exit(1);
            }
            ClientConfig.clientConfig.setMethod(commandLine.getOptionValue("m"));
            // local address
            ClientConfig.clientConfig.setLocal(StringUtil.isNullOrEmpty(commandLine.getOptionValue("c")) ? "0.0.0.0:1080" : commandLine.getOptionValue("c"));
            // boss thread number
            ClientConfig.clientConfig.setBossThreadNumber(StringUtil.isNullOrEmpty(commandLine.getOptionValue("bn")) ? Runtime.getRuntime().availableProcessors() * 2 : Integer.valueOf(commandLine.getOptionValue("bn")));
            // works thread number
            ClientConfig.clientConfig.setWorkersThreadNumber(StringUtil.isNullOrEmpty(commandLine.getOptionValue("wn")) ? Runtime.getRuntime().availableProcessors() * 2 : Integer.valueOf(commandLine.getOptionValue("wn")));
            // idle time
            ClientConfig.clientConfig.setIdleTime(StringUtil.isNullOrEmpty(commandLine.getOptionValue("i")) ? 600 : Integer.valueOf(commandLine.getOptionValue("i")));
            // log level
            String levelName = commandLine.getOptionValue("level");
            if (StringUtil.isNullOrEmpty(levelName)) {
                Level level = Level.toLevel(levelName, Level.INFO);
                logger.info("set log level to " + level.toString());

                LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
                ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger("ROOT");
                rootLogger.setLevel(level);
                if (Level.toLevel(levelName).levelInt < Level.INFO.levelInt) {
                    PatternLayoutEncoder ple = new PatternLayoutEncoder();
                    ple.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");
                    ple.setContext(loggerContext);
                    ple.start();

                    ConsoleAppender consoleAppender = new ConsoleAppender();
                    consoleAppender.setEncoder(ple);
                    consoleAppender.setName("STDOUT");
                    consoleAppender.setContext(loggerContext);
                    consoleAppender.start();

                    rootLogger.detachAppender("STDOUT");
                    rootLogger.addAppender(consoleAppender);
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
        helpFormatter.setOptionComparator(null);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);

        Options shortOptions = new Options();
        shortOptions.addOption(OPTIONS.getOption("s"));
        shortOptions.addOption(OPTIONS.getOption("p"));
        shortOptions.addOption(OPTIONS.getOption("m"));
        shortOptions.addOption(OPTIONS.getOption("c"));
        shortOptions.addOption(OPTIONS.getOption("h"));
        shortOptions.addOption(OPTIONS.getOption("help"));

        helpFormatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH * 2, "java -jar socks5.jar -h", null,
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
        helpFormatter.setOptionComparator(null);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
        helpFormatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH * 2, "java -jar socks5.jar -help", null,
                OPTIONS, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
        printWriter.flush();
        String result = new String(byteArrayOutputStream.toByteArray());
        printWriter.close();
        return result;
    }

    private static InetSocketAddress getLocalAddress(String address) {
        if (!address.contains(":")) {
            throw new IllegalArgumentException("illegal server address: " + address + ", address format: ip:port");
        }
        String host = address.substring(0, address.indexOf(":"));
        int port = Integer.parseInt(address.substring(address.indexOf(":") + 1));
        return new InetSocketAddress(host, port);
    }
}
