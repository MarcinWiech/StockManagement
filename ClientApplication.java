import client.ClientBackend;
import client.ClientInterface;
import client.ClientWindow;

/**
 * @author mw7u17
 */

public class ClientApplication {

    public static ClientInterface initialise(){
        return new ClientBackend() {
        };
    }

    public static void launchGUI(ClientInterface clientBackEnd){
        new ClientWindow(clientBackEnd);
    }

    public static void main (String args[]){

        launchGUI(initialise());
    }
}
