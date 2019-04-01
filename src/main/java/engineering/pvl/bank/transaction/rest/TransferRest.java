package engineering.pvl.bank.transaction.rest;

import com.google.gson.Gson;
import engineering.pvl.bank.transaction.model.Transaction;
import engineering.pvl.bank.transaction.service.TransferRequest;
import engineering.pvl.bank.transaction.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

public class TransferRest {

    private static final Logger log = LoggerFactory.getLogger(TransferRest.class);

    private final TransferService transferService;
    private final Gson gson;

    public TransferRest(TransferService transferService, Gson gson) {
        this.transferService = transferService;
        this.gson = gson;
    }

    public Transaction transfer(Request request) {
        log.debug("Transferring: {}", request);
        TransferRequest transferRequest = gson.fromJson(request.body(), TransferRequest.class);
        Transaction transaction = transferService.transfer(transferRequest);
        log.trace("Transfer created transaction: {}", transaction);
        return transaction;
    }

}
