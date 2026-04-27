package service;

/**
 * Exception nghiệp vụ (để hiện message thân thiện lên UI).
 */
public class ServiceException extends Exception {
    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

