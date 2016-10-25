package com.mvgreen;

import com.mvgreen.resolver.*;

/**
 * Поддерживаемые символы и их значения:
 * 0 - константа "ноль"
 * 1 - константа "один"
 * ¬ - отрицание
 * ∧ - конъюнкция (можно пропускать, программа автоматически вставляет его, если между переменными нет оператора)
 * ∨ - дизъюнкция
 * + - сложение по модулю 2 (XOR)
 * → - импликация
 * ↔ - эквиваленция
 * ( - открывающая скобка
 * ) - закрывающая скобка
 *
 * Любой другой символ будет рассматриваться как переменная.
 *
 * Ввиду необходимости использования отсутствующих на клавиатуре символов, ввод через консоль не предусмотрен
 * (позже будет добавлен GUI). Вместо этого выражение вводится прямо в исходном коде.
 * В строке expression нужно ввести выражение, в массиве vars - все используемые в выражении переменные.
 **/
public class Main {

    public static void main(String[] args) throws IncorrectExpressionException {
        String expression = "¬xy + ¬yz";
        char[] vars = {'x', 'y', 'z'};

        Resolver t = new DiscreteResolver();
        byte[] b = t.resolveVector(expression, vars);

        // Вывод вектора в консоль в виде таблицы
        for (char c: vars)
            System.out.print(c + " ");
        System.out.println("f");
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < vars.length; j++) {
                System.out.print((i >> (2-j)) & 1);
                System.out.print(" ");
            }
            System.out.println(b[i]);
        }
    }
}