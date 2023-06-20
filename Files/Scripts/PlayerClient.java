package JAVARuntime;

import java.util.*;
import java.text.*;
import java.net.*;
import java.math.*;
import java.io.*;
import java.nio.*;

public class PlayerClient extends Component {
    
    private String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public ServerConnection connection;
    public String player_name = "player";
    public SUIText ping_viewer;
    private float timer;
    
    @Override
    public void start()
    {
        connection.open_connection(get_client_id(), player_name, myTransform.position, myTransform.rotation);
        connection.async_server_listener();
    }

    @Override
    public void repeat()
    {
        timer += Math.bySecond();
        
        if(timer >= 2f)
        {
            timer = 0;
            connection.ping_server();
        }
        
        connection.update_position(get_client_id(), myTransform.position, myTransform.rotation);
        
        ping_viewer.setText("ping: " + connection.get_ping());
    }
    
    @Override
    public void stoppedRepeat()
    {
        if(connection != null)
        {
            connection.close_connection(get_client_id());
        }
    }
    
    private String get_client_id()
    {
        String id = SaveGame.loadString("clientid");
        
        if(id == null)
        {
            StringBuilder sb = new StringBuilder(10);
            for (int i = 0; i < 10; i++)
            {
                int index = (int) (Random.range(0, characters.length() - 1));
                sb.append(characters.charAt(index));
            }
            
            id = sb.toString();
            SaveGame.saveString("clientid", id);
        }
        
        return id;
    }
}














