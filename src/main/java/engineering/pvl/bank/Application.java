package engineering.pvl.bank;

class Application {


    public static void main(String[] args) {
        initAndStartServer();
    }

    private static void initAndStartServer() {
        ServiceRegistry serviceRegistry = new ServiceRegistry();

        serviceRegistry.getAccountImporter().importData();
        RestApiServer apiServer = new RestApiServer(serviceRegistry);
        apiServer.start();
    }

}
