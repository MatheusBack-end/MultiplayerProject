package JAVARuntime;

public class PlayerMov extends Component {
    
    private SpatialObject camera;
    private Vector2 touch;
    private Vector2 joystick;
    private Characterbody body;
    
    public float sensitivity = 1.0f;

    @Override
    public void start() {
        body = myPhysics.getPhysicsEntity();
        camera = myObject.findChildObject("camera");
        touch = Input.getAxisValue("touch-area");
        joystick = Input.getAxisValue("joystick");
    }

    @Override
    public void repeat() {
        myObject.rotateInSeconds(0,-touch.x * sensitivity,0);
        camera.rotateInSeconds(touch.y * sensitivity, 0,0);
        
        body.setForwardSpeed(joystick.y);
        body.setSideSpeed(-joystick.x);
    }
}
