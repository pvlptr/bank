package engineering.pvl.bank.utils;

public class ResourceNotFoundException extends BankOperationException {

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
