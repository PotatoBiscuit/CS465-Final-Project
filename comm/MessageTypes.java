package comm;

public interface MessageTypes {
    
    public static int READ_REQUEST = 1;
    public static int WRITE_REQUEST = 2;
    public static int CREATE_TRANS = 3;
    public static int CLOSE_TRANS = 4;
	public static int READ_LOCK = 5;
	public static int WRITE_LOCK = 6;
	public static int EMPTY_LOCK = 0;
	public static int DISPLAY = 7;
}
