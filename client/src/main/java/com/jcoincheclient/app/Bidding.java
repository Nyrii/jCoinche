package com.jcoincheclient.app;

import com.jcoincheclient.protobuf.Game;
import com.jcoincheclient.protobuf.Game.Answer;
import io.netty.channel.ChannelFuture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.jcoincheclient.protobuf.Game.Answer.Type.BIDDING;

/**
 * Created by noboud_n on 21/11/2016.
 */
public class Bidding {

    public void sendError(String error) {
        Game.Answer answer = Game.Answer.newBuilder()
                            .setRequest(error)
                            .setCode(-1)
                            .setType(BIDDING)
                            .build();
        Connection.get_channel().writeAndFlush(answer);
        try {
            Connection.get_channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void biddingProcess() throws Exception {
        try {
            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String line = null;

            for (;;) {
                line = null;
                while (line.isEmpty() && line.toLowerCase() != "y" && line.toLowerCase() != "n") {
                    try {
                        System.out.println("Would you like to bet ? (y/n) : ");
                        line = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                        sendError("QUIT");
                        throw new Exception("System error : Could not get the input.");
                    }
                }

                switch (line) {

                    case "y":
                        break;

                    case "n":
                        break;

                }
//                }
//
//                if (line != null && !line.isEmpty() && line.trim().length() > 0) {
//                    // Sends the received line to the server.
//                    Game.Answer answer = Game.Answer.newBuilder()
//                            .setType(BIDDING)
//                            .setPlayer(Game.Player.newBuilder().setName(line + "\r\n").build())
//                            .build();
//                    lastWriteFuture = Connection.get_channel().writeAndFlush(answer);
//                    if (lastWriteFuture != null) {
//                        try {
//                            lastWriteFuture.sync();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                            sendError("QUIT");
//                            throw new Exception("Could not send the player's informations to the server.");
//                        }
//                    }
//                    break;
//                } else {
//                    System.out.println("Your name is invalid, please enter a new one : ");
//                }
//            }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError("QUIT");
            throw new Exception("Cannot get the player's informations.");
        }
    }


    public boolean bid() throws Exception {
        try {
            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String line = null;
            try {
                System.out.println("Choose an option (x as an integer to announce the value of your contract, \"CAPOT\", \"GENERALE\") :");
                line = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                sendError("QUIT");
                throw new Exception("System error : Could not get the input.");
            }
            if (line != null && !line.isEmpty()) {
                Game.Answer.Builder answer = Game.Answer.newBuilder();
                Game.Bidding.Builder bidding = Game.Bidding.newBuilder();

                bidding.setBid(true);


                if (line.toUpperCase().equals("CAPOT")) {
                    bidding.setContract(Game.Bidding.Contract.CAPOT);
                } else if (line.toUpperCase().equals("GENERALE")) {
                    bidding.setContract(Game.Bidding.Contract.GENERALE);
                } else {
                    try {
                        Integer amount = Integer.parseInt(line);
                        bidding.setAmount(amount);
                        bidding.setContract(Game.Bidding.Contract.AMOUNT);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
                System.out.println("Coucou");
                System.out.println(bidding);
            }
//                }
//
//                if (line != null && !line.isEmpty() && line.trim().length() > 0) {
//                    // Sends the received line to the server.
//                    Game.Answer answer = Game.Answer.newBuilder()
//                            .setType(BIDDING)
//                            .setPlayer(Game.Player.newBuilder().setName(line + "\r\n").build())
//                            .build();
//                    lastWriteFuture = Connection.get_channel().writeAndFlush(answer);
//                    if (lastWriteFuture != null) {
//                        try {
//                            lastWriteFuture.sync();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                            sendError("QUIT");
//                            throw new Exception("Could not send the player's informations to the server.");
//                        }
//                    }
//                    break;
//                } else {
//                    System.out.println("Your name is invalid, please enter a new one : ");
//                }
//            }

        } catch (Exception e) {
            e.printStackTrace();
            sendError("QUIT");
            throw new Exception("Cannot get the player's bidding wishes.");
        }
        return true;
    }



}
