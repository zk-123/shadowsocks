package com.zkdcloud.shadowsocks.client.socks5.menu;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import com.zkdcloud.shadowsocks.client.socks5.config.ClientConfig;
import com.zkdcloud.shadowsocks.common.cipher.CipherProvider;
import io.netty.util.internal.StringUtil;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * @author yuhao
 * @version 5.11.0
 * @since 2021年05月22日 01:08:00
 */
public class ClientHelper {

    private static final Logger logger = LoggerFactory.getLogger(ClientHelper.class);

    private static final Options OPTIONS = new Options();

    private static CommandLine commandLine;

    public static void useHelp(String[] args) {
        parseArgs(args);
        printHelpMessage();
    }

    private static void parseArgs(String[] args) {
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
            CommandLineParser commandLineParser = new DefaultParser();
            commandLine = commandLineParser.parse(OPTIONS, args);
        } catch (ParseException e) {
            logger.error(e.getMessage() + "\n" + getShortHelpString());
            System.exit(0);
        }
    }

    private static void printHelpMessage() {
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
        ClientConfig.clientConfig.setBossThreadNumber(StringUtil.isNullOrEmpty(commandLine.getOptionValue("bn")) ? Runtime.getRuntime().availableProcessors() * 2 : Integer.parseInt(commandLine.getOptionValue("bn")));
        // works thread number
        ClientConfig.clientConfig.setWorkersThreadNumber(StringUtil.isNullOrEmpty(commandLine.getOptionValue("wn")) ? Runtime.getRuntime().availableProcessors() * 2 : Integer.parseInt(commandLine.getOptionValue("wn")));
        // idle time
        ClientConfig.clientConfig.setIdleTime(StringUtil.isNullOrEmpty(commandLine.getOptionValue("i")) ? 600 : Integer.parseInt(commandLine.getOptionValue("i")));
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
                ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
                consoleAppender.setEncoder(ple);
                consoleAppender.setName("STDOUT");
                consoleAppender.setContext(loggerContext);
                consoleAppender.start();
                rootLogger.detachAppender("STDOUT");
                rootLogger.addAppender(consoleAppender);
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

        helpFormatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH * 2, "java -jar shadowsocks-socks.jar -h", null,
                shortOptions, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
        printWriter.flush();
        String result = byteArrayOutputStream.toString();
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
        helpFormatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH * 2, "java -jar shadowsocks-socks.jar -help", null,
                OPTIONS, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
        printWriter.flush();
        String result = byteArrayOutputStream.toString();
        printWriter.close();
        return result;
    }
}
