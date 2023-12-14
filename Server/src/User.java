import java.net.Socket;

public class User {
    private String name;
    private String addr;
    private Socket clientSocket;

    User(String name, String addr, Socket clientSocket){
        this.name = name;
        this.addr = addr;
        this.clientSocket = clientSocket;
    }

    public String getName() {
        return name;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public String getAddr() {
        return addr;
    }
}
