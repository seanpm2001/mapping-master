/* Generated By:JJTree: Do not edit this line. ASTOWLDataPropertyAssertion.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.mm.parser.node;

import org.mm.parser.MappingMasterParser;
import org.mm.parser.NodeVisitor;

public
class ASTDataPropertyAssertion extends SimpleNode {
  public ASTDataPropertyAssertion(int id) {
    super(id);
  }

  public ASTDataPropertyAssertion(MappingMasterParser p, int id) {
    super(p, id);
  }

  @Override
  public void accept(NodeVisitor visitor) { /* XXX: Manually added */
    visitor.visit(this);
  }
}
/* JavaCC - OriginalChecksum=c369c2455da33b620212db7314c88f86 (do not edit this line) */
