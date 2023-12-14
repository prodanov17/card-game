import java.net.Socket;

public class User {
    private String name;
    private ClientHandler client;

    User(String name, ClientHandler client){
        this.name = name;
        this.client = client;
    }

    public String getName() {
        return name;
    }

    public ClientHandler getClient(){
        return this.client;
    }
}
