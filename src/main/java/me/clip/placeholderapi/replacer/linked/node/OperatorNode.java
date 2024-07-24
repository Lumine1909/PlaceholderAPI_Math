package me.clip.placeholderapi.replacer.linked.node;

import java.util.function.BiFunction;

public class OperatorNode extends BaseNode {

  public OperatorType type;

  public OperatorNode(OperatorType type) {
    this.type = type;
  }

  @Override
  public String parseString() {
    return type.name();
  }

  public enum OperatorType {

    ADD(NumberNode::add),
    MINUS(NumberNode::minus),
    TIMES(NumberNode::times),
    DIVIDED(NumberNode::dividedBy);

    public BiFunction<NumberNode, NumberNode, NumberNode> function;

    OperatorType(BiFunction<NumberNode, NumberNode, NumberNode> function) {
      this.function = function;
    }
  }
}