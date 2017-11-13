package server;
/*  */
public class ClientInput {
    private String input;
    //Constructor
    public ClientInput(String input) {
        this.input = input;
    }
    //Returns and input from the client
    public String getInput() {
        return input;
    }
    //Method that checks if the client has disconnected
    public boolean isDisconnected() {
        boolean disconnected = input == null;
        if (disconnected) {
            //Message in the server if one of the clients is disconnected
            System.out.println("Client " + Thread.currentThread().getName() + " has disconnected");
        }

        return disconnected;
    }
}
