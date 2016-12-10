package eu.epitech.jcoinche.jcoincheserver.game;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Saursinet on 06/12/2016.
 */
public class Person {
    private ChannelHandlerContext ctx;
    private String name;
    private int pos;

    public Person (ChannelHandlerContext ctx, String name, int pos) {
        this.ctx = ctx;
        this.name = name;
        this.pos = pos;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPos() {
        return pos;
    }
}
