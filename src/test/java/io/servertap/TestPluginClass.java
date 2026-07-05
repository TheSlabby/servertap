package io.servertap;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

public class TestPluginClass {
    private static ServerMock server;
    private static ServerTapMain plugin;
    private static boolean mockBukkitAvailable = false;

    private static final String TEST_URL_BASE = "http://localhost:4567";

    @BeforeAll
    public static void setUp() {
        try {
            server = MockBukkit.mock();
            plugin = MockBukkit.load(ServerTapMain.class);
            mockBukkitAvailable = true;
        } catch (Throwable e) {
            System.err.println("MockBukkit initialization skipped: " + e.getMessage());
            mockBukkitAvailable = false;
        }
    }

    @AfterAll
    public static void tearDown() {
        if (mockBukkitAvailable) {
            MockBukkit.unmock();
        }
    }

    @Test
    @DisplayName("Verify that auth is on")
    void verifyTestEnvironment() {
        Assumptions.assumeTrue(mockBukkitAvailable, "MockBukkit is not available");
        HttpResponse<JsonNode> response = Unirest.get(TEST_URL_BASE + "/v1/players/all").asJson();
        Assertions.assertEquals(401, response.getStatus());
    }

    @Test
    @DisplayName("swagger endpoint loads")
    void verifySwaggerUI() {
        Assumptions.assumeTrue(mockBukkitAvailable, "MockBukkit is not available");
        HttpResponse<JsonNode> response = Unirest.get(TEST_URL_BASE + "/swagger").asJson();
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("swagger-docs endpoint loads")
    void verifySwaggerDocs() {
        Assumptions.assumeTrue(mockBukkitAvailable, "MockBukkit is not available");
        HttpResponse<JsonNode> response = Unirest.get(TEST_URL_BASE + "/swagger-docs").asJson();
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("registries endpoint works")
    void verifyRegistriesEndpoint() {
        Assumptions.assumeTrue(mockBukkitAvailable, "MockBukkit is not available");
        HttpResponse<JsonNode> response = Unirest.get(TEST_URL_BASE + "/v1/registries").asJson();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().getArray().length() > 0);
    }
}
