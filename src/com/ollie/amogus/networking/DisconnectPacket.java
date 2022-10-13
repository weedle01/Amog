package com.ollie.amogus.networking;

public class DisconnectPacket extends Packet {

    private String username;

    public DisconnectPacket(byte[] data){
        super(01);
        this.username = readData(data);
    }

    public DisconnectPacket(String username){
        super(01);
        this.username = username;
    }

    @Override
    public void writeData(GameServer e) {
        e.sendToAllClients(getData());
    }

    @Override
    public void writeData(GameClient c) {
        c.sendData(getData());
    }

    @Override
    public byte[] getData() {
        return ("01" + this.username).getBytes();
    }

    public String getUsername() {
        return username;
    }
}
