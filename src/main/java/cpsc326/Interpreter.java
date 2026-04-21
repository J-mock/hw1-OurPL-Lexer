package cpsc326;

import java.util.List;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Object>{
    // Think create static one to start, recurse when see new blocks
    private static Environment environment = new Environment();

    void interpret(List<Stmt> stmts) {

        for(Stmt stmt : stmts){
            try {
                Object value = evaluate(stmt);
                //System.out.println(stringify(value));
            } catch (RuntimeError e) {
                OurPL.runtimeError(e);
            }
        }
        
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch(expr.operator.type) {
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
            case BANG:
                return !isTruthy(right);
        }

        return null;
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator,"Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator,"Operandd must be numbers.");
    }

    private boolean isTruthy(Object object) {
        if (object == null){
            //System.out.println("istruthy null object");
            return false;
        } 
        if (object instanceof Boolean){
            //System.out.println("Boolean object found: " + object.toString());
            return (boolean)object;
        } 
        return true;
    }

    private boolean isEqual(Object left, Object right) {
        if (left == null && right == null) return true;
        if (left == null) return false;

        return left.equals(right);
    }

    private String stringify(Object object) {
        if (object == null) {
            return "nil";
        }

        if (object instanceof Double) {
            String text = object.toString();
            if(text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private Object evaluate(Stmt stmt){
        return stmt.accept(this);
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch(expr.operator.type){
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case BANG_EQUAL:
                return !isEqual(left,right);
            case EQUAL_EQUAL:
                return isEqual(left,right);
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }
                if (left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }
                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            
        }

        return null;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr){
        // Variable expression, for now just going to return the value the variable holds?
        return environment.get(expr.name);
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr){
        Object left = evaluate(expr.left);
        
        // And/Or expression
        switch(expr.operator.type){
            case OR:
                if(isTruthy(left)){
                    return left;
                }
                break;
            case AND:
                if(!isTruthy(left)){
                    return left;
                }
                break;
            default:
                System.out.println("Token not stored correct, expected AND/OR, recieved: " + expr.operator.lexeme);
                return null;
        }

        return evaluate(expr.right);
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr){
        
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);
        // Return value? We may need to return variable name though?
        // For now just going to return the value (literal value of the variable)
        // Feel like nothing should be returned for assignment interpretation
        return null;
    }



    //Overrides for STMT interface

    @Override
    public Object visitWhileStmt(Stmt.While stmt){

        if(isTruthy(evaluate(stmt.condition))){
            evaluate(stmt.body);
        }
        return stmt.condition;
    }

    @Override
    public Object visitIfStmt(Stmt.If stmt){
        if(isTruthy(stmt.condition)){
            return evaluate(stmt.thenBranch);
        }else if (stmt.elseBranch != null){
            return evaluate(stmt.elseBranch);
        }
        System.out.println("bad use of the if statement.");
        return null;
    }

    @Override
    public Object visitVarStmt(Stmt.Var stmt){
        // Need to establish an environment, call define?
        Object value = evaluate(stmt.initializer);

        environment.define(stmt.name.lexeme, value);

        return null;
    }

    @Override
    public Object visitBlockStmt(Stmt.Block stmt){
        Environment childEnvironment = new Environment(environment);
        evaluateBlock(stmt, environment);
        return stmt.statements;
    }

    private Object evaluateBlock(Stmt.Block stmt, Environment enclosing){
        // For passing in non global block
        Environment childEnvironment = new Environment(enclosing);
        
        Environment temp = environment;
        environment = childEnvironment;

        for(Stmt blockStmt : stmt.statements){
            evaluate(blockStmt);
        }

        environment = temp;
        
        return null;
    }

    @Override
    public Object visitPrintStmt(Stmt.Print stmt){
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return value;
    }

    @Override
    public Object visitExpressionStmt(Stmt.Expression stmt){
        return evaluate(stmt.expression);
    }

}

