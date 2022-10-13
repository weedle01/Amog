package com.ollie.amogus.networking;

public abstract class Packet {

    private byte packetId;

    public Packet(int packetId) {
        this.packetId = (byte) packetId;
    }

    public abstract void writeData(GameClient c);
    public abstract void writeData(GameServer e);

    public String readData(byte[] data){
        String msg = new String(data).trim();
        return msg.substring(2);
    }

    public abstract byte[] getData();

    public static PacketTypes lookupPacket(String packetID){

        try{
            return lookupPacket(Integer.parseInt(packetID));
        } catch (NumberFormatException e){
            return PacketTypes.INVALID;
        }

    }

    public static PacketTypes lookupPacket(int packetID){

        for(PacketTypes t : PacketTypes.values()){
            if(t.getPacketId() == packetID)
                return t;
        }

        return PacketTypes.INVALID;

    }

}
