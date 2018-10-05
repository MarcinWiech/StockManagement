import server.ServerBackend;
import server.ServerInterface;
import server.ServerWindow;
import java.io.IOException;

/**
 * @author mw7u17
 */

public class ServerApplication {

    public static ServerInterface initialise(){
        try {
            return new ServerBackend();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void launchGUI(ServerInterface serverBackEnd){
        new ServerWindow(serverBackEnd);
    }

    public static void main(String[] args){
        launchGUI(initialise());
    }
}
