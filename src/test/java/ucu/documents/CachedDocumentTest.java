package ucu.documents;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CachedDocumentTest {
    @Test
    void usesCacheAfterFirstParse() throws Exception {
        Path dbFile = Files.createTempFile("document-cache", ".db");
        String databaseUrl = "jdbc:sqlite:" + dbFile.toAbsolutePath();

        Document delegate = mock(Document.class);
        when(delegate.parse()).thenReturn("cached content");

        CachedDocument cachedDocument = new CachedDocument(delegate, "gcs://bucket/document1", databaseUrl);

        assertEquals("cached content", cachedDocument.parse());
        verify(delegate, times(1)).parse();

        assertEquals("cached content", cachedDocument.parse());
        verify(delegate, times(1)).parse();

        Files.deleteIfExists(dbFile);
    }
}
