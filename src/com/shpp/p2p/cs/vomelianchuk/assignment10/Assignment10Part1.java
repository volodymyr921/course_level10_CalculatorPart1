package com.shpp.p2p.cs.vomelianchuk.assignment11;

import java.util.HashMap;

/**
 * Assignment10Part1.java
 * ----------------------
 * The program calculates simple arithmetic expressions,
 * accepts it and returns a value
 */
public class Assignment11Part1 {
    /**
     * Calculates an expression with parameters
     * and outputs the result to the console
     *
     * @param args The expression and its parameters
     */
    public static void main(String[] args) {
        String formula = null;

        FormulaTree formulaTree = parseFormula(args);

        System.out.println(calculate(formulaTree.getFormula()));
    }

    private static FormulaTree parseFormula(String[] data) {
        String formulaParse = data[0];
        formulaParse =formulaParse.replaceAll("\\s", "");
        HashMap<String, Double> variables = new HashMap<>();
        for (int i = 1; i < data.length; i++) {
            String variableAndValue = data[i].replaceAll("\\s", "");
            variables.put(getVariable(variableAndValue), getValue(variableAndValue));
        }
        for(String variable : variables.keySet()) {
            formulaParse = formulaParse.replaceAll(variable,String.valueOf(variables.get(variable)));
        }
        return new FormulaTree(formulaParse);
    }

    /**
     * Gets the parameter name
     *
     * @param variableAndValue The string with the parameter and its value
     * @return The parameter name
     */
    private static String getVariable(String variableAndValue) {
        return variableAndValue.split("=")[0];
    }

    /**
     * The value of the parameter is obtained
     *
     * @param variableAndValue The string with the parameter and its value
     * @return Parameter value
     */
    private static Double getValue(String variableAndValue) {
        return Double.valueOf(variableAndValue.split("=")[1]);
    }

    /**
     * Evaluates the given expression with its parameters
     *
     * @param formula The formula to be calculated
     * @return The result of the expression when the parameters are substituted
     */
    private static double calculate(String formula) {
        try {
            return  evaluate(formula);
        } catch (Exception e) {
            System.err.println("Incorrect FORMULA!");
            return Double.NaN;
        }
    }

    /**
     * Evaluates an expression with numbers without parameters,
     * respecting the precedence of signs
     *
     * @param formula The formula to be calculated
     * @return The result of the expression
     */
    private static double evaluate(String formula) {
        return new Object() {
            int pos = -1, character;

            /**
             * Gets the next character
             */
            void nextChar() {
                character = (++pos < formula.length()) ? formula.charAt(pos) : -1;
            }

            /**
             * Tests a character against the current character
             * @param symbol The character
             * @return Symbols match or not
             */
            boolean checkSymbol(int symbol) {
                if (character == symbol) {
                    nextChar();
                    return true;
                }
                return false;
            }

            /**
             * This method begins the evaluation of a mathematical expression
             *
             * @return The result expression
             */
            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < formula.length()) throw new RuntimeException("Unexpected: " + (char) character);
                return x;
            }

            /**
             * This method evaluates an expression that consists of addition and subtraction
             *
             * @return The result expression
             */
            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (checkSymbol('+')) x += parseTerm();
                    else if (checkSymbol('-')) x -= parseTerm();
                    else return x;
                }
            }

            /**
             * This method evaluates an expression
             * that consists of multiplication and division
             *
             * @return The result expression
             */
            double parseTerm() {
                double x = parsePow();
                for (; ; ) {
                    if (checkSymbol('*')) x *= parsePow();
                    else if (checkSymbol('/')) x /= parsePow();
                    else return x;
                }
            }

            /**
             * This method evaluates an expression
             * that consists of exponentiation
             *
             * @return The result expression
             */
            double parsePow() {
                double x = parseFactor();
                for (; ; ) {
                    if (checkSymbol('^')) x = Math.pow(x, parseFactor());
                    else return x;
                }
            }

            /**
             * This method evaluates expression factors such as numbers,
             * unary pluses, and minuses
             *
             * @return The result expression
             */
            double parseFactor() {
                if (checkSymbol('+')) return parseFactor();
                if (checkSymbol('-')) return -parseFactor();

                // Calculates an integer, regardless of whether it is an integer or a fraction
                double x;
                int startPos = this.pos;
                if ((character >= '0' && character <= '9') || character == '.') {
                    while ((character >= '0' && character <= '9') || character == '.') nextChar();
                    x = Double.parseDouble(formula.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) character);
                }
                return x;
            }
        }.parse();
    }
}
