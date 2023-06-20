package JAVARuntime;

import java.util.*;
import java.text.*;
import java.net.*;
import java.math.*;
import java.io.*;
import java.nio.*;

public class ServerConnection extends Component {
    
    private DatagramSocket socket;
    private InetAddress address;
    private String client_id;
    private long timestamp;
    private int ping;
    public int port;
    public String ip;
    
    private static int PING_PID = 0x00;
    private static int OPEN_CONNECTION_PID = 0x01;
    private static int UPDATE_POSITION_PID = 0x02; 
    private static int START_GAME_PID = 0x04;
    private static int CLOSE_CONNECTION_PID = 0x05;
    
    public ObjectFile player_object;
    public List<player> players = new ArrayList();

    @Override
    public void start()
    {
        bind_socket();
    }

    @Override
    public void repeat()
    {
        for(player player: players)
        {
            
            if(!player.active)
            {
                player.player_object.destroy();
                players.remove(player);
            }
            else if(player.player_object == null)
            {
                SpatialObject spatial = myObject.instantiate(player_object, player.position, player.rotation);
                spatial.setName(player.name);
                player.player_object = spatial;
            } else {
                player.player_object.position = player.position;
                player.player_object.rotation = player.rotation;
            }
        }
    }
    
    public void update_position(String client_id, Vector3 position, Quaternion rotation)
    {
        ByteBuffer buffer = ByteBuffer.allocate(35);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) UPDATE_POSITION_PID);
        buffer.put(alloc_vector3(position));
        buffer.put(alloc_vector3(rotation));
        buffer.put(client_id.getBytes());
        
        send_server(buffer.array());
    }
    
    public void open_connection(String client_id, String player_name, Vector3 position, Quaternion rotation)
    {
        ByteBuffer b = ByteBuffer.allocate(55);
        b.order(ByteOrder.LITTLE_ENDIAN);
        
        b.put((byte) OPEN_CONNECTION_PID);
        b.put(client_id.getBytes());
        b.put(alloc_name(player_name));
        b.put(alloc_vector3(position));
        b.put(alloc_vector3(rotation));
        
        send_server(b.array());
    }
    
    public void ping_server() {
        timestamp = System.currentTimeMillis();
        ByteBuffer buffer = ByteBuffer.allocate(9);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        buffer.put((byte) PING_PID);
        buffer.putLong(timestamp);
        
        send_server(buffer.array());
    }
    
    public void close_connection(String client_id)
    {
        if(socket == null)
        {
            bind_socket();
            
            ByteBuffer buffer = ByteBuffer.allocate(11);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.put((byte) CLOSE_CONNECTION_PID);
            buffer.put(client_id.getBytes());
            
            send_server(buffer.array());
        }
    }
    
    private byte[] alloc_vector3(Vector3 vector)
    {
        ByteBuffer buffer = ByteBuffer.allocate(12);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putFloat(vector.x);
        buffer.putFloat(vector.y);
        buffer.putFloat(vector.z);
        
        return buffer.array();
    }
    
    private byte[] alloc_vector3(Quaternion vector)
    {
        ByteBuffer buffer = ByteBuffer.allocate(12);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putFloat(vector.x);
        buffer.putFloat(vector.y);
        buffer.putFloat(vector.z);
        
        return buffer.array();
    }
    
    private byte[] alloc_name(String name)
    {
        ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(name.getBytes());
        
        return buffer.array();
    }
    
    private String read_str(ByteBuffer buffer, int size)
    {
        byte[] dst = new byte[size];
        
        for(int i = 0; i < size; i++)
        {
            dst[i] = buffer.get();
        }
        
        return new String(dst);
    }

    
    public void send_server(byte[] packet)
    {
        try
        {
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
    
    public byte[] read_server()
    {
        try
        {
            DatagramPacket packet = new DatagramPacket(new byte[256], 256);
            socket.receive(packet);
            
            return packet.getData();
        } catch(Exception e) {
            Console.log(e);
        }
        
        return null; 
    }
    
    public player get_player_by_id(String id)
    {
        for(player player: players)
        {
            if(player.client_id == id)
            {
                return player;
            }
        }
        
        return null;
    }
    
    
    private void bind_socket()
    {
        try
        {
            if(SaveGame.loadString("host-ip") == null) {
                SaveGame.saveString("host-ip", "localhost");
            }
            
            port = SaveGame.loadInt("host-port");
            
            if(port == 0) {
                SaveGame.saveInt("host-port", 19132);
            }
            
            address = InetAddress.getByName(SaveGame.loadString("host-ip"));
            ip = SaveGame.loadString("host-ip");
            port = SaveGame.loadInt("host-port");
            socket = new DatagramSocket();       
        } catch(Exception e) {
            Console.log(e);
        }
    }
    
    
    public void async_server_listener()
    {
        new AsyncTask(new AsyncRunnable()
        {
           public Object onBackground(Object input)
           {
               try
               {
                   while(true)
                   {
                       byte[] packet = read_server();
                       ByteBuffer buffer = ByteBuffer.wrap(packet);
                       buffer.order(ByteOrder.LITTLE_ENDIAN);
                       
                       byte pid = buffer.get();
                       
                       if(pid == PING_PID) {
                           ping = (int) (System.currentTimeMillis() - timestamp);
                       }
                       
                       if(pid == START_GAME_PID)
                       {
                           int players_amount = buffer.getInt();
                           
                           for(int i = 0; i < players_amount; i++)
                           {
                               float x = buffer.getFloat();
                               float y = buffer.getFloat();
                               float z = buffer.getFloat();
                               
                               float rx = buffer.getFloat();
                               float ry = buffer.getFloat();
                               float rz = buffer.getFloat();
                               
                               String client_id = read_str(buffer, 10);
                               String player_name = read_str(buffer, 20);
                               
                               player player = new player();
                               player.client_id = client_id;
                               player.name = player_name;
                               player.position = new Vector3(x, y, z);
                               player.rotation = new Quaternion(0, rx, ry, rz);
                               player.active = true;
                               
                               players.add(player);
                           }
                       }
                       
                       
                        if(pid == OPEN_CONNECTION_PID)
                        {
                            byte[] string = new byte[10];
                            for(int i = 0; i < 10; i++) {
                                string[i] = buffer.get();
                            }
                            
                            String client_id = new String(string);
                            
                            
                            string = new byte[20];
                            
                            for(int o = 0; o < 20; o++) {
                                string[o] = buffer.get();
                            }
                            
                            String player_name = new String(string);
                                                       
                            float x = buffer.getFloat();
                            float y = buffer.getFloat();
                            float z = buffer.getFloat();
                            
                            float rx = buffer.getFloat();
                            float ry = buffer.getFloat();
                            float rz = buffer.getFloat();
                                                       
                            player player = new player();
                            player.client_id = client_id;
                            player.name = player_name;
                            player.position = new Vector3(x, y, z);
                            player.rotation = new Quaternion(0, rx, ry, rz);
                            player.active = true;
                            Console.log(player_name + " add in server");
                            
                            players.add(player);
                        }
                        
                        if(pid == CLOSE_CONNECTION_PID)
                        {
                            byte[] string = new byte[10];
                            
                            for(int i = 0; i < 10; i++) {
                                string[i] = buffer.get();
                            }
                            
                            String client_id = new String(string);
                            player player = get_player_by_id(client_id);
                            player.active = false;
                        }
                       
                       if(pid == UPDATE_POSITION_PID)
                       {
                           float x = buffer.getFloat();
                           float y = buffer.getFloat();
                           float z = buffer.getFloat();
                           
                           float rx = buffer.getFloat();
                           float ry = buffer.getFloat();
                           float rz = buffer.getFloat();
                           
                           byte[] cid = new byte[10];
                           
                           for(int i = 24; i < 34; i++) {
                               cid[i - 24] = buffer.get();
                           }
                           
                           String client_id = new String(cid);
                           
                           player player = get_player_by_id(client_id);
                           player.position.x = x;
                           player.position.y = y;
                           player.position.z = z;
                           player.rotation.x = rx;
                           player.rotation.y = ry;
                           player.rotation.z = rz;
                       }
                   }
               } catch(Exception e) {
                   Console.log(e);
               }
               
               return null;
           }
           
           public void onEngine(Object result)
           {
               
           }
        });
    }
}

class player {
    public String client_id;
    public Vector3 position = new Vector3();
    public Quaternion rotation = new Quaternion();
    public String name;
    public SpatialObject player_object;
    public boolean active = false;
}




























