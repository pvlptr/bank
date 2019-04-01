package engineering.pvl.bank.transaction.service;

import engineering.pvl.bank.transaction.model.Transaction;

public interface TransferService {

    Transaction transfer(TransferRequest transferRequest);
}
