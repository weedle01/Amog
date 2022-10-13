package com.ollie.amogus.networking;

import com.ollie.amogus.gameobjects.entities.MPCrewMate;
import com.ollie.amogus.main.Game;

import java.io.IOException;
import java.net.*;

public class GameClient extends Thread {

    private InetAddress ipAddress;
    private DatagramSocket socket;
    private final Game g;

    public GameClient(Game g, String ip){
        this.g = g;

        try{
            socket = new DatagramSocket();
            ipAddress = InetAddress.getByName(ip);
        } catch (SocketException | UnknownHostException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true){
            //Constantly try recieve a new packet
            byte[] data = new byte[1024];
            DatagramPacket p = new DatagramPacket(data, data.length);
            try{
                socket.receive(p);
            } catch (IOException e){
                e.printStackTrace();
            }

            parsePacket(p.getData(), p.getAddress(), p.getPort());
        }
    }

    private void parsePacket(byte[] data, InetAddress address, int port){

        //Recieve the packet and work out what to do with it (ignore anything that isnt 00-002
        String msg = new String(data).trim();
        PacketTypes type = Packet.lookupPacket(msg.substring(0, 2));
        Packet p = null;

        switch (type){
            default:
            case INVALID:
                break;
            case LOGIN:
                p = new LoginPacket(data);
                handleLogin((LoginPacket) p, address, port);
                break;

            case DISCONNECT:
                p = new DisconnectPacket(data);
                System.out.println("[" + address.getHostAddress() + ":" + port + "] "
                        + ((DisconnectPacket) p).getUsername() + " has left the world...");
                break;

            case MOVE:
                p = new MovePacket(data);
                handleMove((MovePacket) p);
        }

    }

    //Send a packet from the client to the server
    public void sendData(byte[] data){
        DatagramPacket p = new DatagramPacket(data, data.length, ipAddress, 1331);
        try{
            socket.send(p);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //On login, add a new crewmate to the map on the client and post a message in the console.
    private void handleLogin(LoginPacket p, InetAddress address, int port){
        System.out.println("[" + address.getHostAddress() + ":" + port + "] " + p.getUsername()
                + " has joined the game...");
        MPCrewMate crew = new MPCrewMate(p.getX(), p.getY(), p.getUsername(), address, port);
        g.getMap().addCrewMate(crew);
    }

    //Move another player on everyone elses clients
    private void handleMove(MovePacket p){
        g.getMap().moveCrewmates(p.getUsername(), p.getX(), p.getY(), p.getDirections());
    }

    public Game getG() {
        return g;
    }

}
