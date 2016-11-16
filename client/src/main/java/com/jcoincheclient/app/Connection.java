package com.jcoincheclient.app;

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

    private String _host = null;
    private String _port = null;
    SslContext _secureSocket = null;
    EventLoopGroup _group = null;
    Bootstrap _bootstrap = null;
    Channel _channel = null;

    public String get_host() {
        return _host;
    }

    public String get_port() {
        return _port;
    }

    public Bootstrap get_bootstrap() {
        return _bootstrap;
    }

    public Channel get_channel() {
        return _channel;
    }

    public EventLoopGroup get_group() {
        return _group;
    }

    public SslContext get_secureSocket() {
        return _secureSocket;
    }

    public void set_host(String host) {
        this._host = host;
    }

    public void set_port(String port) {
        this._port = port;
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
        if (host == null || port == null) {
            throw new ConnectException("Invalid port or/and host.");
        }
        set_host(host);
        set_port(port);
    }

    public void connect() throws ConnectException {
        try {
            _secureSocket = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } catch (SSLException e) {
            e.printStackTrace();
        }
        _group = new NioEventLoopGroup();
        _bootstrap = new Bootstrap();
        _bootstrap.group(_group)
            .channel(NioSocketChannel.class)
            .handler(new ClientInitializer(_secureSocket));

        // Start the connection attempt.
        try {
            _channel = _bootstrap.connect(System.getProperty("host", _host), Integer.parseInt(System.getProperty("port", _port))).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
