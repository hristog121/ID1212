package server.net;

import server.model.ClientInput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;

public class ClientCommunication {
    private PrintWriter out;
    private BufferedReader in;

    //Constructor
    public ClientCommunication(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;
    }

    // A method that prints to the client a char array
    public void printToClient(char[] charArray) {
        for (int i = 0; i < charArray.length; i++) {
            out.print(charArray[i] + " ");
        }
        out.println();
    }

    public void printToClient(String toClient) {
        out.println(toClient);
        out.println();
    }

    //Method that reads and input from the client
    public ClientInput readClientInput() throws IOException {    //TODO blocking
        try {
            //Read a line from the input from client and check if the client is disconnected
            return new ClientInput(in.readLine());
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return new ClientInput(in.readLine());
    }
}
