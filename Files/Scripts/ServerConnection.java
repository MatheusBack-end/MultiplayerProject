package JAVARuntime;

import java.util.*;
import java.net.*;
import java.nio.*;

public class ServerConnection extends Component {
    
    private DatagramSocket socket;
    private InetAddress address;
    private String client_id;
    private long timestamp;
    private int ping;
    public int port;
    public ObjectFile player_object;
    public List<PlayerSession> players = new ArrayList();

    @Override
    public void start() {
        bind_socket();
    }

    @Override
    public void repeat() {
        for(PlayerSession player: players) {
            if(!player.active) {
                player.player_object.destroy();
                players.remove(player);
            } else if(player.player_object == null) {
                SpatialObject spatial = myObject.instantiate(player_object, player.position, player.rotation);
                spatial.setName(player.name);
                player.player_object = spatial;
            } else {
                player.player_object.position = player.position;
                player.player_object.rotation = player.rotation;
            }
        }
    }
    
    public void update_position(String client_id, Vector3 position, Quaternion rotation) {
        UpdatePositionPacket packet = new UpdatePositionPacket();
        packet.position = position;
        packet.rotation = rotation;
        packet.client_id = client_id;
        packet.encode();
        send_server(packet.buffer.array());
    }
    
    public void open_connection(String client_id, String player_name, Vector3 position, Quaternion rotation) {
        OpenSessionPacket packet = new OpenSessionPacket();
        packet.client_id = client_id;
        packet.player_name = player_name;
        packet.position = position;
        packet.rotation = rotation;
        packet.encode();
        send_server(packet.buffer.array());
    }
    
    public void ping_server() {
        PingPacket packet = new PingPacket();
        timestamp = System.currentTimeMillis();
        packet.timestamp = timestamp;
        packet.encode();
        send_server(packet.buffer.array());
    }
    
    public void close_connection(String client_id) {
        if(socket == null) {
            bind_socket();
        }
        
        CloseConnectionPacket packet = new CloseConnectionPacket();
        packet.client_id = client_id;
        packet.encode();
        send_server(packet.buffer.array());
    }
    
    public void send_server(byte[] packet) {
        try {
            socket.send(new DatagramPacket(packet,packet.length,address,port));
        } catch(Exception e) {
            Console.log(e);
        }
    }
    
    public int get_ping()
    {
        return ping;
    }
    
    public void set_client_id(String cid)
    {
        client_id = cid;
    }
    
    public byte[] read_server() {
        try {
            DatagramPacket packet = new DatagramPacket(new byte[256], 256);
            socket.receive(packet);
            
            return packet.getData();
        } catch(Exception e) {
            Console.log(e);
        }
        
        return null; 
    }
    
    public PlayerSession get_player_by_id(String id) {
        for(PlayerSession player: players) {
            if(player.client_id == id) {
                return player;
            }
        }
        
        return null;
    }
    
    
    private void bind_socket() {
        try {
            address = InetAddress.getByName(get_host_ip());
            port = get_host_port();
            socket = new DatagramSocket();       
        } catch(Exception e) {
            Console.log(e);
        }
    }
    
    private String get_host_ip() {
        String ip = SaveGame.loadString("host-ip");
        
        if(ip == null) {
            SaveGame.saveString("host-ip", "localhost");
            return "localhost";
        }
        
        return ip;
    }
    
    private int get_host_port() {
        int port = SaveGame.loadInt("host-ip");
        
        if(port == 0) {
            SaveGame.saveInt("host-ip", 19132);
            return 19132;
        }
        
        return port;
    }
    
    
    public void async_server_listener() {
        new AsyncTask(new AsyncRunnable() {
           
           public Object onBackground(Object input) {
               try {
                   while(true) {
                       byte[] pk = read_server();
                       ByteBuffer buffer = ByteBuffer.wrap(pk);
                       buffer.order(ByteOrder.LITTLE_ENDIAN);
                       
                       byte pid = buffer.get();
                       
                       if(pid == 0x00) {
                           ping = (int) (System.currentTimeMillis() - timestamp);
                       }
                       
                       if(pid == 0x01) {
                            OpenSessionPacket packet = new OpenSessionPacket();
                            packet.buffer = buffer;
                            packet.decode();
                            players.add(packet.player_session);
                       }
                       
                       if(pid == 0x02) {
                           UpdatePositionPacket packet = new UpdatePositionPacket();
                           packet.buffer = buffer;
                           packet.decode();
                           PlayerSession player = get_player_by_id(packet.client_id);
                           player.position = packet.position;
                           player.rotation = packet.rotation;
                       }
                       
                       if(pid == 0x04) {
                           StartGamePacket packet = new StartGamePacket();
                           packet.buffer = buffer;
                           packet.decode();
                           players = packet.players;
                       }
                        
                       if(pid == 0x05) {
                            CloseConnectionPacket packet = new CloseConnectionPacket();
                            packet.buffer = buffer;
                            packet.decode();
                            PlayerSession player = get_player_by_id(packet.client_id);
                            player.active = false;
                       }
                   }
               } catch(Exception e) {
                   Console.log(e);
               }
               
               return null;
           }
           
           public void onEngine(Object result){}
        });
    }
}