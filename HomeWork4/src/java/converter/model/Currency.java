package converter.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * A model representing a conversion rate
 * @author iceroot
 */
@Entity
public class Currency implements CurrencyDTO, Serializable {

    private static final long serialVersionUID = 16247164401L;
    @Id
    private String currName; // Currency pair
    private float rate; // Conversion rate

    public Currency() {
    }

    /**
     * Creates a conversion rate based on currency pair name and conversion rate
     * @param value - conversion rate
     * @param currName - currency pair
     */
    public Currency(float value, String currName) {
        this.rate = value;
        this.currName = currName;        
    }

    @Override
    public String getCurrName() {
        return currName;
    }

    @Override
    public float getRate() {
        return rate;
    }

    /**
     * Overrides hash code, as equals is also overriten
     * @return - hash code based on the currency pair name
     */
    @Override
    public int hashCode() {
        int hash = 0;
        return new String(currName).hashCode();
    }

    /**
     * Determines whether two conversion rates are the same
     * @param object - conversion rate to compare to
     * @return True if the currency pair names match, False otherwise
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Currency)) {
            return false;
        }
        Currency other = (Currency) object;
        return this.currName == other.currName;
    }

    /**
     * Creates a string representation of the conversion rate
     * @return - the string representation of a conversion rate
     */
    @Override
    public String toString() {
        return "Conversion rate [currency pair=" + currName + "conversion rate" + rate + "]";
    }
}
