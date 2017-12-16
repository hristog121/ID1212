package converter.integration;

import converter.model.Currency;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

/**
 * Encapsulates the interaction with entity manager, which store and retrieves
 * data from the database
 * @author iceroot
 */
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Stateless
public class CurrencyDAO {
    @PersistenceContext(unitName = "currencyPU")
    private EntityManager em;
        
    /**
     * Inserts a conversion rate
     * @param currency - the conversion rate that we want to store
     */
    public void insertCurrency(Currency currency) {
        em.persist(currency);
    }
    
    /**
     * Finds a conversion rate by the currency pair name
     * @param currName - currency pair name
     * @return the conversion rate if found. Throws EntityNotFoundException
     * otherwise
     */
    public Currency findCurrency(String currName) {
        Currency found = em.find(Currency.class, currName);
        if (found == null) {
            throw new EntityNotFoundException("No conversion info for " + currName);
        }
        return found;
    }
}
