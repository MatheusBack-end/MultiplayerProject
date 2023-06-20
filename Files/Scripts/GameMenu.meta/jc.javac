package JAVARuntime;

public class GameMenu extends Component {
    
    public WorldFile game_scene;
    public WorldFile settings_scene;
    public ServerConnection connection;
    private Key game_start_btn;
    private Key game_settings_btn;
    
    @Override
    public void start()
    {
        game_start_btn = Input.getKey("start-game-btn");
        game_settings_btn = Input.getKey("settings-game-btn");
    }

    @Override
    public void repeat()
    {
        if(game_start_btn.isDown())
        {
            WorldController.loadWorldAsync(game_scene);
        }
        
        if(game_settings_btn.isDown())
        {
            WorldController.loadWorldAsync(settings_scene);
        }
    }
    
    public void stoppedRepeat()
    {
        if(connection != null)
        {
            String cid = SaveGame.loadString("clientid");
            
            if(cid != null)
            {
                connection.close_connection(cid);
            }
        }
    }
}










// eof