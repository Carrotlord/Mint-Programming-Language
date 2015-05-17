package mint;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Implements Dijkstra's Shunting-Yard algorithm.
 * @author Jiangcheng Oliver Chu
 */
public class ShuntingYard {
    public static HashMap<Character, Integer> precedences = null;
    public static Set<Character> operators;
    
    private static boolean isInitialized = false;
    
    public enum Associativity {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT;
    }
    
    /** All mint operators.
     *  Characters chosen so that debugging output is easier to read.
     *  Assigned symbols are not representative of actual operator syntax.
     */
    /** Postfix ++ as "upwards arrow". */
    public static final char INCREMENT = '\u2191';
    /** Postfix -- as "downwards arrow". */
    public static final char DECREMENT = '\u2193';
    /** Object method/property access operator. */
    public static final char DEREFERENCE = '.';
    
    /** Unary minus as "circled minus". */
    public static final char NEGATE = '\u2296';
    /** Keyword not as "propositional negation". */
    public static final char NOT = '\u00ac';
    /** Keyword notb as ~. */
    public static final char BITWISE_NOT = '~';
    
    public static final char EXPONENTIATE = '^';
    
    public static final char CONSTRUCT_RATIO = ':';
    
    public static final char MULTIPLY = '*';
    public static final char DIVIDE = '/';
    public static final char REMAINDER = '%';
    
    /** Addition and string concatenation. */
    public static final char ADD = '+';
    public static final char SUBTRACT = '-';

    /** Shift left << as "much less than sign". */
    public static final char SHIFT_LEFT = '\u226a';
    /** Shift right >> as "much greater than sign". */
    public static final char SHIFT_RIGHT_ARITH = '\u226b';
    
    public static final char GREATER = '>';
    public static final char LESSER = '<';
    /** >= as "greater than or equal to sign". */
    public static final char GREATER_EQUAL = '\u2265';
    /** <= as "greater than or equal to sign". */
    public static final char LESSER_EQUAL = '\u2264';
    
    /** == as "identical to sign". */
    public static final char COMPARE_EQUAL = '\u2261';
    /** != as "not equal to sign". */
    public static final char NOT_EQUAL = '\u2260';
    
    /** Keyword andb as &. */
    public static final char BITWISE_AND = '&';
    
    /** Keyword xorb as $. */
    public static final char BITWISE_XOR = '$';
    
    /** Keyword orb as @. */
    public static final char BITWISE_OR = '@';
    
    /** Boolean and as "logical and sign". */
    public static final char AND = '\u2227';
    
    /** Boolean xor as "circled plus". */
    public static final char XOR = '\u2295';
    
    /** Boolean or as "logical or sign". */
    public static final char OR = '\u2228';
    
