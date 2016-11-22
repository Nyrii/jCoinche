package eu.epitech.jcoinche.jcoincheclient.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

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

    public void requestHostAndPort() throws ConnectException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String host = null;
        String port = null;
        try {
            System.out.println("Please type the server's host :");
            host = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConnectException("Could not get the host.");
        }
        try {
            System.out.println("Please type the server's port :");
            port = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConnectException("Could not get the port.");
        }
        if (host == null || port == null || host.isEmpty() || port.isEmpty()) {
            throw new ConnectException("Invalid port or/and host.");
        }
        set_host(host);
        set_port(port);
    }

    public void connect() throws ConnectException {
        try {
            _secureSocket = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } catch (SSLException e) {
            String error = new StringBuilder()
                    .append("Could not create a socket.")
                    .toString();
            throw new ConnectException(error);
        }
        _group = new NioEventLoopGroup();
        _bootstrap = new Bootstrap();
        _bootstrap.group(_group)
            .channel(NioSocketChannel.class)
            .handler(new ClientInitializer(_secureSocket));

        // Start the connection attempt.
        try {
            _channel = _bootstrap.connect(System.getProperty("host", HOST), Integer.parseInt(System.getProperty("port", PORT))).sync().channel();
        } catch (InterruptedException e) {
            String error = new StringBuilder()
                    .append("Connection interrupted. Could not create a channel to connect to the server.")
                    .toString();
            throw new ConnectException(error);
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
