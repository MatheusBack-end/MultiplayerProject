package JAVARuntime;

import java.nio.*;

public class CloseConnectionPacket {
    
    private static int PID = 0x05;
    public ByteBuffer buffer;
    public String client_id;
    
    public CloseConnectionPacket() {
        
    }
    
    public void encode() {
        buffer = ByteBuffer.allocate(11);
        buffer.put((byte) PID);
        BinaryUtils.write_string(buffer, client_id, 10);
    }
    
    public void decode() {
        client_id = BinaryUtils.read_string(buffer, 10);
    }
}
