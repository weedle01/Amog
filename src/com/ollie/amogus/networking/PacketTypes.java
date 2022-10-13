package com.ollie.amogus.networking;

public enum PacketTypes {

    INVALID(-1),
    LOGIN(00),
    DISCONNECT(01),
    MOVE(02);

    private int packetId;

    PacketTypes(int packetId){
        this.packetId = packetId;
    }

    public int getPacketId() {
            return packetId;
        }

    public void setPacketId(int packetId) {
            this.packetId = packetId;
        }

}
