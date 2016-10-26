package com.mvgreen.resolver;

import java.util.EmptyStackException;

public class DiscreteResolver implements Resolver{

    /** Данные байты - команды операций для стековой машины */

    private final static byte ZERO = 0;

    private final static byte ONE = 1 << 1;

    private final static byte NEGATION = 2 << 1;

    private final static byte CONJUNCTION = 3 << 1;

    private final static byte DISJUNCTION = 4 << 1;
    private final static byte ADDITION = 5 << 1;

    // Временно не используется
    //private final static byte SHEFFER = 6 << 1;
    //private final static byte PIERCE= 7 << 1;

    private final static byte IMPLICATION = 8 << 1;

    // Временно не используется
    //private final static byte INTERDICT = 9 << 1;

    private final static byte EQUIVALENCE = 10 << 1;

    // Особый знак, в итоговое выражение не входит
    private final static byte BRACE = 11 << 1;

    private byte[] expression;
    // Не более 31! т.к. вектор значений длиной 2^(кол-во переменных)
    private Variable[] variables;
    private Stack stack;

    /** DONE */
    public byte resolve(String expr, Variable[] vars) throws IncorrectExpressionException {
        variables = vars;
        expression = convert(expr);
        return resolve();
    }

    /** DONE */
    public byte resolve() throws IncorrectExpressionException {
        if (!uniqueVars())
            throw new IncorrectExpressionException("В декларации переменных присутствуют одинаковые имена!");
        calculate();
        if (stack.top != 1)
            throw new IncorrectExpressionException("Некорректное выражение!");
        else
            return stack.pop();
    }

    /** DONE */
    private boolean uniqueVars() {
        for (int i = 0; i < variables.length - 1; i++)
            for (int j = i + 1; j < variables.length; j++)
                if (variables[i].name == variables[j].name)
                    return false;
        return true;
    }

    /** Вычисляет значение, записанное в expression при помощи значений в variables */
    private void calculate(){
        initStack();
        byte x, y;
        for (byte b : expression) {
            switch (b){
                case ZERO:
                case ONE:
                    stack.push((byte) (b >> 1));
                    break;
                case NEGATION:
                    stack.push(not(stack.pop()));
                    break;
                case CONJUNCTION:
                    y = stack.pop();
                    x = stack.pop();
                    stack.push((byte) (x & y));
                    break;
                case DISJUNCTION:
                    y = stack.pop();
                    x = stack.pop();
                    stack.push((byte) (y | x));
                    break;
                case ADDITION:
                    y = stack.pop();
                    x = stack.pop();
                    stack.push((byte) ((x & not(y)) | (not(x) & y)));
                    break;
                case IMPLICATION:
                    y = stack.pop();
                    x = stack.pop();
                    stack.push((byte) (not(x) | y));
                    break;
                case EQUIVALENCE:
                    y = stack.pop();
                    x = stack.pop();
                    stack.push((byte) ((x & y) | (not(x) & not(y))));
                    break;
                default:
                    stack.push(variables[b >>> 1].value);
                    break;
            }
        }
    }

    /** В результате простой инверсии получается отрицательное число,
     *  чтобы получить отрицание, нужно прибавить к инверсии 2. */
    private byte not(byte x) {
        return (byte) (~x + 2);
    }

    /** DONE */
    private void initStack() {
        if (stack == null)
            stack = new Stack();
        else
            stack.clear();
    }

    private Stack operationBuffer = new Stack();
    private Stack temp = new Stack();


