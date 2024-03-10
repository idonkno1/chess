package passofTests.serverTests.serviceTests;

import dataAccess.MemoryDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import service.CreateGameService;

public class CreateGameServiceTest {
    private MemoryDataAccess memoryDataAccess;
    private CreateGameService createGameService;

    @BeforeEach
    public void setUp() {
        memoryDataAccess = new MemoryDataAccess();
        createGameService = new CreateGameService(memoryDataAccess);
    }

    @AfterEach
    public void tearDown() {
        // Clear the database after each test to ensure a clean state
        memoryDataAccess.clearDAO();
    }
}
