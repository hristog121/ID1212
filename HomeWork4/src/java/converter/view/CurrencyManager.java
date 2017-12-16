package converter.view;

import converter.controller.ConverterFacade;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import converter.model.CurrencyDTO;

/**
 * Used to interact with the user interface and execute user initiated commands
 * @author iceroot
 */
@Named("convertManager")
@ConversationScoped
public class CurrencyManager implements Serializable {

    private static final long serialVersionUID = 16247164405L;
    @EJB
    private ConverterFacade cashierFacade;
    @Inject
    private Conversation conversation;
    
    private Exception transactionFailure;
    private String inputCurr1 = "SEK";
    private String inputCurr2 = "SEK";
    private float amount = 0;
    private float result = 0;

    private void startConversation() {
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    private void stopConversation() {
        if (!conversation.isTransient()) {
            conversation.end();
        }
    }

    /**
     * Converts a specified amount (amount) from the specified currency (inputCurr1)
     * to another specified currency (inputCurr2)
     */
    public void convert() {
        try {
            startConversation();
            transactionFailure = null;
            String desiredCurrencyPair = inputCurr1 + inputCurr2;
            if (inputCurr1.equals(inputCurr2)){
               result = amount*1; 
            } else {
                CurrencyDTO currencyPair = cashierFacade.findConversionRate(desiredCurrencyPair);
                float multiplier = currencyPair.getRate();
                result = amount*multiplier;
            }
        } catch (Exception e) {
            handleException(e);
        }
    } 
    
    /**
     * Prints an exception stack trace and stores the exception in transactionFailure
     * in order to be displayed to the user
     * @param e - an exception that is thrown
     */
    private void handleException(Exception e) {
        stopConversation();
        e.printStackTrace(System.err);
        transactionFailure = e; 
    }
    
    /**
     * Returns whether an error has occurred
     * @return True if no exception was occurred. False otherwise
     */
    public boolean getSuccess() {
        return transactionFailure == null;
    }

    public Exception getException() {
        return transactionFailure;
    }
    
    public void setAmount(float amount) {        
        this.amount = amount;        
    }
    
    public float getAmount() {       
        return amount;
    }

    
    public float getResult() {
        return result;
    }

    public void setResult(float result) {
        this.result = result;
    }
    
    
    public void setInputCurr1(String inputCurr1) {       
        this.inputCurr1 = inputCurr1;
    }
    
    public String getInputCurr1() {        
        return inputCurr1;
    } 
    
    
    public void setInputCurr2(String inputCurr2) {       
        this.inputCurr2 = inputCurr2;
    }
    
    public String getInputCurr2() {        
        return inputCurr2;
    }
}
