package me.clip.placeholderapi.replacer.linked.node;

public class NumberNode extends BaseNode {

  private Number number;
  private boolean isDouble;

  public NumberNode(double value) {
    if ((long) value == value) {
      isDouble = false;
      number = (long) value;
    } else {
      isDouble = true;
      number = value;
    }
  }

  public NumberNode add(NumberNode node) {
    if (node.isDouble || this.isDouble) {
      isDouble = true;
      number = this.number.doubleValue() + node.number.doubleValue();
    } else {
      number = this.number.longValue() + node.number.longValue();
    }
    return this;
  }

  public NumberNode minus(NumberNode node) {
    if (node.isDouble || this.isDouble) {
      isDouble = true;
      number = this.number.doubleValue() - node.number.doubleValue();
    } else {
      number = this.number.longValue() - node.number.longValue();
    }
    return this;
  }

  public NumberNode times(NumberNode node) {
    if (node.isDouble || this.isDouble) {
      isDouble = true;
      number = this.number.doubleValue() * node.number.doubleValue();
    } else {
      number = this.number.longValue() * node.number.longValue();
    }
    return this;
  }

  public NumberNode dividedBy(NumberNode node) {
    if (node.isDouble || this.isDouble) {
      isDouble = true;
      number = this.number.doubleValue() / node.number.doubleValue();
    } else {
      number = this.number.longValue() / node.number.longValue();
    }
    return this;
  }

  @Override
  public String parseString() {
    if (isDouble) {
      return String.valueOf(number.doubleValue());
    } else {
      return String.valueOf(number.longValue());
    }
  }

  @Override
  public NumberNode tryParseNumber() {
    return this;
  }
}