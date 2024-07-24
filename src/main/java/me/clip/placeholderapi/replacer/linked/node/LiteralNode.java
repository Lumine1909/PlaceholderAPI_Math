package me.clip.placeholderapi.replacer.linked.node;

public class LiteralNode extends BaseNode {

  private final String literalValue;
  private final boolean mustNotNumber;

  public LiteralNode(String literalValue) {
    this(literalValue, true);
  }

  public LiteralNode(String literalValue, boolean mustNotNumber) {
    this.literalValue = literalValue;
    this.mustNotNumber = mustNotNumber;
  }

  @Override
  public String parseString() {
    return literalValue;
  }

  @Override
  public NumberNode tryParseNumber() {
    if (mustNotNumber) {
      return null;
    }
    try {
      double doubleValue = Double.parseDouble(literalValue);
      return new NumberNode(doubleValue);
    } catch (NumberFormatException e) {
      return null;
    }
  }
}