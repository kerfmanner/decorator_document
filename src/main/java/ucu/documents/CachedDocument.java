package ucu.documents;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CachedDocument extends AbstractDecorator {
    private static final String TABLE_NAME = "document_cache";

    private final String cacheKey;
    private final String databaseUrl;

    public CachedDocument(Document document, String cacheKey, String databaseUrl) {
        super(document);
        this.cacheKey = cacheKey;
        this.databaseUrl = databaseUrl;
    }

    @Override
    public String parse() {
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            createTableIfNeeded(connection);
            String cachedContent = readFromCache(connection);
            if (cachedContent != null) {
                return cachedContent;
            }

            String parsed = super.parse();
            persistToCache(connection, parsed);
            return parsed;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to parse document", e);
        }
    }

    private void createTableIfNeeded(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                    + " (document_key TEXT PRIMARY KEY, content TEXT)");
        }
    }

    private String readFromCache(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT content FROM " + TABLE_NAME + " WHERE document_key = ?")) {
            statement.setString(1, cacheKey);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("content");
            }
            return null;
        }
    }

    private void persistToCache(Connection connection, String parsed) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT OR REPLACE INTO " + TABLE_NAME + " (document_key, content) VALUES (?, ?)")) {
            statement.setString(1, cacheKey);
            statement.setString(2, parsed);
            statement.executeUpdate();
        }
    }
}
