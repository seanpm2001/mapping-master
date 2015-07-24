package org.mm.parser.node;

import org.mm.parser.ASTName;
import org.mm.parser.ASTOWLLiteral;
import org.mm.parser.ASTOWLPropertyAssertionObject;
import org.mm.parser.ASTReference;
import org.mm.parser.InternalParseException;
import org.mm.parser.Node;
import org.mm.parser.ParseException;
import org.mm.parser.ParserUtil;

public class OWLPropertyAssertionObjectNode implements MMNode
{
  private ReferenceNode referenceNode;
  private NameNode nameNode;
  private OWLLiteralNode literalNode;

  public OWLPropertyAssertionObjectNode(ASTOWLPropertyAssertionObject node) throws ParseException
  {
    if (node.jjtGetNumChildren() != 1)
      throw new InternalParseException("expecting one child of node " + getNodeName());
    else {
      Node child = node.jjtGetChild(0);
      if (ParserUtil.hasName(child, "Reference"))
        this.referenceNode = new ReferenceNode((ASTReference)child);
      else if (ParserUtil.hasName(child, "Name"))
        this.nameNode = new NameNode((ASTName)child);
      else if (ParserUtil.hasName(child, "OWLLiteral"))
        this.literalNode = new OWLLiteralNode((ASTOWLLiteral)child);
      else
        throw new InternalParseException("unexpected child node " + child + " for node " + getNodeName());
    }
  }

  public String getNodeName()
  {
    return "OWLPropertyAssertionObject";
  }

  public ReferenceNode getReferenceNode()
  {
    return this.referenceNode;
  }

  public NameNode getNameNode()
  {
    return this.nameNode;
  }

  public OWLLiteralNode getOWLLiteralNode()
  {
    return this.literalNode;
  }

  public boolean isReference()
  {
    return this.referenceNode != null;
  }

  public boolean isName()
  {
    return this.nameNode != null;
  }

  public boolean isLiteral()
  {
    return this.literalNode != null;
  }

  public String toString()
  {
    if (isReference())
      return this.referenceNode.toString();
    else if (isName())
      return this.nameNode.toString();
    else if (isLiteral())
      return this.literalNode.toString();
    else
      return "";
  }
}
