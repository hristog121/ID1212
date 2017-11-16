/* This class will take care of giving a word to the client. All the small methods will take care of different things */

package server.model;

import java.io.*;

public class GameWord {

    private char[] wordArray;
    private char[] newMasked;

    private String word;

    // Constructor
    public GameWord() throws IOException {
        word = chooseWordFromFile();
        System.out.println("Client " + Thread.currentThread().getName() + " play with word: " + word);
        wordArray = word.toCharArray();
        newMasked = new char[wordArray.length];

        // Loop for filing up an a array with dashes ( mask the word )
        for (int i = 0; i < newMasked.length; i++) {
            newMasked[i] = '_';
        }
    }

    //Will return true if the word is guessed or false if it is not guessed
    public boolean checkWholeWordGuessed(String clientGuess) {
        if (word.equals(clientGuess) || word.equals(new String(newMasked))) {
            return true;
        }
        return false;
    }

    //Checking if the word is guessed if the player input a whole word
    public boolean isWordGuessed() {
        if (word.equals(new String(newMasked))) {
            return true;
        }
        return false;
    }

    //Checking if the word is guessed if the player input a letter by letter
    public boolean checkLetterGuessed(String clientGuess) {
        boolean found = false;

        //Check if the player input 'letter' is in the word
        for (int i = 0; i < wordArray.length; i++) {

            // Replace a dash with a guessed letter
            if (clientGuess.charAt(0) == wordArray[i]) {
                newMasked[i] = clientGuess.charAt(0);
                found = true;
            }
        }
        return found;
    }

    //Getting a masked word (with dashes)
    public char[] getNewMasked() {
        return newMasked;
    }

    //Getting the count - how many attempts the player has
    public int getCount() {
        return wordArray.length;
    }

    //Getting a word
    public String getWord() {
        return word;
    }

    //Extracting a random word from the text file.
    private String chooseWordFromFile() throws IOException {
        // Take the words from the word.txt file
        try (FileInputStream getFile = new FileInputStream("/Users/iceroot/Documents/KTH/Year2/P2/Homeworks/ID1212/HomeWork1/src/main/resources/words.txt");
             BufferedReader getData = new BufferedReader(new InputStreamReader(new DataInputStream(getFile)))) {

            String word = "";
            //Get a random word from the txt file
            int random = (int) Math.round(Math.random() * 51528);
            for (int i = 0; i < random; i++) {
                word = getData.readLine();
            }
            return word;
        } catch (IOException ex) {
            throw ex;
        }
    }
}