    /** DONE */
    /**
     * Переводит строку с выражением в массив, каждый элемент которого - команда стековой машине.
     * Если младший бит равен 1, то семь старших битов - индекс переменной,
     * если 0, то семь старших бит - номер операции (см. константы вверху).
     * Пропущенные конъюнкции вставляются между переменными.
     **/
    public byte[] convert(String expr) throws IncorrectExpressionException {
        boolean lastWasNumber = false;
        temp.clear();
        expr = '(' + expr + ')';
        for (char c : expr.toCharArray()) {
            switch (c){
                case '0':
                    if (lastWasNumber){
                        pushLowerPriority(CONJUNCTION);
                        operationBuffer.push(CONJUNCTION);
                    }
                    temp.push(ZERO);
                    lastWasNumber = true;
                    break;
                case '1':
                    if (lastWasNumber){
                        pushLowerPriority(CONJUNCTION);
                        operationBuffer.push(CONJUNCTION);
                    }
                    temp.push(ONE);
                    lastWasNumber = true;
                    break;
                case '¬':
                    if (lastWasNumber){
                        pushLowerPriority(CONJUNCTION);
                        operationBuffer.push(CONJUNCTION);
                    }
                    pushLowerPriority(NEGATION);
                    operationBuffer.push(NEGATION);
                    lastWasNumber = false;
                    break;
                case '∧':
                    pushLowerPriority(CONJUNCTION);
                    operationBuffer.push(CONJUNCTION);
                    lastWasNumber = false;
                    break;
                case '∨':
                    pushLowerPriority(DISJUNCTION);
                    operationBuffer.push(DISJUNCTION);
                    lastWasNumber = false;
                    break;
                case '+':
                    pushLowerPriority(DISJUNCTION); // Одинаковый приоритет
                    operationBuffer.push(ADDITION);
                    lastWasNumber = false;
                    break;
                case '→':
                    pushLowerPriority(IMPLICATION);
                    operationBuffer.push(IMPLICATION);
                    lastWasNumber = false;
                    break;
                case '↔':
                    pushLowerPriority(EQUIVALENCE);
                    operationBuffer.push(EQUIVALENCE);
                    lastWasNumber = false;
                    break;
                case '(':
                    if (lastWasNumber){
                        pushLowerPriority(CONJUNCTION);
                        operationBuffer.push(CONJUNCTION);
                    }
                    operationBuffer.push(BRACE); // Не может выталкивать другие знаки
                    lastWasNumber = false;
                    break;
                case ')':
                    pushLowerPriority(BRACE);
                    lastWasNumber = true;
                    break;
                default:
                    if (c == ' ')
                        break;
                    boolean found = false;
                    for (int i = 0; i < variables.length; i++) {
                        if (variables[i].name == c){
                            found = true;
                            if (lastWasNumber){
                                pushLowerPriority(CONJUNCTION);
                                operationBuffer.push(CONJUNCTION);
                                lastWasNumber = false;
                            }
                            temp.push((byte) ((i << 1) + 1));
                            break;
                        }
                    }
                    if (!found)
                        throw new IncorrectExpressionException("Переменной \"" + c + "\" не существует!");
                    lastWasNumber = true;
                    break;
            }
        }
        return temp.toByteArray();
    }

    /** DONE */
    @Override
    public byte[] resolveVector(String expression, char[] vs) throws IncorrectExpressionException{
        byte[] vector = new byte[power2(vs.length)];
        variables = new Variable[vs.length];
        for (int i = 0; i < vs.length; i++)
            variables[i] = new Variable(vs[i], (byte) 0);
        vector[0] = resolve(expression, variables);
        for (int i = 1; i < vector.length; i++) {
            for (int j = 0; j < variables.length; j++)
                variables[j].value = (byte) ((i >> (2 - j)) & 1);
            vector[i] = resolve();
        }
        return vector;
    }

    private int power2(int b) {
        int a = 1;
        for (int i = 0; i < b; i++)
            a *= 2;
        return a;
    }

    /** DONE */
    /** Выталкивает из буфера в итоговый массив все операторы с приоритетом меньшим или равным данному,
     * вытолкнутые скобки не попадают в результат
     **/
    private void pushLowerPriority(byte operator) {
        while (operationBuffer.top > 0) {
            byte b = operationBuffer.peek();
            if (b == BRACE && operator == BRACE){
                operationBuffer.pop();
                return;
            }
            if (b <= operator)
                temp.push(operationBuffer.pop());
            else return;
        }

    }

}

// Дженерики в Java не поддерживают примитивные типы данных, поэтому библиотечный класс не подходит.
class Stack {

    private byte[] stack;
    // Указывает на первый свободный индекс
    int top;

    Stack(){
        stack = new byte[10];
        top = 0;
    }

    void push(byte b){
        if (top == stack.length)
            extendStack();
        stack[top] = b;
        top++;
    }

    byte pop(){
        if (top == 0)
            throw new EmptyStackException();
        top--;
        return stack[top];
    }

    byte peek(){
        if (top == 0)
            throw new EmptyStackException();
        return stack[top - 1];
    }

    private void extendStack() {
        byte[] t = new byte[stack.length * 2];
        System.arraycopy(stack, 0, t, 0, stack.length);
        stack = t;
    }

    void clear() {
        top = 0;
    }

    /** Обрубает пустые индексы, нужен для convert() */
    byte[] toByteArray(){
        byte[] result = new byte[top];
        System.arraycopy(stack, 0, result, 0, top);
        return result;
    }
}