package common;

public class FileCatalogException extends Exception {
    public FileCatalogException(String reason){
        super(reason);
    }
    public FileCatalogException(String reason, Throwable rootCause) {
        super(reason, rootCause);
    }
}
