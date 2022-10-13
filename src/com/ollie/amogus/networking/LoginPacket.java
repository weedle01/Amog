package com.ollie.amogus.networking;

public class LoginPacket extends Packet {

    private String username;
    private float x, y;

    public LoginPacket(byte[] data){
        super(00);

        String[] dataArray = readData(data).split(",");
        this.username = dataArray[0];
        this.x = Float.parseFloat(dataArray[1]);
        this.y = Float.parseFloat(dataArray[2]);

    }

    public LoginPacket(String username, float x, float y){
        super(00);

        this.username = username;
        this.x = x;
        this.y = y;
    }

    @Override
    public void writeData(GameClient c ) {

        c.sendData(getData());
    }

    @Override
    public void writeData(GameServer e) {
        e.sendToAllClients(getData());
    }

    @Override
    public byte[] getData() {
        return ("00" + this.username + "," + getX() + "," + getY()).getBytes();
    }

    public String getUsername() {
        return username;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}