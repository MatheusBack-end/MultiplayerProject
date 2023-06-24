package JAVARuntime;

import java.util.*;

public class PlayerSession {
    
    public String client_id;
    public String name;
    public Vector3 position = new Vector3();
    public Quaternion rotation = new Quaternion();
    public SpatialObject player_object;
    public boolean active;
    
    public PlayerSession() {
        
    }
}
