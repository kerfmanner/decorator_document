package ucu.documents;

import java.util.concurrent.TimeUnit;

public class TimedDocument extends AbstractDecorator {
    private long lastDurationMillis;

    public TimedDocument(Document document) {
        super(document);
    }

    @Override
    public String parse() {
        long start = System.nanoTime();
        String parsed = super.parse();
        lastDurationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        return parsed;
    }

    public long getLastDurationMillis() {
        return lastDurationMillis;
    }
}
