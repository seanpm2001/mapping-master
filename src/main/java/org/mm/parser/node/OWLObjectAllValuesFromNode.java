package org.mm.parser.node;

import org.mm.parser.ASTOWLClass;
import org.mm.parser.ASTOWLClassExpression;
import org.mm.parser.ASTOWLObjectAllValuesFrom;
import org.mm.parser.InternalParseException;
import org.mm.parser.Node;
import org.mm.parser.ParseException;
import org.mm.parser.ParserUtil;

public class OWLObjectAllValuesFromNode
{
  private OWLClassExpressionNode classExpressionNode;
  private OWLClassNode classNode;

  public OWLObjectAllValuesFromNode(ASTOWLObjectAllValuesFrom node) throws ParseException
  {
    Node child = node.jjtGetChild(0);
    if (ParserUtil.hasName(child, "OWLClassExpression"))
      classExpressionNode = new OWLClassExpressionNode((ASTOWLClassExpression)child);
    else if (ParserUtil.hasName(child, "OWLClass"))
      classNode = new OWLClassNode((ASTOWLClass)child);
    else
      throw new InternalParseException(
        "OWLObjectAllValuesFrom node expecting OWLClassExpression child, got " + child.toString());
  }

  public boolean hasOWLClassExpression()
  {
    return classExpressionNode != null;
  }

  public boolean hasOWLClass()
  {
    return classNode != null;
  }

  public OWLClassExpressionNode getOWLClassExpressionNode()
  {
    return classExpressionNode;
  }

  public OWLClassNode getOWLClassNode()
  {
    return classNode;
  }

  public String toString()
  {
    String representation = "ONLY ";

    if (hasOWLClassExpression())
      representation += classExpressionNode.toString();
    else if (hasOWLClass())
      representation += classNode.toString();

    representation += ")";

    return representation;
  }
}
