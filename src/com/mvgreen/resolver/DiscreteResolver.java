package com.mvgreen.resolver;

import java.util.HashMap;
import java.util.Stack;

public class DiscreteResolver implements Resolver {

    /** Метод реализует стековую машину, подсчитывающую значение выражения для данного набора значений переменных.
     * Версия требует упрощения и оптимизации.
     * Для более удобного наследования типы K и V HashMap-а не указаны, в данном случае они являются
     * Character и Integer соответственно. */
    @Override
    public int resolve(String expression, HashMap values) throws IncorrectExpressionException {
        // Конвертирование в обратную польскую нотацию
        expression = convertToPostfix(expression);
        Stack<Character> stack = new Stack<>();
        // Все используемые операторы унарные или бинарные, a и b - переменные, хранящие операнды
        int a, b;
        for (char c : expression.toCharArray()) {
            switch (c){
                case '¬':
                    a = stack.pop();
                    if (a == 0)
                        stack.push((char) 1);
                    else
                        stack.push((char) 0);
                    break;
                case '∧':
                    b = stack.pop();
                    a = stack.pop();
                    stack.push((char)(a * b));
                    break;
                case '∨':
                    b = stack.pop();
                    a = stack.pop();
                    if (a == 1 || b == 1)
                        stack.push((char) 1);
                    else
                        stack.push((char) 0);
                    break;
                case '+':
                    b = stack.pop();
                    a = stack.pop();
                    if (a == b)
                        stack.push((char) 0);
                    else
                        stack.push((char) 1);
                    break;
                case '|':
                    b = stack.pop();
                    a = stack.pop();
                    if (a == 1 && b == 1)
                        stack.push((char) 0);
                    else
                        stack.push((char) 1);
                    break;
                case '↓':
                    b = stack.pop();
                    a = stack.pop();
                    if (a == 0 && b == 0)
                        stack.push((char) 1);
                    else
                        stack.push((char) 0);
                    break;
                case '→':
                    b = stack.pop();
                    a = stack.pop();
                    if (a == 1 && b == 0)
                        stack.push((char) 0);
                    else
                        stack.push((char) 1);
                    break;
                case '↛':
                    b = stack.pop();
                    a = stack.pop();
                    if (a == 1 && b == 0)
                        stack.push((char) 1);
                    else
                        stack.push((char) 0);
                    break;
                case '↔':
                    b = stack.pop();
                    a = stack.pop();
                    if (b == a)
                        stack.push((char) 1);
                    else
                        stack.push((char) 0);
                case '0':
                    stack.push((char) 0);
                case '1':
                    stack.push((char) 1);
                    break;
                default:
                    if (values.containsKey(c)) {
                        if ((int)values.get(c) == 0)
                            stack.push((char) 0);
                        else
                            stack.push((char) 1);
                    }
                    else
                        throw new IncorrectExpressionException("Can't find variable " + c);
                    break;
            }
        }
        return stack.pop();
    }

    @Override
    public String convertToPostfix(String expression) {
        StringBuilder s = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        stack.push('(');
        expression += ')';
        boolean b;
        char a;
        for (char c : expression.toCharArray()) {
            switch (c){
                // I
                case '¬':
                    b = true;
                    do {
                        if (stack.peek() == '¬')
                            s.append('¬');
                        else
                            b = false;
                    } while (b);
                    stack.push(c);
                    break;
                // II
                case '∧':
                    b = true;
                    do {
                        a = stack.peek();
                        if (a == '¬' || a == '∧')
                            s.append(stack.pop());
                        else
                            b = false;
                    } while (b);
                    stack.push(c);
                    break;
                // III
                case '∨':
                case '+':
                    b = true;
                    do {
                        a = stack.peek();
                        if (a == '¬' || a == '∧' || a == '∨' || a == '+')
                            s.append(stack.pop());
                        else
                            b = false;
                    } while (b);
                    stack.push(c);
                    break;
                // IV
                case '|':
                case '↓':
                    b = true;
                    do {
                        a = stack.peek();
                        if (a == '¬' || a == '∧' || a == '∨' || a == '+' || a == '|' || a == '↓')
                            s.append(stack.pop());
                        else
                            b = false;
                    } while (b);
                    stack.push(c);
                    break;
                // V
                case '→':
                case '↛':
                    b = true;
                    do {
                        a = stack.peek();
                        if (a == '¬' || a == '∧' || a == '∨' || a == '+' || a == '|' || a == '↓' || a ==  '→' || a == '↛')
                            s.append(stack.pop());
                        else
                            b = false;
                    } while (b);
                    stack.push(c);
                    break;
                // VI
                case '↔':
                    b = true;
                    do {
                        a = stack.peek();
                        if (a == '¬' || a == '∧' || a == '∨' || a == '+' || a == '|' || a == '↓' || a ==  '→' ||
                                a == '↛' || a == '↔')
                            s.append(stack.pop());
                        else
                            b = false;
                    } while (b);
                    stack.push(c);
                    break;
                // Не выталкиваемые ничем иным
                case '(':
                    stack.push(c);
                    break;
                case ')':
                    do {
                        a = stack.pop();
                        if (a != '(')
                            s.append(a);
                    } while (a != '(');
                    break;
                // Достижима только для символов переменных и цифр
                default:
                    if (c != ' ')
                        s.append(c);
                    break;
            }
        }
        return s.toString();
    }
}
