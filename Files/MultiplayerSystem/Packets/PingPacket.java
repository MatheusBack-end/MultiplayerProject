package JAVARuntime;

import java.nio.*;

public class PingPacket {
    
    private static int PID = 0x00;
    public ByteBuffer buffer;
    public long timestamp;
    
    public PingPacket() {
        
    }
    
    public void encode() {
        buffer = ByteBuffer.allocate(9);
        buffer.put((byte) PID);
        buffer.putLong(timestamp);
    }
    
    public void decode() {
        
    }
}
