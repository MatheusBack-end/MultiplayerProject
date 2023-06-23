package JAVARuntime;

import java.nio.*;

public class BinaryUtils {
    
    public BinaryUtils() {
        
    }
    
    public static String read_string(ByteBuffer buffer, int size) {
        byte[] destiny = new byte[size];
        
        for(int i = 0; i < size; i++) {
            destiny[i] = buffer.get();
        }
        
        return new String(destiny);
    }
    
    public static Vector3 read_vector3(ByteBuffer buffer) {
        float x = buffer.getFloat();
        float y = buffer.getFloat();
        float z = buffer.getFloat();
        
        return new Vector3(x, y, z);
    }
    
    public static Quaternion read_quaternion(ByteBuffer buffer) {
        float w = 0.0f;
        float x = buffer.getFloat();
        float y = buffer.getFloat();
        float z = buffer.getFloat();
        
        return new Quaternion(w, x, y, z);
    }
    
    public static void write_vector3(ByteBuffer buffer, Vector3 vector3) {
        buffer.putFloat(vector3.x);
        buffer.putFloat(vector3.y);
        buffer.putFloat(vector3.z);
    }
    
    public static void write_quaternion(ByteBuffer buffer, Quaternion quaternion) {
        buffer.putFloat(quaternion.x);
        buffer.putFloat(quaternion.y);
        buffer.putFloat(quaternion.z);
    }
    
    public static void write_string(ByteBuffer buffer, String string, int size) {
        buffer.put(write_string(string, size));
    }
    
    private static byte[] write_string(String string, int size) {
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.put(string.getBytes());
        
        return buffer.array();
    }
}
