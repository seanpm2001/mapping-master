/* Generated By:JJTree: Do not edit this line. ASTOWLObjectUnion.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.mm.parser.node;

import org.mm.parser.MappingMasterParser;
import org.mm.parser.NodeVisitor;

public
class ASTObjectUnion extends SimpleNode {
  public ASTObjectUnion(int id) {
    super(id);
  }

  public ASTObjectUnion(MappingMasterParser p, int id) {
    super(p, id);
  }

  @Override
  public void accept(NodeVisitor visitor) { /* XXX: Manually added */
    visitor.visit(this);
  }
}
/* JavaCC - OriginalChecksum=4d58de6d518ceaabbbe9aeba5a5e8626 (do not edit this line) */
