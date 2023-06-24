package JAVARuntime;

import java.util.*;
import java.nio.*;

public class StartGamePacket {
    
    private static int PID = 0x04;
    public ByteBuffer buffer;
    public List<PlayerSession> players = new ArrayList();
    
    public StartGamePacket() {
        
    }
    
    public void encode() {
        
    }
    
    public void decode() {
        int players_count = buffer.getInt();
        
        for(int i = 0; i < players_count; i++) {
            PlayerSession player_session = new PlayerSession();
            
            player_session.position = BinaryUtils.read_vector3(buffer);
            player_session.rotation = BinaryUtils.read_quaternion(buffer);
            player_session.client_id = BinaryUtils.read_string(buffer, 10);
            player_session.name = BinaryUtils.read_string(buffer, 20);
            player_session.active = true;
            
            players.add(player_session);
        }
    }

}
