package server;
/* Will handle the request from the server for multiple clients */


import java.io.*;
import java.net.Socket;
import java.util.Scanner;

class ClientHandler extends Thread {

    private Socket clientSocket;
    private PrintWriter outStream;
    private BufferedReader inStream;


    public ClientHandler(Socket socket){
        clientSocket = socket;



    }


    public void run(){

         String clientGuess = "\0";
        try {

            outStream = new PrintWriter(clientSocket.getOutputStream(),true);
            inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outStream.println("Guess a letter");
            clientGuess = inStream.readLine();


            while ((clientGuess = inStream.readLine())!= null){

                System.out.println("Received form client: " + clientGuess);
            }

            clientSocket.close();
        } catch (IOException ioEx){
            ioEx.printStackTrace();
        }



        outStream.println("Please guess a letter");
        outStream.println();
    }


}
