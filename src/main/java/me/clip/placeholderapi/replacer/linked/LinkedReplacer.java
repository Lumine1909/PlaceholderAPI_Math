package me.clip.placeholderapi.replacer.linked;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.replacer.Replacer;
import me.clip.placeholderapi.replacer.linked.node.LiteralNode;
import me.clip.placeholderapi.replacer.linked.node.NumberNode;
import me.clip.placeholderapi.replacer.linked.node.OperatorNode;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.Locale;
import java.util.function.Function;

public class LinkedReplacer implements Replacer {

  @NotNull
  private final Closure closure;

  public LinkedReplacer(@NotNull final Closure closure) {
    this.closure = closure;
  }

  @Override
  public @NotNull String apply(@NotNull String text, @Nullable OfflinePlayer player, @NotNull Function<String, @Nullable PlaceholderExpansion> lookup) {
    final LinkedNodeList nodes = new LinkedNodeList();
    final char[] chars = text.toCharArray();

    final StringBuilder literal = new StringBuilder();
    final StringBuilder identifier = new StringBuilder();
    final StringBuilder parameters = new StringBuilder();

    for (int i = 0; i < chars.length; i++) {
      final char l = chars[i];

      if (((l != closure.head) && (l != Closure.NUMBER.head) && (l != Closure.OPERATOR.head)) || i + 1 >= chars.length) {
        literal.append(l);
        continue;
      }

      if (literal.length() != 0) {
        nodes.add(new LiteralNode(literal.toString()));
      }
      literal.setLength(0);

      final Closure currentClosure;

      if (l == Closure.NUMBER.head) {
        currentClosure = Closure.NUMBER;
      } else if (l == Closure.OPERATOR.head) {
        currentClosure = Closure.OPERATOR;
      } else {
        currentClosure = closure;
      }

      boolean identified = false;
      boolean invalid = true;
      boolean hadSpace = false;

      while (++i < chars.length) {
        final char p = chars[i];

        if (p == ' ' && !identified) {
          hadSpace = true;
          break;
        }
        if (p == currentClosure.tail) {
          invalid = false;
          break;
        }

        if (p == '_' && !identified) {
          identified = true;
          continue;
        }

        if (identified) {
          parameters.append(p);
        } else {
          identifier.append(p);
        }
      }

      final String identifierString = identifier.toString();
      final String lowercaseIdentifierString = identifierString.toLowerCase(Locale.ROOT);
      final String parametersString = parameters.toString();

      System.out.println(identifierString);

      identifier.setLength(0);
      parameters.setLength(0);

      if (invalid) {
        literal.append(currentClosure.head).append(identifierString);

        if (identified) {
          literal.append('_').append(parametersString);
        }

        if (hadSpace) {
          literal.append(' ');
        }

        if (literal.length() != 0) {
          nodes.add(new LiteralNode(literal.toString()));
        }
        literal.setLength(0);
        continue;
      }

      switch (currentClosure) {
        case NUMBER: {
          try {
            double number = Double.parseDouble(identifierString);
            nodes.add(new NumberNode(number));
          } catch (Exception e) {
            nodes.add(new LiteralNode(Closure.NUMBER.head + identifierString + Closure.NUMBER.tail));
          }
          break;
        }
        case OPERATOR: {
          if (identifierString.length() != 1) {
            nodes.add(new LiteralNode(Closure.OPERATOR.head + identifierString + Closure.OPERATOR.tail));
            break;
          }
          switch (identifierString) {
            case "+": {
              nodes.add(new OperatorNode(OperatorNode.OperatorType.ADD));
              break;
            }
            case "-": {
              nodes.add(new OperatorNode(OperatorNode.OperatorType.MINUS));
              break;
            }
            case "*": {
              nodes.add(new OperatorNode(OperatorNode.OperatorType.TIMES));
              break;
            }
            case "/": {
              nodes.add(new OperatorNode(OperatorNode.OperatorType.DIVIDED));
              break;
            }
            default: {
              nodes.add(new LiteralNode(Closure.OPERATOR.head + identifierString + Closure.OPERATOR.tail));
            }
          }
          break;
        }
        case PERCENT:
        case BRACKET: {
          final PlaceholderExpansion placeholder = lookup.apply(lowercaseIdentifierString);

          if (placeholder == null) {
            literal.append(currentClosure.head).append(identifierString);

            if (identified) {
              literal.append('_');
            }

            literal.append(parametersString).append(currentClosure.tail);
            if (literal.length() != 0) {
              nodes.add(new LiteralNode(literal.toString()));
            }
            literal.setLength(0);

            continue;
          }
          final String replacement = placeholder.onRequest(player, parametersString);
          if (replacement == null) {
            literal.append(currentClosure.head).append(identifierString);

            if (identified) {
              literal.append('_');
            }

            literal.append(parametersString).append(currentClosure.tail);
            if (literal.length() != 0) {
              nodes.add(new LiteralNode(literal.toString()));
            }
            literal.setLength(0);
            continue;
          }

          nodes.add(new LiteralNode(replacement, false));
          break;
        }
      }
    }

    if (literal.length() != 0) {
      nodes.add(new LiteralNode(literal.toString()));
    }


    LinkedNodeList.Node node = nodes.first.next;
    int i = 0;
    while (node != null) {
      System.out.println(i + " " + node.item);
      if (node.item instanceof OperatorNode && node.prev != null && node.next != null) {
        NumberNode n1 = node.prev.item.tryParseNumber();
        NumberNode n2 = node.next.item.tryParseNumber();
        if (n1 != null && n2 != null) {
          System.out.println("Operated!");
          OperatorNode node1 = (OperatorNode) node.item;
          LinkedNodeList.Node newNode = new LinkedNodeList.Node(node1.type.function.apply(n1, n2));
          node.prev.prev.next = newNode;
          newNode.prev = node.prev.prev;
          newNode.next = node.next.next;
          if (node.next.next != null) {
            node.next.next.prev = newNode;
          }
        }
      }
      node = node.next;
      i++;
    }

    final StringBuilder builder = new StringBuilder(text.length());
    node = nodes.first.next;
    while (node != null) {
      builder.append(node.item.parseString());
      node = node.next;
    }

    return builder.toString();
  }
}
