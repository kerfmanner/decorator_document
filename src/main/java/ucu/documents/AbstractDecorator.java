package ucu.documents;

public abstract class AbstractDecorator implements Document {
    private final Document document;

    protected AbstractDecorator(Document document) {
        this.document = document;
    }

    protected Document getDocument() {
        return document;
    }

    @Override
    public String parse() {
        return document.parse();
    }
}
