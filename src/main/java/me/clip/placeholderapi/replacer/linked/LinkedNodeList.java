package me.clip.placeholderapi.replacer.linked;

import me.clip.placeholderapi.replacer.linked.node.BaseNode;

public class LinkedNodeList {

  public static class Node {
    public BaseNode item;
    public Node prev;
    public Node next;

    public Node(BaseNode item) {
      this.item = item;
    }
  }

  public Node first;
  private Node last;

  public LinkedNodeList() {
    first = new Node(null);
    last = first;
  }

  public void add(BaseNode item) {
    final Node node = new Node(item);
    node.prev = last;
    last.next = node;
    last = node;
  }
}