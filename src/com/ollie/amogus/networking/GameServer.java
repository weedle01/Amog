package com.ollie.amogus.networking;

import com.ollie.amogus.gameobjects.entities.MPCrewMate;
import com.ollie.amogus.main.Game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class GameServer extends Thread {

    private DatagramSocket socket;
    private Game g;
    private List<MPCrewMate> players = new ArrayList<>();

    public GameServer(Game g){

        this.g = g;

        try{
            socket = new DatagramSocket(1331);
        } catch (SocketException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            parsePacket(packet.getData(), packet.getAddress(), packet.getPort());

        }
    }

    private void parsePacket(byte[] data, InetAddress address, int port){

        String message = new String(data).trim();
        PacketTypes type = Packet.lookupPacket(message.substring(0, 2));

        Packet p = null;
        switch (type){
            default:
            case INVALID:
                break;
            case LOGIN:
                p = new LoginPacket(data);
                System.out.println("[" + address.getHostAddress() + ":" + port + "] "
                        + ((LoginPacket) p).getUsername() + " has connected...");
                MPCrewMate crew = new MPCrewMate(512, 512, ((LoginPacket) p).getUsername(), address, port);
                addConnection(crew, ((LoginPacket)p));
                break;

            case DISCONNECT:
                p = new DisconnectPacket(data);
                System.out.println("[" + address.getHostAddress() + ":" + port + "] "
                        + ((DisconnectPacket) p).getUsername() + " has left...");
                removeConnection((DisconnectPacket) p);
                break;

            case MOVE:
                p = new MovePacket(data);
                handleMove((MovePacket) p);
        }

    }

    public void addConnection(MPCrewMate crew, LoginPacket packet){

        boolean alreadyConnected = false;
        for(MPCrewMate c : players){

            if(crew.getUserName().equalsIgnoreCase(c.getUserName())) {
                if(c.getIp() == null)
                    c.setIp(crew.getIp());

                if (c.getPort() == -1)
                    c.setPort(crew.getPort());
                alreadyConnected = true;
            } else {

                sendData(packet.getData(), c.getIp(), c.getPort());

                packet = new LoginPacket(packet.getUsername(), packet.getX(), packet.getY());
                sendData(packet.getData(), crew.getIp(), crew.getPort());

            }
        }

        if(!alreadyConnected)
            this.players.add(crew);

    }

    private void removeConnection(DisconnectPacket p){
        players.remove(getCrewMPIndex(p.getUsername()));
        p.writeData(this);
    }

    public MPCrewMate getMPCrew(String username){
        for(MPCrewMate p : players){
            if(p.getUserName().equals(username))
                return p;
        }
        return null;
    }

    private int getCrewMPIndex(String username){

        int index = 0;
        for(MPCrewMate p : players){
            if(p.getUserName().equals(username))
                break;

            index++;
        }
        return index;
    }

    public void sendData(byte[] data, InetAddress address, int port){

        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        try{
            this.socket.send(packet);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendToAllClients(byte[] data){
        for(MPCrewMate crewMate : players){
            sendData(data, crewMate.getIp(), crewMate.getPort());
        }
    }

    private void handleMove(MovePacket p){
        if(getMPCrew(p.getUsername()) != null){
            int index = getCrewMPIndex(p.getUsername());
            MPCrewMate crew = players.get(index);
            crew.setNewX(p.getX());
            crew.setNewY(p.getY());
            crew.setMoving(p.isMoving());
            crew.changeDirection(p.getDirections());
            p.writeData(this);

        }
    }

    public List<MPCrewMate> getPlayers() {
        return players;
    }
}
