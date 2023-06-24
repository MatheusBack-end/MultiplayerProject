package JAVARuntime;

import java.nio.*;

public class OpenSessionPacket {
    
    private static int PID = 0x01;
    public ByteBuffer buffer;
    public PlayerSession player_session;
    public String client_id;
    public String player_name;
    public Vector3 position;
    public Quaternion rotation;
    
    public OpenSessionPacket() {
        
    }
    
    public void encode() {
        buffer = ByteBuffer.allocate(55);
        buffer.put((byte) PID);
        BinaryUtils.write_string(buffer, client_id, 10);
        BinaryUtils.write_string(buffer, player_name, 20);
        BinaryUtils.write_vector3(buffer, position);
        BinaryUtils.write_quaternion(buffer, rotation);
    }
    
    public void decode() {
        player_session = new PlayerSession();
        
        player_session.client_id = BinaryUtils.read_string(buffer, 10);
        player_session.name = BinaryUtils.read_string(buffer, 20);
        player_session.position = BinaryUtils.read_vector3(buffer);
        player_session.rotation = BinaryUtils.read_quaternion(buffer);
        player_session.active = true;
    }
}
