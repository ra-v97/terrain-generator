package model;

public enum VertexType {

    HANGING_NODE("H"), SIMPLE_NODE("V");

    private final String symbol;

    VertexType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
