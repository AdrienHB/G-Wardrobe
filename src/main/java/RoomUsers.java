import gearth.protocol.HPacket;

import java.util.Vector;

public class RoomUsers {
    private static RoomUsers instance;
    private Vector<HabboPlayer> players;

    private RoomUsers(){
        this.players = new Vector<>();
    }

    public Vector<HabboPlayer> getPlayers(){
        return this.players;
    }

    public static RoomUsers getInstance(){
        if(instance == null) instance = new RoomUsers();
        return instance;
    }

    public void changeRoom(){
        this.players.clear();
    }

    public HabboPlayer getPlayerByIndex(int index){
        for(HabboPlayer player : this.players){
            if(player.getIndex() == index){
                return player;
            }
        }
        return null;
    }

    public void removePlayer(int index){
        if(index == Wardrobe.owner.getIndex()){
            this.changeRoom();
        }else{
            HabboPlayer player = getPlayerByIndex(index);
            if(player != null){
                this.players.remove(player);
            }
        }
    }

    //UpdateAvatar
    public void changeLook(HPacket packet){
        int index = packet.readInteger();
        String figure = packet.readString();
        String gender = packet.readString();
        HabboPlayer player = getPlayerByIndex(index);
        if(player != null){
            player.setFigure(figure);
            player.setGender(gender);
        }
    }

    //playersinroom
    public void parse(HPacket packet){
        short nbr = packet.readShort();
        for(short i =0; i < nbr; i++){
            String gender = "";
            packet.readLong(); //id
            String name = packet.readString();
            packet.readString();//motto
            String figure = packet.readString();
            int index = packet.readInteger();
            packet.readInteger();//X
            packet.readInteger();//y
            packet.readString();//Z
            packet.readInteger();
            int type = packet.readInteger();
            if(type == 1){ //PLAYER
                gender = packet.readString();
                packet.readInteger();packet.readInteger();packet.readInteger();
                packet.readString(); //Group name
                packet.readString();packet.readInteger();packet.readBoolean();
            }else if(type == 2){ //PET
                packet.readInteger();
                packet.readLong();//Owner id
                packet.readString();//Owner name
                packet.readInteger();packet.readBoolean();packet.readBoolean();packet.readBoolean();packet.readBoolean();packet.readBoolean();packet.readBoolean();packet.readInteger();packet.readString();
            }else if(type == 4){ //BOT
                gender = packet.readString();
                packet.readLong(); //Owner id
                packet.readString(); //Owner name
                short n = packet.readShort();
                for(short k = 0; k < n; k++){
                    packet.readShort();
                }
            }

            if(type == 1 || type == 4){
                if(name.equals(Wardrobe.owner.getName())){
                    Wardrobe.owner.setIndex(index);
                    this.players.add(Wardrobe.owner);

                }else{
                    this.players.add(new HabboPlayer(index, name, figure, gender));
                }
            }
        }
    }
}
