package converter.controller;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * Used for initialization. The singleton session bean is initialized before 
 * the EJB container delivers client requests to any enterprise beans in the 
 * application.
 * @author iceroot
 */
@Startup
@Singleton
public class StartupBean {
    @EJB
    private ConverterFacade converterFacade;
    
    /**
     * Initializes the conversion rates
     */
    @PostConstruct
    void init() {
        new CoversionRatesInitializer().readCoversionRates(converterFacade);
        System.out.println("Currency rates initialized");
    }
}
