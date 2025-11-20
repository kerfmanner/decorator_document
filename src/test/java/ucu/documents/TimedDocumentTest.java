package ucu.documents;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimedDocumentTest {
    @Test
    void measuresParseDuration() {
        Document delayedDocument = () -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            return "parsed";
        };

        TimedDocument timedDocument = new TimedDocument(delayedDocument);

        String result = timedDocument.parse();

        assertEquals("parsed", result);
        assertTrue(timedDocument.getLastDurationMillis() >= 40,
                "Expected duration to be measured");
    }
}
