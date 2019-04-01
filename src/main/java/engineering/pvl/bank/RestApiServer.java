package engineering.pvl.bank;

import engineering.pvl.bank.account.rest.AccountRest;
import engineering.pvl.bank.transaction.rest.TransactionRest;
import engineering.pvl.bank.transaction.rest.TransferRest;
import engineering.pvl.bank.transaction.service.InsufficientFundsException;
import engineering.pvl.bank.utils.BankOperationException;
import engineering.pvl.bank.utils.JsonTransformer;
import engineering.pvl.bank.utils.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ResponseTransformer;
import spark.Spark;

import java.util.UUID;

import static spark.Spark.*;

public class RestApiServer {

    public static class ErrorResponse {
        private final String errorMessage;

        public ErrorResponse(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(RestApiServer.class);

    public static final int SERVER_PORT = 18081;

    public static final String API_PREFIX = "api";
    public static final String API_VERSION = "1.0";
    public static final String API_PATH = "/" + API_PREFIX + "/" + API_VERSION;

    private final ServiceRegistry serviceRegistry;

    public RestApiServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void start() {
        port(SERVER_PORT);
        before((request, response) -> response.type("application/json"));

        initExceptionHandlers();

        createAndInitRestServices();
        awaitInitialization();

        String url = "http://localhost:" + SERVER_PORT + API_PATH;
        log.info("Bank rest API server started on: {}", url);
        log.info("Accounts: {}", url + "/accounts");
        log.info("Transactions: {}", url + "/transactions");
        log.info("Transfers: {}", url + "/transfer");
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
        log.info("Bank rest API server stopped");
    }


    private void initExceptionHandlers() {

        exception(ResourceNotFoundException.class, (exception, request, response) -> {
            response.status(404);
            response.body(serviceRegistry.getGson().toJson(new ErrorResponse("Resource not found")));
            log.trace("Resource by request {} not found", request);
            log.trace("Resource not found Exception: ", exception);
        });
        exception(InsufficientFundsException.class, (exception, request, response) -> {
            response.status(400);
            response.body(serviceRegistry.getGson().toJson(new ErrorResponse("Insufficient funds")));
            log.trace("Insufficient funds for operation: {}", request);
            log.trace("Insufficient funds Exception: ", exception);
        });
        exception(BankOperationException.class, (exception, request, response) -> {
            response.status(400);
            response.body(serviceRegistry.getGson().toJson(new ErrorResponse("Invalid operation")));
            log.trace("Invalid operation: {}", request);
            log.trace("Invalid operation Exception: ", exception);
        });
        exception(IllegalArgumentException.class, (exception, request, response) -> {
            response.status(400);
            response.body(serviceRegistry.getGson().toJson(new ErrorResponse("Invalid operation")));
            log.trace("Invalid operation: {}", request);
            log.trace("Invalid operation Exception: ", exception);
        });

        exception(Exception.class, (exception, request, response) -> {
            response.status(500);
            response.body(serviceRegistry.getGson().toJson(new ErrorResponse("Internal error")));
            log.error("Internal server error", exception);
        });
    }

    private void createAndInitRestServices() {
        ResponseTransformer responseTransformer = new JsonTransformer(serviceRegistry.getGson());

        path(API_PATH, () -> {
                    //accounts
                    path("/accounts", () -> {
                        AccountRest accountRest = new AccountRest(
                                serviceRegistry.getAccountRepository(),
                                serviceRegistry.getAccountService(),
                                serviceRegistry.getGson());
                        get("", (req, res) -> accountRest.listAll(), responseTransformer);
                        post("", (req, res) -> accountRest.create(req), responseTransformer);
                        get("/:id", (req, res) ->
                                accountRest.getById(UUID.fromString(req.params(":id"))), responseTransformer);
                    });
                    path("/transactions", () -> {
                        TransactionRest transactionRest = new TransactionRest(serviceRegistry.getTransactionRepository());
                        get("", (req, res) -> transactionRest.list(), responseTransformer);
                        get("/:id", (req, res) ->
                                transactionRest.getById(UUID.fromString(req.params(":id"))), responseTransformer);
                    });

                    //transfers
                    TransferRest transferRest = new TransferRest(
                            serviceRegistry.getTransferService(),
                            serviceRegistry.getGson());
                    post("/transfers", (req, res) -> transferRest.transfer(req), responseTransformer);
                }
        );

        get("*", (request, response) -> {
                    throw new ResourceNotFoundException();
                }
        );
        post("*", (request, response) -> {
                    throw new ResourceNotFoundException();
                }
        );
        put("*", (request, response) -> {
                    throw new ResourceNotFoundException();
                }
        );
        delete("*", (request, response) -> {
                    throw new ResourceNotFoundException();
                }
        );


    }


}