    /**
     * Converts infix notation to reverse polish notation.
     * See en.wikipedia.org/wiki/Shunting-yard_algorithm
     * for a pseudocode outline of this method.
     * @param infixExpression
     * @return RPN linked list
     */
    public static DenseLinkedList infixToRPN(String infixExpression)
           throws MintException {
        if (!isInitialized) {
            initialize();
        }
        DenseLinkedList stack = new DenseLinkedList();
        DenseLinkedList queue = new DenseLinkedList();
        for (char token : infixExpression.toCharArray()) {
            if ((token >= '0' && token <= '9') ||
                (token >= 'a' && token <= 'z') ||
                (token >= 'A' && token <= 'Z')) {
                queue.push(token);
            } else if (token == '(') {
                stack.push('(');
            } else if (token == ')') {
                boolean isSuccessful = false;
                for (;;) {
                    char possibleLeftParen;
                    try {
                        possibleLeftParen = stack.pop();
                    } catch (NoSuchElementException ex) {
                        isSuccessful = true;
                        break;
                    }
                    if (possibleLeftParen == '(') {
                        isSuccessful = true;
                        break;
                    } else {
                        queue.push(possibleLeftParen);
                    }
                }
                if (!isSuccessful) {
                    throw new MintException(
                          "Mismatched parentheses in expression " +
                          "(left parenthesis possibly missing).");
                }
            } else if (operators.contains(token)) {
                for (;;) {
                    Associativity tokenAssoc = associativityOf(token);
                    char popToken;
                    try {
                        popToken = stack.pop();
                    } catch (NoSuchElementException ex) {
                        break;
                    }
                    int precedence = precedenceOf(token);
                    int popPrecedence = precedenceOf(popToken);
                    boolean isParenthesis = popToken == '(' || popToken == ')';
                    boolean shouldOutputLeft = tokenAssoc ==
                            Associativity.LEFT_TO_RIGHT &&
                            precedence > popPrecedence;
                    boolean shouldOutputRight = tokenAssoc ==
                            Associativity.RIGHT_TO_LEFT &&
                            precedence >= popPrecedence;
                    if ((shouldOutputLeft || shouldOutputRight) &&
                        !isParenthesis) {
                        queue.push(popToken);
                    } else {
                        /* Put the operator back: */
                        stack.push(popToken);
                        break;
                    }
                }
                stack.push(token);
            } else {
                throw new MintException("Unrecognized token: " + token);
            }
        }
        for (;;) {
            char movedValue;
            try {
                movedValue = stack.pop();
            } catch (NoSuchElementException ex) {
                break;
            }
            if (movedValue == '(' || movedValue == ')') {
                throw new MintException(
                      "Mismatched parentheses in expression " +
                      "(possible: too many parentheses on either side).");
            }
            queue.push(movedValue);
        }
        return queue;
    }
    
    public static void initialize() {
        precedences = new HashMap<Character, Integer>();
        /* Highest precedence operators. */
        precedences.put(INCREMENT, 1);
        precedences.put(DECREMENT, 1);
        precedences.put(DEREFERENCE, 1);
        /* Precedence 2 operators. */
        precedences.put(NEGATE, 2);
        precedences.put(NOT, 2);
        precedences.put(BITWISE_NOT, 2);
        /* Precedence 3 operators. */
        precedences.put(EXPONENTIATE, 3);
        /* Precedence 4 operators. */
        precedences.put(CONSTRUCT_RATIO, 4);
        /* Precedence 5 operators. */
        precedences.put(MULTIPLY, 5);
        precedences.put(DIVIDE, 5);
        precedences.put(REMAINDER, 5);
        /* Precedence 6 operators. */
        precedences.put(ADD, 6);
        precedences.put(SUBTRACT, 6);
        /* Precedence 7 operators. */
        precedences.put(SHIFT_LEFT, 7);
        precedences.put(SHIFT_RIGHT_ARITH, 7);
        /* Precedence 8 operators. */
        precedences.put(GREATER, 8);
        precedences.put(LESSER, 8);
        precedences.put(GREATER_EQUAL, 8);
        precedences.put(LESSER_EQUAL, 8);
        /* Precedence 9 operators. */
        precedences.put(COMPARE_EQUAL, 9);
        precedences.put(NOT_EQUAL, 9);
        /* Precedence 10-12, bitwise operators. */
        precedences.put(BITWISE_AND, 10);
        precedences.put(BITWISE_XOR, 11);
        precedences.put(BITWISE_OR, 12);
        /* Precedence 13-15, boolean operators. */
        precedences.put(AND, 13);
        precedences.put(XOR, 14);
        precedences.put(OR, 15);
        operators = precedences.keySet();
        isInitialized = true;
    }

    /**
     * Returns the precedence of an operator.
     * 1 is the highest precedence (most tightly bound to operands).
     * @param operator 
     * @return precedence of operator, or -1 if not defined
     */
    public static int precedenceOf(char operator) {
        if (!isInitialized) {
            initialize();
        }
        if (precedences.containsKey(operator)) {
            return precedences.get(operator);
        } else {
            return -1;
        }
    }
    
    public static Associativity associativityOf(char operator) {
        return precedenceOf(operator) == 2 ? Associativity.RIGHT_TO_LEFT :
                                             Associativity.LEFT_TO_RIGHT;
    }
}
