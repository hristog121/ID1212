package converter.controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Initializes the conversion rates
 * @author iceroot
 */
public class CoversionRatesInitializer {

    /**
     * Reads and stores conversion rates from the file convertrates.txt
     * @param converterFacade - a facade which exposes methods for storing or
     * retrieving conversion rates from the database
     */
    public void readCoversionRates(ConverterFacade converterFacade){
        try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream("/converter/resources/convertrates.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                String[] covertionRate = line.split(";");
                converterFacade.createConversionRate(covertionRate[0], Float.parseFloat(covertionRate[1]));
                line = br.readLine();
            }
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Error while initializing conversion rates.", ex);
        } catch (IOException ex) {
            throw new RuntimeException("Error while initializing conversion rates.", ex);
        }
    }
}
