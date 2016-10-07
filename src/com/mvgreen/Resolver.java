package com.mvgreen;

import java.util.HashMap;

public interface Resolver {

    /** Возвращает результат подсчета выражения для конкретного набора входных параметров*/
    int resolve(String expression, HashMap values) throws IncorrectExpressionException;

    /** Возвращает математическое выражение, конвертированное в обратную постфиксную запись*/
    String convertToPostfix(String expression);

}
