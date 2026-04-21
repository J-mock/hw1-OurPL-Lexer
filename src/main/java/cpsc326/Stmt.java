package cpsc326;

import java.util.ArrayList;
import java.util.List;

public abstract class Stmt {
    
    interface Visitor<R> {
        R visitBlockStmt(Block stmt);
        R visitExpressionStmt(Expression stmt);
        R visitIfStmt(If stmt);
        R visitPrintStmt(Print stmt);
        R visitVarStmt(Var stmt);
        R visitWhileStmt(While stnt);

    }

    static class Block extends Stmt{

        public List<Stmt> statements;

        // Empty constructor -- do not think need
        Block() {
            this.statements = new ArrayList<>();
        }

        // Paramaterized constructor
        Block(List<Stmt> statements) {
            this.statements = statements;
        }
        
        // accept function
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }
    }

    static class Expression extends Stmt {
        final Expr expression;

        Expression(Expr expression) {
            this.expression = expression;
        }

        // accept function
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }
    
    static class If extends Stmt {
        final Expr condition;    //The thing inside the if
        final Stmt thenBranch;    //Likely going to the the "if" part of an if statement
        final Stmt elseBranch;   //Likely going to be the 'then' part of an if statement (else?)

        If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        // accept statement
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }
    }

    static class Print extends Stmt{
        Expr expression;

        public Print(Expr expression) {
            this.expression = expression;
        }
        
        // accept function
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }
    }

    static class Var extends Stmt {
        Token name;
        Expr initializer;

        Var(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        //Accept function
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }
    }

    static class While extends Stmt {
        // Can hold an expression or a statement?
        // Feel like cant hold both so overload?
        final Expr condition;
        final Stmt body;  //List of statements? (Block?)

        While(Expr condition, Stmt body){
            this.condition = condition;
            this.body = body;
        }

        //accept function
        @Override
        <R> R accept(Visitor<R> visitor){
            return visitor.visitWhileStmt(this);
        }
    }   

    // abstract accept function
    abstract <R> R accept(Visitor<R> visitor);
}
