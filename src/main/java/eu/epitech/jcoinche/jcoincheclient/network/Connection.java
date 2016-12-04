package eu.epitech.jcoinche.jcoincheclient.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;

/**
 * Created by noboud_n on 16/11/2016.
 */

public class Connection {

    static String HOST = null;
    static String PORT = null;
    private SslContext _secureSocket = null;
    private static EventLoopGroup _group = null;
    private Bootstrap _bootstrap = null;
    private static Channel _channel = null;

    public String get_host() {
        return HOST;
    }

    public String get_port() {
        return PORT;
    }

    public Bootstrap get_bootstrap() {
        return _bootstrap;
    }

    public static Channel get_channel() {
        return _channel;
    }

    public static EventLoopGroup get_group() {
        return _group;
    }

    public SslContext get_secureSocket() {
        return _secureSocket;
    }

    public void set_host(String host) {
        this.HOST = host;
    }

    public void set_port(String port) {
        this.PORT = port;
    }

    public void set_bootstrap(Bootstrap _bootstrap) {
        this._bootstrap = _bootstrap;
    }

    public void set_channel(Channel _channel) {
        this._channel = _channel;
    }

    public void set_group(EventLoopGroup _group) {
        this._group = _group;
    }

    public void set_secureSocket(SslContext _secureSocket) {
        this._secureSocket = _secureSocket;
    }

    public boolean requestHost(String host) {
        if (host == null || host.isEmpty()) {
            return false;
        }
        set_host(host.replaceAll("\\s", ""));
        return true;
    }

    public boolean requestPort(String port) {
        if (port == null || port.isEmpty()) {
            return false;
        }
        set_port(port.replaceAll("\\s", ""));
        return true;
    }

    public void connect() throws ConnectException {
        // Start the connection attempt.
        try {
            set_secureSocket(SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build());
            set_group(new NioEventLoopGroup());
            set_bootstrap(new Bootstrap());
            get_bootstrap().group(get_group())
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer(get_secureSocket()));
            set_channel(_bootstrap.connect(System.getProperty("host", get_host()), Integer.parseInt(System.getProperty("port", get_port()))).sync().channel());
        } catch (Exception e) {
            String error = new StringBuilder()
                            .append("Could not connect to host \"")
                            .append(HOST)
                            .append("\" and port \"")
                            .append(PORT)
                            .append("\". Please check them and retry later.")
                            .toString();
            throw new ConnectException(error);
        }
    }
}
