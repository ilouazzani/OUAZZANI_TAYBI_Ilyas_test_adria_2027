package net.adria.bank.transaction.service.exception;

public class TransactionExceedsLimitException extends RuntimeException {
    public TransactionExceedsLimitException(String message) {
        super(message);
    }
}
