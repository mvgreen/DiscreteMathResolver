package com.mvgreen.resolver;

public class Function {

    private String expression;
    private byte[] vector;
    private Variable[] variables;

    public Function(String expression, Variable... variables) throws IncorrectExpressionException{
        this.expression = expression;
        this.variables = variables;
        vector = DiscreteResolver.getInstance().resolveVector(this);
    }

    public String getExpression(){
        return expression;
    }

    public Variable[] getVars(){
        return variables;
    }
}
