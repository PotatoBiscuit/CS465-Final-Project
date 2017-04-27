
package comm;

/**
 * Interface [MessageTypes] Defines the different message types used in the application.
 * Any entity using objects of class Message needs to implement this interface.
 * 
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public interface MessageTypes {
    
    public static int READ_REQUEST = 1;
    public static int WRITE_REQUEST = 2;
    public static int CREATE_TRANS = 3;
    public static int CLOSE_TRANS = 4;
}
