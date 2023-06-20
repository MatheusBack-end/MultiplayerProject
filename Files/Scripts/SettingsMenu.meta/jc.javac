package JAVARuntime;

// Useful imports
import java.util.*;
import java.text.*;
import java.net.*;
import java.math.*;
import java.io.*;
import java.nio.*;

/**
 * @Author 
*/
public class SettingsMenu extends Component {
    
    public WorldFile menu_scene;
    private Key back_btn;
    private Key ip_btn;
    private Key port_btn;
    public SUIText ip_txt;
    public SUIText port_txt;

    /// Run only once
    @Override
    public void start() {
        back_btn = Input.getKey("back-btn");
        ip_btn = Input.getKey("ip-btn");
        port_btn = Input.getKey("port-btn");
        set_texts();
    }
    
    private void set_texts()
    {
        ip_txt.setText(SaveGame.loadString("host-ip"));
        port_txt.setText(SaveGame.loadInt("host-port"));
    }

    /// Repeat every frame
    @Override
    public void repeat() {
        if(back_btn.isDown())
        {
            WorldController.loadWorldAsync(menu_scene);
        }
        
        if(ip_btn.isDown())
        {
            new InputDialog("Ip label", new InputDialogListener() {
                public void onFinish(String text)
                {
                    SaveGame.saveString("host-ip", text);
                    ip_txt.setText(text);
                }
                
                public void onCancel()
                {
                    
                }
            });
        }
        
        if(port_btn.isDown())
        {
            new InputDialog("Port label", InputDialog.Type.Int, new InputDialogListener() {
                public void onFinish(String port)
                {
                    SaveGame.saveInt("host-port", (int) Integer.parseInt(port));
                    port_txt.setText(port);
                }
                
                public void onCancel()
                {
                    
                }
            });
        }
    }
}















// eof
