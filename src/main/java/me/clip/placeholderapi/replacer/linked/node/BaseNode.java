package me.clip.placeholderapi.replacer.linked.node;

import org.jetbrains.annotations.Nullable;

public abstract class BaseNode {

  public abstract String parseString();

  public @Nullable NumberNode tryParseNumber() {
    return null;
  }
}