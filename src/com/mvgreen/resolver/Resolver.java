package com.mvgreen.resolver;

public interface Resolver {

    /** Возвращает результат подсчета выражения для конкретного набора входных параметров*/
    byte resolve(String expression, Variable[] values) throws IncorrectExpressionException;

    /** Возвращает математическое выражение, конвертированное в обратную постфиксную запись*/
    byte[] convert(String expression) throws IncorrectExpressionException;

    /** Возвращает вектор значений функции */
    byte[] resolveVector(String expression, char[] variables) throws IncorrectExpressionException;

    byte[] resolveVector(String expression, Variable[] variables) throws IncorrectExpressionException;

    byte[] resolveVector(Function function) throws IncorrectExpressionException;

}
