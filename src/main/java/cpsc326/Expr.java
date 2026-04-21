package cpsc326;

abstract class Expr {
  interface Visitor<R> {
    R visitBinaryExpr(Binary expr);

    R visitGroupingExpr(Grouping expr);

    R visitLiteralExpr(Literal expr);

    R visitUnaryExpr(Unary expr);

    R visitAssignExpr(Assign expr);

    R visitLogicalExpr(Logical expr);

    R visitVariableExpr(Variable expr);
  }

  static class Unary extends Expr {
    Unary(Token operator, Expr right) {
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnaryExpr(this);
    }

    final Token operator;
    final Expr right;
  }

  static class Binary extends Expr {
    Binary(Expr Left, Token Operator, Expr Right) {
      // TODO complete class
      this.operator = Operator;
      this.left = Left;
      this.right = Right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinaryExpr(this);
    }

    // TODO complete class
    final Token operator;
    final Expr left;
    final Expr right;
  }

  static class Grouping extends Expr {
    // TODO complete class

    // Assuming it is something like this
    Grouping(Expr group){
      this.expression = group;
    }

    @Override
    <R> R accept(Visitor<R> visitor){
      return visitor.visitGroupingExpr(this);
    }
    final Expr expression;
  }

  static class Literal extends Expr {
    // TODO complete class

    Literal(Object value){
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor){
      return visitor.visitLiteralExpr(this);
    }
    // Not sure if we really need to pass in the token itself to do this operation of if we could just pass in the value
    
    // Value should be an object? Could be a number of things, string, number, boolean, nil?
    final Object value;
  }

  static class Assign extends Expr {
    final Token name;
    final Expr value;

    Assign(Token name, Expr value){
      this.name = name;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitAssignExpr(this);
    }

  }

  static class Logical extends Expr {
    final Expr left;
    final Token operator;
    final Expr right;

    Logical(Expr left, Token operator, Expr right){
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor){
      return visitor.visitLogicalExpr(this);
    }

  }

  static class Variable extends Expr {
    final Token name;
    
    Variable(Token name){
      this.name = name;
    }

    @Override
    <R> R accept(Visitor<R> visitor){
      return visitor.visitVariableExpr(this);
    }
  }
  
  abstract <R> R accept(Visitor<R> visitor);
}
