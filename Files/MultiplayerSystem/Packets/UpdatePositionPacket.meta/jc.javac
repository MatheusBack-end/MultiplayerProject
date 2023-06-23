package JAVARuntime;

import java.nio.*;

public class UpdatePositionPacket {
    
    private static int PID = 0x02;
    public ByteBuffer buffer;
    public Vector3 position;
    public Quaternion rotation;
    public String client_id;
    
    public UpdatePositionPacket() {
        
    }
    
    public void encode() {
        buffer = ByteBuffer.allocate(35);
        buffer.put((byte) PID);
        BinaryUtils.write_vector3(buffer, position);
        BinaryUtils.write_quaternion(buffer, rotation);
        BinaryUtils.write_string(buffer, client_id, 10);
    }
    
    public void decode() {
        position = BinaryUtils.read_vector3(buffer);
        rotation = BinaryUtils.read_quaternion(buffer);
        client_id = BinaryUtils.read_string(buffer, 10);
    }
}
