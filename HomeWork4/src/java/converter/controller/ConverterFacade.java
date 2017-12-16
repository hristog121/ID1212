package converter.controller;

import converter.integration.CurrencyDAO;
import converter.model.Currency;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import converter.model.CurrencyDTO;
import javax.ejb.EJB;

/**
 * Exposes methods to store and retrieve conversion rates
 * @author iceroot
 */
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class ConverterFacade {
    @EJB CurrencyDAO currencyDao;    

    /**
     * Creates new conversion rate and stores it in the DB
     * @param currName - currency pair name
     * @param value - conversion rate
     * @return the newly created conversion rate
     */
    public CurrencyDTO createConversionRate(String currName, float value) {
        Currency newAcct = new Currency(value, currName);
        currencyDao.insertCurrency(newAcct);
        return newAcct;
    }

    /**
     * Finds conversion rate by currency pair name
     * @param currName
     * @return the conversion rate
     */
    public CurrencyDTO findConversionRate(String currName) {
        return currencyDao.findCurrency(currName);
    }
}
