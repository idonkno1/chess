package passofTests.serverTests.serviceTests;

import dataAccess.MemoryDataAccess;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import service.JoinGameService;

public class JoinGameServiceTest {
    private MemoryDataAccess memoryDataAccess;
    private JoinGameService joinGameService;

    @BeforeEach
    public void setUp() {
        memoryDataAccess = new MemoryDataAccess();
        joinGameService = new JoinGameService(memoryDataAccess);
    }

    @AfterEach
    public void tearDown() {
        // Clear the database after each test to ensure a clean state
        memoryDataAccess.clearDAO();
    }
}
