package server.model;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class GameWord {

    private final char[] wordArray;
    private final char[] newMasked;

    private final String word;

    // Constructor
    public GameWord() {
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
    public boolean checkWholeWordGuessed(final String clientGuess) {
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
    public boolean checkLetterGuessed(final String clientGuess) {
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
    public String getNewMasked() {
        // add " " after every letter
        final StringBuilder sb = new StringBuilder(newMasked.length * 2);
        for (final char ch : newMasked) {
            sb.append(ch).append(" ");
        }
        return sb.toString();
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
    private String chooseWordFromFile() {
        // Take the words from the word.txt file
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(classLoader.getResource("words.txt").getFile());
        try (FileInputStream getFile = new FileInputStream(file);
             BufferedReader getData = new BufferedReader(new InputStreamReader(new DataInputStream(getFile)))) {

            String word = "";

            //Get a random word from the txt file
            final int random = (int) Math.round(Math.random() * 51528);
            for (int i = 0; i < random; i++) {
                word = getData.readLine();
            }
            return word;
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
