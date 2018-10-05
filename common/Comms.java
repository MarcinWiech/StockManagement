package common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Comms sends and receives messages
 */

public abstract class Comms{

    protected Socket s;
    private ObjectOutputStream outputStream = null;
    private ObjectInputStream inputStream = null;

    //constructors
    public Comms(){}

    public Comms(Socket s, ObjectInputStream ois, ObjectOutputStream oos){

        this.s = s;
        this.outputStream = oos;
        this.inputStream = ois;
    }


    protected void sendMessage(Message m) {

        try {
            outputStream.writeObject(m);
            outputStream.reset();
        } catch (Exception e) {
            //Means that client closed window
        }
    }

    protected Message receiveMessage() {

        try {
            return (Message) inputStream.readObject();
        } catch (Exception e) { }
        return null;
    }

    public void setOutputStream(ObjectOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setInputStream(ObjectInputStream inputStream) {
        this.inputStream = inputStream;
    }
}
