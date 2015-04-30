package mint;

import builtin.Constant;
import builtin.graphics.ButtonManager;
import builtin.graphics.MintWindow;
import builtin.graphics.Shape;
import builtin.list.Remove;
import builtin.string.Split;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @author Oliver Chu
 */
public class Interpreter {
    private SmartList<String> operators;
    private Environment env;
    private SmartList<Integer> loopProgramPointerStack;
    private SmartList<Boolean> ifStack;
    private SmartList<String> imports;
    
    public Interpreter() {
        String[] ops = {"=", "+=", "-=", "*=", "/=", "%=", "^=", "==", "!=",
                        "<", ">", "<=", ">=", "(", ")", "++", "--", ",", "[",
                        "]", "+", "-", "/", "*", "-/", "%", "^", ".", "//",
                        "/*", "*/", "..", "{", "}", "...", "?", ":"};
        operators = new SmartList<String>(ops);
        env = new Environment();
        loopProgramPointerStack = new SmartList<Integer>();
        ifStack = new SmartList<Boolean>();
        imports = new SmartList<String>();
    }
    
    public Environment getEnv() {
        return env;
    }
    
    public SmartList<String> getImports() {
        return imports;
    }
    
    public Pointer run(String programNameOrCode, boolean isDirectCode) throws
                                                                 MintException {
        SmartList<SmartList<Pointer>> pointerLists =
                                   loadProgram(programNameOrCode, isDirectCode);

        SmartList<String> _imports = new SmartList<String>();
        if (!isDirectCode) {
            imports.add(programNameOrCode);
        }
        return execute(pointerLists, _imports, new Environment());
    }
    
    private SmartList<Pointer> convertStringsToPointers(
                                                    SmartList<String> strings) {
        SmartList<Pointer> pointerList = new SmartList<Pointer>();
        for (String s : strings) {
            pointerList.add(evaluateString(s));
        }
        return pointerList;
    }
    
    private Pointer getArg(SmartList<Pointer> pointerList, int i, int argNum)
                                                          throws MintException {
        if (argNum == 0) {
            int end = ListTools.findFirst(pointerList, Constants.COMMA);
            int end2 = ListTools.findFirst(pointerList, Constants.CLOSE_PAREN);
/*            if (end == -1 && end2 == -1)
                return null;*/
            if (end == -1)
                end = pointerList.size();
            if (end2 == -1)
                end2 = pointerList.size();
            return evalExpression(new SmartList<Pointer>(
                   pointerList.subList(i, Math.max(i, Math.min(end, end2)))),
                                       0);
        } else {
            while (argNum > 0) {
                i = 1 + ListTools.findFirst(pointerList, Constants.COMMA);
                argNum--;
            }
            int end = i + ListTools.findFirst(pointerList.subList(i),
                                              Constants.COMMA);
            int end2 = i + ListTools.findFirst(pointerList.subList(i),
                                               Constants.CLOSE_PAREN);
/*            if (end == i - 1 && end2 == i - 1)
                return null;*/
            if (end == i - 1)
                end = pointerList.size();
            if (end2 == i - 1)
                end2 = pointerList.size();
            return evalExpression(new SmartList<Pointer>(
                   pointerList.subList(i, Math.min(end, end2))), 0);
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="Method loadProgram">
    public SmartList<SmartList<Pointer>> loadProgram(String programNameOrCode,
                                                      boolean isDirectCode)
                                                      throws MintException {
        SmartList<SmartList<String>> tokenLists =
                new SmartList<SmartList<String>>();
        SmartList<SmartList<Pointer>> pointerLists =
                new SmartList<SmartList<Pointer>>();
        String code = "";
        if (isDirectCode) {
            code = programNameOrCode;
        } else {
            code = FileIO.fileToStr(programNameOrCode);
        }
        code = code.replace("\t", "    ");
        code = code.replace("{", "[");
        code = code.replace("}", "]");
        int k = 0;
        String newCode = "";
        while (k < code.length()) {
            if (StrTools2.slice(code, k, k + 2).equals("\"\"")) {
                if (code.charAt(k - 1) == '\\') {
                    newCode += "\"";
                } else {
                    newCode += "\"a\".remove(\"a\")";
                    k++;
                }
            } else {
                newCode += code.charAt(k);
            }
            k++;
        }
        code = newCode;
        //code = code.replace("\"\"", "\"a\".remove(\"a\")");
        code = code.replace("\\\"\"", "\\q\"");
        code = code.replace("\\\\", "\\\\\\ ");
        boolean inQuotes = false;
        newCode = "";
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            if (c == ';') {
                if (!inQuotes) {
                    newCode += "\n";
                } else {
                    newCode += ";";
                }
            } else if (c == '"' && i - 1 > 0 && code.charAt(i - 1) != '\\') {
                inQuotes = !inQuotes;
                newCode += "\"";
            } else {
                newCode += c;
            }
        }
        code = newCode;
        code = code.replace("\r", "\n");
        code = code.replace("\n\n", "\n");
        String[] lines = code.split("\n");
        for (String line : lines) {
            tokenLists.add(new SmartList<String>(
                           StrTools.splitOnNoQuotes(line, operators)));
        }
        // Pad end statements with nulls:
        int j = 0;
        boolean skipNext = false;
        for (SmartList<String> list : tokenLists) {
            for (int i = 0; i < list.size(); i++) {
                if (!skipNext) {
                    if (list.get(i).equals("end")) {
                        SmartList<String> list1 = new SmartList<String>();
                        list1.add("null");
                        SmartList<String> list2 = new SmartList<String>();
                        list2.add("end");
                        SmartList<SmartList<String>> bigList =
                                             new SmartList<SmartList<String>>();
                        bigList.add(list1);
                        bigList.add(list2);
                        tokenLists.assignSublist(j, j + 1, bigList);
                        skipNext = true;
                    }
                } else {
                    skipNext = false;
                }
            }
            j++;
        }
        // Pad else statements with nulls:
        for (j = 0; j < tokenLists.size(); j++) {
            SmartList<String> list = tokenLists.get(j);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals("else")) {
                    SmartList<String> list1 = new SmartList<String>();
                    list1.add("null");
                    SmartList<SmartList<String>> bigList =
                                         new SmartList<SmartList<String>>();
                    bigList.add(list1);
                    bigList.add(list);
                    tokenLists.assignSublist(j, j + 1, bigList);
                    j++;
                }
            }
        }
        // Remove empty strings:
        j = 0;
        for (SmartList<String> list : tokenLists) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isEmpty()) {
                    list.remove(i);
                    i--;
                }
            }
            j++;
        }
        for (SmartList<String> list : tokenLists) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).contains(";") &&
                    !list.get(i).startsWith("\"")) {
                    list.set(i, list.get(i).replace(";", ""));
                    if (list.get(i).isEmpty()) {
                        list.remove(i);
                        i--;
                    }
                } else if (list.get(i).isEmpty()) {
                    list.remove(i);
                    i--;
                }
            }
        }
        for (int i = 0; i < tokenLists.size(); i++) {
            if (tokenLists.get(i).isEmpty()) {
                tokenLists.remove(i);
                i--;
            }
        }
        // Combine compound operators:
        for (SmartList<String> list : tokenLists) {
            for (String op : operators) {
                if (op.length() == 2) {
                    for (int i = 0; i < list.size() - 1; i++) {
                        boolean isLengthOne = list.get(i).length() == 1 &&
                                list.get(i + 1).length() == 1;
                        boolean isCompound = false;
                        if (isLengthOne) {
                            isCompound = list.get(i).charAt(0) ==
                                    op.charAt(0) &&
                                    list.get(i + 1).charAt(0) ==
                                    op.charAt(1);
                        }
                        if (isLengthOne && isCompound) {
                            list.assignSublist(i, i + 2, op);
                        }
                    }
                }
            }
        }
        // Remove line comments and block comments:
        int savedJ = 0;
        int savedI = 0;
        for (j = 0; j < tokenLists.size(); j++) {
            SmartList<String> list = tokenLists.get(j);
            int counter = 0;
            for (int i = 0; i < list.size(); i++) {
                String current = list.get(i);
                if (current.equals("//")) {
                    list = new SmartList<String>(list.subList(0, i));
                } else if (current.equals("/*")) {
                    savedJ = j;
                    savedI = i;
                } else if (current.equals("*/")) {
                    SmartList<String> list2 = list.subList(i + 1);
                    SmartList<String> list1 = new SmartList<String>(
                                     tokenLists.get(savedJ).subList(0, savedI));
                    SmartList<String> newList = new SmartList<String>();
                    newList.addAll(list1);
                    newList.addAll(list2);
                    tokenLists.assignSublist(savedJ, j + 1, newList);
                    j = savedJ;
                    i = savedI + 1;
                }
                counter++;
                if (counter > 10000)
                    break;
            }
        }
        //System.out.println(tokenLists);
        for (SmartList<String> list : tokenLists) {
            for (int i = 0; i < list.size(); i++) {
                String current = list.get(i);
                if (current.equals("[")) {
                    String[] paren = {"(", "["};
                    list.assignSublist(i, i + 1, new SmartList<String>(paren));
                    i++;
                } else if (current.equals("]")) {
                    String[] paren = {"]", ")"};
                    list.assignSublist(i, i + 1, new SmartList<String>(paren));
                }
            }
        }
        j = 0;
        for (SmartList<String> list : tokenLists) {
            for (int i = 0; i < list.size(); i++) {
                String current = list.get(i);
                if (current.equals("{")) {
/*                    SmartList<String> list1 = new SmartList<String>(
                                              list.subList(0, i));
                    SmartList<String> list2 = list.subList(i + 1);
                    SmartList<SmartList<String>> newList =
                                             new SmartList<SmartList<String>>();
                    newList.add(list1);
                    newList.add(list2);
                    tokenLists.assignSublist(j, j + 1, newList); */
                    list.remove(i);
                    i--;
                } else if (current.equals("}")) {
                    SmartList<String> list1 = new SmartList<String>(
                                              list.subList(0, i));
                    SmartList<String> list2 = new SmartList<String>();
                    SmartList<String> list3 = list.subList(i + 1);
                    list2.add("null");
                    list3.add(0, "end");
                    SmartList<SmartList<String>> newList =
                                             new SmartList<SmartList<String>>();
                    newList.add(list1);
                    newList.add(list2);
                    newList.add(list3);
                    tokenLists.assignSublist(j, j + 1, newList);
                }
            }
            j++;
        }
        //Delete ends before elses (the ends were curly braces):
        for (SmartList<String> list : tokenLists) {
            if (list.size() >= 2 && list.get(0).equals("end") &&
                    list.get(1).equals("else")) {
                list.assignSublist(0, 2, "else");
            }
        }
        //Combine else and if into elseif:
        for (SmartList<String> list : tokenLists) {
            if (list.size() >= 2 && list.get(0).equals("else") &&
                    list.get(1).equals("if")) {
                list.assignSublist(0, 2, "elseif");
            }
        }
        //Combine for and each into foreach:
        for (SmartList<String> list : tokenLists) {
            if (list.size() >= 2 && list.get(0).equals("for") &&
                    list.get(1).equals("each")) {
                list.assignSublist(0, 2, "foreach");
            }
        }
        // Combine floating point literals:
        for (SmartList<String> list : tokenLists) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).equals(".") || list.get(i).equals("-") &&
                        i > 0 && i < list.size() - 1) {
                    try {
                        String potentialReal = list.get(i - 1) +
                                list.get(i) + list.get(i + 1);
                        Double.parseDouble(potentialReal);
                        list.assignSublist(i - 1, i + 2, potentialReal);
                    } catch (NumberFormatException ex) {
                        // Ignore error.
                    } catch (IndexOutOfBoundsException ex) {
                        // Ignore error.
                    }
                }
            }
        }
        for (SmartList<String> tokenList : tokenLists) {
            pointerLists.add(convertStringsToPointers(tokenList));
        }
        return pointerLists;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Method convertKeywordToOperator">
    public static String convertKeywordToOperator(int keyword) {
        switch (keyword) {
            case Constants.PLUS:
                return "+";
            case Constants.MINUS:
                return "-";
            case Constants.MULTIPLY:
                return "*";
            case Constants.DIVIDE:
                return "/";
            case Constants.FLOOR_DIVIDE:
                return "-/";
            case Constants.MODULO:
                return "%";
            case Constants.POWER:
                return "^";
            case Constants.INPUT:
                return "input";
            case Constants.OPEN_PAREN:
                return "(";
            case Constants.CLOSE_PAREN:
                return ")";
            case Constants.DOT:
                return ".";
            case Constants.EQUAL:
                return "==";
            case Constants.NOT_EQUAL:
                return "!=";
            case Constants.GTR_THAN:
                return ">";
            case Constants.LESS_THAN:
                return "<";
            case Constants.GTR_OR_EQUAL:
                return ">=";
            case Constants.LESS_OR_EQUAL:
                return "<=";
            case Constants.QUOTE:
                return "\"";
            case Constants.ASSIGN:
                return "=";
            case Constants.PRINT:
                return "print";
            case Constants.AND:
                return "and";
            case Constants.OR:
                return "or";
            case Constants.XOR:
                return "xor";
            case Constants.NOT:
                return "not";
            case Constants.OPEN_BRACKET:
                return "[";
            case Constants.CLOSE_BRACKET:
                return "]";
            case Constants.COMMA:
                return ",";
            case Constants.DOUBLE_DOT:
                return "..";
            case Constants.SHOW:
                return "show";
            case Constants.IMPORT:
                return "import";
            case Constants.INTO:
                return "into";
            case Constants.IF:
                return "if";
            case Constants.END:
                return "end";
            case Constants.ELSE:
                return "else";
            case Constants.WHILE:
                return "while";
            case Constants.BREAK:
                return "break";
            case Constants.CONTINUE:
                return "continue";
            case Constants.REPEAT:
                return "repeat";
            case Constants.RETURN:
                return "return";
            case Constants.SUB:
                return "sub";
            case Constants.FOR:
                return "for";
            case Constants.WHEN:
                return "when";
            case Constants.FOREACH:
                return "foreach";
            case Constants.ELSEIF:
                return "elseif";
            case Constants.INCREMENT:
                return "++";
            case Constants.DECREMENT:
                return "--";
            case Constants.RUN:
                return "run";
            case Constants.BLOCK:
                return "block";
            case Constants.LEAVE:
                return "leave";
            case Constants.INHERIT:
                return "inherit";
            case Constants.OF:
                return "of";
            case Constants.IN:
                return "in";
            case Constants.PLUS_ASSIGN:
                return "+=";
            case Constants.MINUS_ASSIGN:
                return "-=";
            case Constants.MULTIPLY_ASSIGN:
                return "*=";
            case Constants.DIVIDE_ASSIGN:
                return "/=";
            case Constants.MODULO_ASSIGN:
                return "%=";
            case Constants.POWER_ASSIGN:
                return "^=";
            case Constants.ELLIPSIS:
                return "...";
            default:
                return "<keyword " + keyword + ">";
        }
    }
    //</editor-fold>
    
    /** Parses a string into a value and returns a pointer to that value. */
    //<editor-fold defaultstate="collapsed" desc="Method evaluate string">
    private Pointer evaluateString(String s) {
        if (operators.contains(s)) {
            if (s.equals("+")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.PLUS);
            } else if (s.equals("-")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.MINUS);
            } else if (s.equals("*")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.MULTIPLY);
            } else if (s.equals("^")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.POWER);
            } else if (s.equals("/")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.DIVIDE);
            } else if (s.equals("-/")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                        Constants.FLOOR_DIVIDE);
            } else if (s.equals("%")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.MODULO);
            } else if (s.equals("(")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                        Constants.OPEN_PAREN);
            } else if (s.equals(")")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                        Constants.CLOSE_PAREN);
            } else if (s.equals(".")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.DOT);
            } else if (s.equals("\"")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.QUOTE);
            } else if (s.equals("=")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.ASSIGN);
            } else if (s.equals("+=")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                                   Constants.PLUS_ASSIGN);
            } else if (s.equals("-=")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                                   Constants.MINUS_ASSIGN);
            } else if (s.equals("*=")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                                   Constants.MULTIPLY_ASSIGN);
            } else if (s.equals("/=")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                                   Constants.DIVIDE_ASSIGN);
            } else if (s.equals("%=")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                                   Constants.MODULO_ASSIGN);
            } else if (s.equals("^=")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                                   Constants.POWER_ASSIGN);
            } else if (s.equals("==")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.EQUAL);
            } else if (s.equals("!=")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.NOT_EQUAL);
            } else if (s.equals(">")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.GTR_THAN);
            } else if (s.equals("<")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.LESS_THAN);
            } else if (s.equals(">=")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                        Constants.GTR_OR_EQUAL);
            } else if (s.equals("<=")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                        Constants.LESS_OR_EQUAL);
            } else if (s.equals("++")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.INCREMENT);
            } else if (s.equals("--")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.DECREMENT);
            } else if (s.equals("..")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                        Constants.DOUBLE_DOT);
            } else if (s.equals(",")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.COMMA);
            } else if (s.equals("[")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                        Constants.OPEN_BRACKET);
            } else if (s.equals("]")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                        Constants.CLOSE_BRACKET);
            } else if (s.equals("//")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                                   Constants.LINE_COMMENT);
            } else if (s.equals("{")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                                   Constants.OPEN_BRACE);
            } else if (s.equals("}")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                                   Constants.CLOSE_BRACE);
            } else if (s.equals("?")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                                   Constants.YIELD);
            } else if (s.equals(":")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                                   Constants.OTHERWISE);
            }
            /* ... etc. */
        } else {
            if (s.equals("true")) {
                return new Pointer(Constants.TRUTH_TYPE, 1);
            } else if (s.equals("false")) {
                return new Pointer(Constants.TRUTH_TYPE, 0);
            } else if (s.equals("null")) {
                return new Pointer(Constants.NULL_TYPE, 0);
            } else if (s.equals("input")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.INPUT);
            } else if (s.equals("print")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.PRINT);
            } else if (s.equals("show")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.SHOW);
            } else if (s.equals("when")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.WHEN);
            } else if (s.equals("if")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.IF);
            } else if (s.equals("end")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.END);
            } else if (s.equals("else")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.ELSE);
            } else if (s.equals("elseif")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.ELSEIF);
            } else if (s.equals("while")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.WHILE);
            } else if (s.equals("repeat")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.REPEAT);
            } else if (s.equals("continue")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.CONTINUE);
            } else if (s.equals("break")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.BREAK);
            } else if (s.equals("return")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.RETURN);
            } else if (s.equals("sub")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.SUB);
            } else if (s.equals("and")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.AND);
            } else if (s.equals("or")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.OR);
            } else if (s.equals("xor")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.XOR);
            } else if (s.equals("not")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.NOT);
            } else if (s.equals("run")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.RUN);
            } else if (s.equals("block")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.BLOCK);
            } else if (s.equals("leave")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.LEAVE);
            } else if (s.equals("inherit")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.INHERIT);
            } else if (s.equals("import")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.IMPORT);
            } else if (s.equals("into")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.INTO);
            } else if (s.equals("for")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.FOR);
            } else if (s.equals("foreach")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.FOREACH);
            } else if (s.equals("of")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.OF);
            } else if (s.equals("in")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.IN);
            } else if (s.equals("erase")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.ERASE);
            } else if (s.equals("all")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.ALL);
            } else if (s.equals("except")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.EXCEPT);
            } else if (s.equals("switch")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.SWITCH);
            } else if (s.equals("case")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.CASE);
            } else if (s.equals("default")) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.DEFAULT);
            } else if (s.startsWith("\"") && s.endsWith("\"")) {
                String str = s.substring(1, s.length() - 1);
                return Heap.allocateString(StrTools.unescape(str));
            } else if (s.equals("given")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                                   Constants.GIVEN);
            } else if (s.equals("yield")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                                   Constants.YIELD);
            } else if (s.equals("otherwise")) {
                return new Pointer(Constants.KEYWORD_TYPE,
                                   Constants.OTHERWISE);
            }
            try {
                int i = Integer.parseInt(s);
                return new Pointer(Constants.INT_TYPE, i);
            } catch (NumberFormatException ex1) {
                try {
                    double d = Double.parseDouble(s);
                    return Heap.allocateReal(d);
                } catch (NumberFormatException ex2) {
                    //We cannot parse this literal, so it must be a name.
                    return Heap.allocateName(s);
                }
            }
        }
        // This line will never be executed:
        return null;
    }
    //</editor-fold>
    
    /** Converts a name pointer into a value pointer. If the pointer is not
     * a name pointer, simply returns it.
     */
    private Pointer evaluateName(Pointer p, Environment env)
                                 throws MintException {
        if (p == null || p.type != Constants.NAME_TYPE)
            return p;
        String name = PointerTools.dereferenceName(p);
        return env.getValue(name);
    }
    
    private boolean shouldEvalParentheses(SmartList<Pointer> pointerList,
                                          Environment env) {
        boolean hasParentheses = ListTools.containsPointer(pointerList,
                                 new Pointer(Constants.KEYWORD_TYPE,
                                             Constants.OPEN_PAREN));
        boolean isNotDefinition = !ListTools.containsPointer(pointerList,
                            new Pointer(Constants.KEYWORD_TYPE, Constants.SUB));
        int beforeParenIndex = Math.max(0, ListTools.findFirst(pointerList,
                                        Constants.OPEN_PAREN) - 1);
        String name = null;
        if (pointerList.get(beforeParenIndex) != null)
            name = PointerTools.dereferenceName(
                                             pointerList.get(beforeParenIndex));
        boolean isNotSubprogramCall = name == null ||
                           (Environment.deepSearchGetValue(name, env) != null &&
                            Environment.deepSearchGetValue(name, env).type != 
                            Constants.SUBPROGRAM_TYPE &&
                            pointerList.get(beforeParenIndex).type !=
                            Constants.SUBPROGRAM_TYPE &&
                            !Constants.BUILTIN_METHODS.contains(name));
        return hasParentheses && isNotDefinition && isNotSubprogramCall;
    }
    
    private boolean shouldEvalDot(SmartList<Pointer> pointerList) {
        int i = ListTools.findFirst(pointerList, Constants.DOT);
        int j = ListTools.findFirst(pointerList, Constants.ASSIGN);
        if (i == -1)
            return false;
        if (j == -1)
            return true;
        return j < i;
    }
    
    private boolean shouldEvalIndex(SmartList<Pointer> pointerList) {
        int i = ListTools.findFirstType(pointerList, Constants.LIST_TYPE);
        int j = ListTools.findFirst(pointerList, Constants.ASSIGN);
        if (i == -1)
            return false;
        if (j == -1)
            return true;
        return j < i;
    }
    
    private Pointer showOrPrint(SmartList<Pointer> pointerList, int i,
                                boolean isShow) throws MintException {
        SmartList<Pointer> sList = pointerList.subList(i + 1);
        Pointer result;
        if (sList.size() == 1)
            result = pointerList.get(i + 1);
        else
            result = evalExpression(sList, 0);
        result = evaluateName(result, env);
        if (result != null) {
            if (result.type == Constants.STR_TYPE) {
                if (isShow) {
                    System.out.print(PointerTools.dereferenceString(result));
                } else {
                    System.out.println(PointerTools.dereferenceString(result));
                }
            } else {
                if (isShow) {
                    System.out.print(result);
                } else {
                    System.out.println(result);
                }
            }
        } else {
            throw new MintException(
                     "Java null passed to print in expression: " + pointerList +
                     " with scope: " + env.pop());
        }
        return result;
    }
    
    private SmartList<Pointer> getArgNames(SmartList<Pointer> pointerList,
                                           int i) {
        pointerList = pointerList.subList(i);
        int first = ListTools.findFirst(pointerList, Constants.OPEN_PAREN);
        int next = ListTools.findMatchingCloseParen(pointerList, first + 1);
        SmartList<Pointer> args = new SmartList<Pointer>(
                                  pointerList.subList(first + 1, next));
        args = ListTools.removePointers(args, 
                          new Pointer(Constants.KEYWORD_TYPE, Constants.COMMA));
        return args;
    }
    
    private SmartList<Pointer> getArgs(SmartList<Pointer> pointerList,
                                       int start, int neededArgs)
                                       throws MintException {
        if (neededArgs == 0)
            return new SmartList<Pointer>();
        pointerList = pointerList.subList(start);
        int first = ListTools.findFirst(pointerList, Constants.OPEN_PAREN);
        int last = ListTools.findMatchingCloseParen(pointerList, first + 1);
        SmartList<Pointer> args =
                   new SmartList<Pointer>(pointerList.subList(first + 1, last));
        SmartList<SmartList<Pointer>> argLists =
                                      ListTools.splitListOnCommas(args);
        args = new SmartList<Pointer>();
        for (SmartList<Pointer> eachArgList : argLists) {
            Pointer value = evalExpression(eachArgList, 0);
            if (value != null) {
                args.add(value);
            }
        }
        return args;
    }
    
    private SmartList<Pointer> getElementsTable(SmartList<Pointer> pointerList,
                                           int start)
                                           throws MintException {
        pointerList = pointerList.subList(start);
        int first = ListTools.findFirst(pointerList, Constants.OPEN_BRACE);
        int last = ListTools.findMatchingCloseBracket(pointerList, first + 1);
        SmartList<Pointer> elems =
                   new SmartList<Pointer>(pointerList.subList(first + 1, last));
        SmartList<SmartList<Pointer>> elemLists =
                                      ListTools.splitListOnCommas(elems);
        elems = new SmartList<Pointer>();
        for (SmartList<Pointer> eachElemList : elemLists) {
            Pointer value = evalExpression(eachElemList, 0);
            if (value != null) {
                elems.add(value);
            }
        }
        return elems;
    }
    
    private SmartList<Pointer> getElements(SmartList<Pointer> pointerList,
                                           int start)
                                           throws MintException {
        pointerList = pointerList.subList(start);
        int first = ListTools.findFirst(pointerList, Constants.OPEN_BRACKET);
        int last = ListTools.findMatchingCloseBracket(pointerList, first + 1);
        SmartList<Pointer> elems =
                   new SmartList<Pointer>(pointerList.subList(first + 1, last));
        SmartList<SmartList<Pointer>> elemLists =
                                      ListTools.splitListOnCommas(elems);
        elems = new SmartList<Pointer>();
        for (SmartList<Pointer> eachElemList : elemLists) {
            Pointer value = evalExpression(eachElemList, 0);
            if (value != null) {
                elems.add(value);
            }
        }
        return elems;
    }
    
    private Subprogram defineSub(SmartList<Pointer> pointerList, int i) {
        String subName = PointerTools.dereferenceName(pointerList.get(i + 1));
        SmartList<Pointer> argNames = getArgNames(pointerList, i + 2);
        return new Subprogram(subName, argNames,
                              new SmartList<SmartList<Pointer>>());
    }
    
    private Block defineBlock(SmartList<Pointer> pointerList, int i) {
        String blockName = PointerTools.dereferenceName(pointerList.get(i + 1));
        return new Block(blockName, new SmartList<SmartList<Pointer>>());
    }
    
    //<editor-fold defaultstate="collapsed" desc="Method Apply Tier0 Keyword">
    private SmartList<Pointer> applyTier0Keyword(int keyword,
            SmartList<Pointer> pointerList, int i)
            throws MintException {
        switch (keyword) {
            case Constants.ASSIGN: {
                if (i - 1 == 0) {
                    String name = PointerTools.dereferenceName(
                            pointerList.get(i - 1));
                    Pointer value = evalExpression(
                            pointerList.subList(i + 1), 0);
                    value = evaluateName(value, env);
                    env.put(name, value);
                    SmartList<Pointer> pointers = new SmartList<Pointer>();
                    pointers.add(value);
                    return pointers;
                } else {
                    SmartList<Pointer> nameRef =
                            new SmartList<Pointer>(pointerList.subList(0, i));
                    nameRef = ListTools.removePointers(nameRef,
                            new Pointer(Constants.KEYWORD_TYPE,
                                    Constants.OPEN_PAREN));
                    nameRef = ListTools.removePointers(nameRef,
                            new Pointer(Constants.KEYWORD_TYPE,
                                    Constants.CLOSE_PAREN));
                    Pointer value = evalExpression(
                            pointerList.subList(i + 1), 0);
                    value = evaluateName(value, env);
                    int j = 0;
                    Pointer systemVar = Heap.allocateName("$-1");
                    while (j < nameRef.size()) {
                        Pointer nameRef0 = nameRef.get(j);
                        Pointer nameRef1 = null;
                        if (j + 1 < nameRef.size())
                            nameRef1 = nameRef.get(j + 1);
                        Pointer dot = new Pointer(
                                Constants.KEYWORD_TYPE, Constants.DOT);
                        if (nameRef0.type == Constants.LIST_TYPE) {
                            if (j + 1 == nameRef.size()) {
                                SmartList<Pointer> newNameRef =
                                        new SmartList<Pointer>();
                                newNameRef.add(systemVar);
                                newNameRef.add(nameRef0);
                                env.put(newNameRef, value);
                            } else {
                                SmartList<Pointer> pList =
                                        new SmartList<Pointer>();
                                Pointer newSystemVar =
                                        Heap.getNextSystemVarName();
                                pList.add(newSystemVar);
                                pList.add(new Pointer(Constants.KEYWORD_TYPE,
                                        Constants.ASSIGN));
                                pList.add(systemVar);
                                pList.add(nameRef0);
                                evalExpression(pList, 0);
                                systemVar = newSystemVar;
                            }
                            j++;
                        } else if (nameRef1 != null &&
                                nameRef1.type == Constants.LIST_TYPE) {
                            if (j + 2 == nameRef.size()) {
                                env.put(nameRef, value);
                            } else {
                                SmartList<Pointer> pList =
                                        new SmartList<Pointer>();
                                systemVar = Heap.getNextSystemVarName();
                                pList.add(systemVar);
                                pList.add(new Pointer(Constants.KEYWORD_TYPE,
                                        Constants.ASSIGN));
                                pList.add(nameRef0);
                                pList.add(nameRef1);
                                evalExpression(pList, 0);
                            }
                            j += 2;
                        } else if (nameRef1 != null && nameRef1.equals(dot)) {
                            if (j + 3 == nameRef.size() &&
                                    nameRef0.type != Constants.LIST_TYPE) {
                                env.put(nameRef, value);
                            } else {
                                Pointer nameRef2 = nameRef.get(j + 2);
                                SmartList<Pointer> pList =
                                        new SmartList<Pointer>();
                                systemVar = Heap.getNextSystemVarName();
                                pList.add(systemVar);
                                pList.add(new Pointer(Constants.KEYWORD_TYPE,
                                        Constants.ASSIGN));
                                pList.add(nameRef0);
                                pList.add(nameRef1);
                                pList.add(nameRef2);
                                evalExpression(pList, 0);
                            }
                            j += 3;
                        } else if (nameRef0.equals(dot)) {
                            if (j + 2 == nameRef.size()) {
                                SmartList<Pointer> newNameRef =
                                        new SmartList<Pointer>();
                                newNameRef.add(systemVar);
                                newNameRef.add(dot);
                                newNameRef.add(nameRef1);
                                env.put(newNameRef, value);
                            } else {
                                SmartList<Pointer> pList =
                                        new SmartList<Pointer>();
                                Pointer newSystemVar =
                                        Heap.getNextSystemVarName();
                                pList.add(newSystemVar);
                                pList.add(new Pointer(Constants.KEYWORD_TYPE,
                                        Constants.ASSIGN));
                                pList.add(systemVar);
                                pList.add(dot);
                                pList.add(nameRef1);
                                evalExpression(pList, 0);
                                systemVar = newSystemVar;
                            }
                            j += 2;
                        }
                        if (j == nameRef.size()) {
                            SmartList<Pointer> pList = new SmartList<Pointer>();
                            pList.add(systemVar);
                            pList.add(new Pointer(Constants.KEYWORD_TYPE,
                                    Constants.ASSIGN));
                            pList.add(value);
                            evalExpression(pList, 0);
                        }
                    }
                    SmartList<Pointer> pointers = new SmartList<Pointer>();
                    pointers.add(value);
                    return pointers;
                }
            } case Constants.PLUS_ASSIGN: {
                String name = PointerTools.dereferenceName(
                              pointerList.get(i - 1));
                Pointer value = evalExpression(
                                pointerList.subList(i + 1), 0);
                value = evaluateName(value, env);
                Pointer nameVal = evaluateName(pointerList.get(i - 1), env);
                env.put(name,
                       Operator.additionFamily(Constants.PLUS, nameVal, value));
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(value);
                return pointers;
            } case Constants.MINUS_ASSIGN: {
                String name = PointerTools.dereferenceName(
                              pointerList.get(i - 1));
                Pointer value = evalExpression(
                                pointerList.subList(i + 1), 0);
                value = evaluateName(value, env);
                Pointer nameVal = evaluateName(pointerList.get(i - 1), env);
                env.put(name,
                      Operator.additionFamily(Constants.MINUS, nameVal, value));
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(value);
                return pointers;
            } case Constants.MULTIPLY_ASSIGN: {
                String name = PointerTools.dereferenceName(
                              pointerList.get(i - 1));
                Pointer value = evalExpression(
                                pointerList.subList(i + 1), 0);
                value = evaluateName(value, env);
                Pointer nameVal = evaluateName(pointerList.get(i - 1), env);
                env.put(name, Operator.multiplicationFamily(
                        Constants.MULTIPLY, nameVal, value));
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(value);
                return pointers;
            } case Constants.DIVIDE_ASSIGN: {
                String name = PointerTools.dereferenceName(
                              pointerList.get(i - 1));
                Pointer value = evalExpression(
                                pointerList.subList(i + 1), 0);
                value = evaluateName(value, env);
                Pointer nameVal = evaluateName(pointerList.get(i - 1), env);
                env.put(name, Operator.multiplicationFamily(
                        Constants.DIVIDE, nameVal, value));
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(value);
                return pointers;
            } case Constants.MODULO_ASSIGN: {
                String name = PointerTools.dereferenceName(
                              pointerList.get(i - 1));
                Pointer value = evalExpression(
                                pointerList.subList(i + 1), 0);
                value = evaluateName(value, env);
                Pointer nameVal = evaluateName(pointerList.get(i - 1), env);
                env.put(name, Operator.multiplicationFamily(
                        Constants.MODULO, nameVal, value));
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(value);
                return pointers;
            } case Constants.POWER_ASSIGN: {
                String name = PointerTools.dereferenceName(
                              pointerList.get(i - 1));
                Pointer value = evalExpression(
                                pointerList.subList(i + 1), 0);
                value = evaluateName(value, env);
                Pointer nameVal = evaluateName(pointerList.get(i - 1), env);
                env.put(name, Operator.power(nameVal, value));
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(value);
                return pointers;
            } case Constants.PRINT: {
                Pointer result = showOrPrint(pointerList, i, false);
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(result);
                return pointers;
            } case Constants.SHOW: {
                Pointer result = showOrPrint(pointerList, i, true);
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(result);
                return pointers;
            } case Constants.WHEN: {
                Pointer result = evalExpression(pointerList.subList(i + 1), 0);
                if (!PointerTools.dereferenceTruth(result)) {
                    Message msg = new Message(Constants.ADVANCE_BY_2);
                    SmartList<Pointer> pointers = new SmartList<Pointer>();
                    pointers.add(Heap.allocateMessage(msg));
                    return pointers;
                } else {
                    SmartList<Pointer> pointers = new SmartList<Pointer>();
                    pointers.add(new Pointer(Constants.NULL_TYPE, 0));
                    return pointers;
                }
            } case Constants.IF: {
                Pointer result = evalExpression(pointerList.subList(i + 1), 0);
                if (!PointerTools.dereferenceTruth(result)) {
                    Message msg = new Message(Constants.CONTINUE_FALSE_IF);
                    SmartList<Pointer> pointers = new SmartList<Pointer>();
                    pointers.add(Heap.allocateMessage(msg));
                    return pointers;
                } else {
                    Message msg = new Message(Constants.PUSH_EMPTY_PTR);
                    SmartList<Pointer> pointers = new SmartList<Pointer>();
                    pointers.add(Heap.allocateMessage(msg));
                    return pointers;
                }
            } case Constants.GIVEN:
              case Constants.YIELD:
              case Constants.OTHERWISE: {
                i = ListTools.findFirst(pointerList, Constants.GIVEN);
                int j = ListTools.findFirst(pointerList, Constants.YIELD);
                int k = ListTools.findFirst(pointerList, Constants.OTHERWISE);
                SmartList<Pointer> condition = new SmartList<Pointer>(
                                               pointerList.subList(i + 1, j));
                Pointer yield1 = pointerList.get(j + 1);
                Pointer yield2 = pointerList.get(k + 1);
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                if (PointerTools.dereferenceTruth(evalExpression(condition, 0)))
                {
                    pointers.add(yield1);
                } else {
                    pointers.add(yield2);
                }
                return pointers;
            } case Constants.END: {
                Message msg = new Message(Constants.POP_PTR);
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(Heap.allocateMessage(msg));
                return pointers;
            } case Constants.ELSE: {
                Message msg = new Message(Constants.GO_AFTER_END_AND_POP);
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(Heap.allocateMessage(msg));
                return pointers;
            } case Constants.ELSEIF: {
                Message msg;
                if (ifStack.get(-1)) {
                    //If the last if was true, then we don't need to evaluate
                    //this elseif.
                    msg = new Message(Constants.GO_AFTER_END_AND_POP);
                } else {
                    Pointer result =
                            evalExpression(pointerList.subList(i + 1), 0);
                    if (PointerTools.dereferenceTruth(result)) {
                        msg = new Message(Constants.CONTINUE_TRUE_ELSEIF);
                    } else {
                        msg = new Message(Constants.CONTINUE_FALSE_ELSEIF);
                    }
                }
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(Heap.allocateMessage(msg));
                return pointers;
            } case Constants.WHILE: {
                Pointer result = evalExpression(pointerList.subList(i + 1), 0);
                Message msg;
                Boolean bool = PointerTools.dereferenceTruth(result);
                if (bool == null) {
                    throw new MintException("Non-truth value used for while " +
                              "loop condition in expression: " + pointerList);
                }
                if (bool) {
                    msg = new Message(Constants.PUSH_PTR);
                } else {
                    msg = new Message(Constants.GO_AFTER_END);
                }
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(Heap.allocateMessage(msg));
                return pointers;
            } case Constants.REPEAT: {
                Message msg;
                int count = PointerTools.dereferenceInt(
                        evalExpression(pointerList.subList(i + 1), 0));
                if (count <= 0)
                    msg = new Message(Constants.RESET_REPEAT_AND_GO_AFTER_END);
                else
                    msg = new Message(Constants.PUSH_PTR_AND_DECREMENT);
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(Heap.allocateMessage(msg));
                return pointers;
            } case Constants.CONTINUE: {
                Message msg = new Message(Constants.MSG_CONTINUE);
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(Heap.allocateMessage(msg));
                return pointers;
            } case Constants.BREAK: {
                Message msg = new Message(Constants.MSG_BREAK);
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(Heap.allocateMessage(msg));
                return pointers;
            } case Constants.LEAVE: {
                Message msg = new Message(Constants.MSG_LEAVE);
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(Heap.allocateMessage(msg));
                return pointers;
            } case Constants.RUN: {
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(new Pointer(Constants.NULL_TYPE, 0));
                String blockName = PointerTools.dereferenceName(
                        pointerList.get(i + 1));
                Block block = PointerTools.dereferenceBlock(
                        env.getValue(blockName));
                block.execute(env, imports, this);
                return pointers;
            } case Constants.RETURN: {
                // Always pop the last frame when returning from a subprogram.
                Message msg;
                if (pointerList.size() > 1) {
                    Pointer p = pointerList.get(i + 1);
                    String name = PointerTools.dereferenceName(p);
                    if (name != null && name.equals("this")) {
                        MintObject lastFrame = env.pop();
                        SmartList<Pointer> retnValue = new SmartList<Pointer>();
                        retnValue.add(Heap.allocateObject(lastFrame));
                        msg = new Message(Constants.RETURN_VALUE, retnValue);
                    } else {
                        SmartList<Pointer> retnValue = new SmartList<Pointer>();
                        Pointer p2 =
                                evalExpression(pointerList.subList(i + 1), 0);
                        MintObject lastFrame = env.pop();
                        if (p2.type == Constants.SUBPROGRAM_TYPE) {
                            Subprogram sub = PointerTools.dereferenceSub(p2);
                            sub.setVirtualArgs(lastFrame);
                            p2 = Heap.allocateSub(sub);
                        }
                        retnValue.add(p2);
                        msg = new Message(Constants.RETURN_VALUE, retnValue);
                    }
                } else {
                    env.pop();
                    SmartList<Pointer> retnValue = new SmartList<Pointer>();
                    retnValue.add(new Pointer(Constants.NULL_TYPE, 0));
                    msg = new Message(Constants.RETURN_VALUE, retnValue);
                }
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(Heap.allocateMessage(msg));
                return pointers;
            } case Constants.SUB: {
                Message msg;
                SmartList<Pointer> retnValue = new SmartList<Pointer>();
                retnValue.add(Heap.allocateSub(defineSub(pointerList, i)));
                msg = new Message(Constants.DEFINE_SUB, retnValue);
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(Heap.allocateMessage(msg));
                return pointers;
            } case Constants.BLOCK: {
                Message msg;
                SmartList<Pointer> retnValue = new SmartList<Pointer>();
                retnValue.add(Heap.allocateBlock(defineBlock(pointerList, i)));
                msg = new Message(Constants.DEFINE_BLOCK, retnValue);
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(Heap.allocateMessage(msg));
                return pointers;
            } case Constants.INHERIT: {
                MintObject obj = PointerTools.dereferenceObject(
                        evalExpression(pointerList.subList(i + 1), 0));
                if (obj == null) {
                    throw new MintException(
                            "Cannot inherit from non-object value.");
                }
                env.putAll(obj);
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(new Pointer(Constants.NULL_TYPE, 0));
                return pointers;
            } case Constants.IMPORT: {
                String fileName =
                        PointerTools.dereferenceString(pointerList.get(i + 1));
                if (fileName == null) {
                    String importName = PointerTools.dereferenceName(
                            pointerList.get(i + 1));
                    MintObject frame = null;
                    if (importName != null) {
                        if (importName.equals("operator")) {
                            frame = Import.importOperator();
                        } else if (importName.equals("math")) {
                            frame = Import.importMath();
                        } else if (importName.equals("type")) {
                            frame = Import.importType();
                        } else if (importName.equals("time")) {
                            frame = Import.importTime();
                        } else if (importName.equals("system")) {
                            frame = Import.importSystem();
                        } else if (importName.equals("file")) {
                            frame = Import.importFile();
                        } else if (importName.equals("web")) {
                            frame = Import.importWeb();
                        } else if (importName.equals("mint")) {
                            frame = Import.importMint();
                        } else if (importName.equals("graphics")) {
                            frame = Import.importGraphics();
                        } else if (importName.equals("thread")) {
                            frame = Import.importThread();
                        } else if (importName.equals("random")) {
                            frame = Import.importRandom();
                        } else {
                            throw new MintException("No library named " +
                                    importName);
                        }
                    } else {
                        throw new MintException("Import name does not exist.");
                    }
                    if (i + 2 < pointerList.size() &&
                            pointerList.get(i + 2).equals(new Pointer(
                                    Constants.KEYWORD_TYPE, Constants.INTO))) {
                        String objName =
                                PointerTools.dereferenceName(pointerList.get(i + 3));
                        env.put(objName, Heap.allocateObject(frame));
                    } else {
                        env.putAll(frame);
                    }
                } else {
                    if (!imports.contains(fileName)) {
                        imports.add(fileName);
                        if (i + 2 < pointerList.size() &&
                                pointerList.get(i + 2).equals(new Pointer(
                                        Constants.KEYWORD_TYPE, Constants.INTO))) {
                            String objName =
                                    PointerTools.dereferenceName(pointerList.get(
                                    i + 3));
                            MintObject newFrame = new MintObject();
                            env.addFrame(newFrame);
                            execute(loadProgram(fileName, false), imports, env);
                            MintObject frame = env.pop();
                            env.put(objName, Heap.allocateObject(frame));
                        } else {
                            execute(loadProgram(fileName, false), imports, env);
                        }
                    }
                }
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(new Pointer(Constants.NULL_TYPE, 0));
                return pointers;
            } case Constants.FOR: {
                Message msg = new Message(Constants.MSG_FOR);
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(Heap.allocateMessage(msg));
                return pointers;
            } case Constants.FOREACH: {
                Message msg = new Message(Constants.MSG_FOREACH);
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(Heap.allocateMessage(msg));
                return pointers;
            } case Constants.IN: {
                Pointer value = evaluateName(pointerList.get(i - 1), env);
                Pointer iterable = evaluateName(pointerList.get(i + 1), env);
                if (iterable != null) {
                    switch (iterable.type) {
                        case Constants.LIST_TYPE: {
                            SmartList<Pointer> list =
                                    PointerTools.dereferenceList(iterable);
                            pointerList.assignSublist(i - 1, i + 2,
                                    Heap.allocateTruth(
                                    ListTools.findPointerValue(list,
                                    value) != -1));
                            return pointerList;
                        } case Constants.STR_TYPE: {
                            String s = PointerTools.dereferenceString(iterable);
                            Pointer t = Heap.allocateTruth(s.contains(
                                    PointerTools.dereferenceString(value)));
                            pointerList.assignSublist(i - 1, i + 2, t);
                            return pointerList;
                        } case Constants.BYTES_TYPE: {
                            SmartList<Byte> bytes = PointerTools.
                                            dereferenceBytes(iterable);
                            Pointer t = Heap.allocateTruth(bytes.contains(
                                    (byte)(int)PointerTools.dereferenceInt(
                                    value)));
                            pointerList.assignSublist(i - 1, i + 2, t);
                            return pointerList;
                        } case Constants.OBJECT_TYPE: {
                            MintObject obj =
                                    PointerTools.dereferenceObject(iterable);
                            String key = PointerTools.dereferenceString(value);
                            Pointer t;
                            if (obj.keys().contains(key))
                                t = new Pointer(Constants.TRUTH_TYPE, 1);
                            else
                                t = new Pointer(Constants.TRUTH_TYPE, 0);
                            pointerList.assignSublist(i - 1, i + 2, t);
                            return pointerList;
                        } default: {
                            throw new MintException(iterable +
                                                    " is not iterable.");
                        }
                    }
                } else {
                    SmartList<Pointer> pointers = new SmartList<Pointer>();
                    pointers.add(Constants.MINT_NULL);
                    return pointers;
                }
            } case Constants.ERASE: {
                Pointer next = pointerList.get(i + 1);
                if (next.type == Constants.KEYWORD_TYPE &&
                    next.value == Constants.ALL) {
                    //Erase all variables, except...
                    SmartList<String> names = new SmartList<String>();
                    SmartList<Pointer> values = new SmartList<Pointer>();
                    if (i + 2 < pointerList.size()) {
                        Pointer further = pointerList.get(i + 2);
                        if (further.type == Constants.KEYWORD_TYPE &&
                            further.value == Constants.EXCEPT) {
                            //Save the following variables:
                            int j = i + 3;
                            for (; j < pointerList.size(); j += 2) {
                                Pointer n = pointerList.get(j);
                                String name = PointerTools.dereferenceName(n);
                                Pointer val = env.getValue(name);
                                names.add(name);
                                values.add(val);
                            }
                        }
                    }
                    int s = env.size();
                    env.eraseAll();
                    for (int k = 0; k < s; k++) {
                        env.addFrame(new MintObject());
                    }
                    int k = 0;
                    for (String name : names) {
                        env.put(name, values.get(k));
                        k++;
                    }
                    Heap.eraseAllExcept(values);
                } else {
                    int j = i + 1;
                    for (; j < pointerList.size(); j += 2) {
                        //Erase all these variables:
                        Pointer n = pointerList.get(j);
                        String name = PointerTools.dereferenceName(n);
                        env.remove(name);
                    }
                }
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(new Pointer(Constants.NULL_TYPE, 0));
                return pointers;
            } case Constants.SWITCH: {
                Message msg = new Message(Constants.MSG_SWITCH);
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(Heap.allocateMessage(msg));
                return pointers;
            } case Constants.CASE:
              case Constants.DEFAULT: {
                Message msg = new Message(Constants.GO_AFTER_END);
                SmartList<Pointer> pointers = new SmartList<Pointer>();
                pointers.add(Heap.allocateMessage(msg));
                return pointers;
            } default: {
                throw new MintException("Unknown tier 0 keyword: " +
                        convertKeywordToOperator(keyword));
            }
        }
    }
    //</editor-fold>
    
    public void setThisPointer(int value) {
        env.setThisPointer(value);
    }
    
    public void setThisPointerName(String value) {
        env.setThisPointerName(value);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Method eval dot">
    private Pointer evalDot(SmartList<Pointer> pointerList, int i) throws
            MintException {
        Pointer previous = pointerList.get(i - 1);
        if (pointerList.get(i - 1).type == Constants.KEYWORD_TYPE) {
            int kwd = PointerTools.dereferenceKeyword(previous);
            if (kwd == Constants.DOUBLE_DOT) {
                return new Pointer(Constants.KEYWORD_TYPE, Constants.ELLIPSIS);
            }
        }
        String name = PointerTools.dereferenceName(previous);
        Pointer obj = evaluateName(previous, env);
        if (obj == null || !Constants.OBJECT_LIKE.contains(obj.type)) {
            if (obj == null) {
                throw new MintException("Java null is not an object." + 
                                        " Expression is: " + pointerList);
            }
            throw new MintException(obj + " is not an object. Expression is: " +
                                    pointerList);
        }
        String member = PointerTools.dereferenceName(pointerList.get(i + 1));
        Pointer result = null;
        switch (obj.type) {
            case Constants.OBJECT_TYPE: {
                result = PointerTools.dereferenceObject(obj).get(member);
                if (result != null && result.type == Constants.SUBPROGRAM_TYPE)
                {
                    Subprogram sub = PointerTools.dereferenceSub(result);
                    sub.setThisPointer(env.getCurrentFrameIndex());
                    sub.setThisPointerName(name);
                    result = Heap.allocateSub(sub);
                }
                break;
            } case Constants.LIST_TYPE: {
                SmartList<Pointer> list = PointerTools.dereferenceList(obj);
                if (member.equals("length") || member.equals("size")) {
                    Subprogram sub = new Subprogram("size",
                            new SmartList<Pointer>(),
                            new Constant(Heap.allocateInt(list.size())));
                    result = Heap.allocateSub(sub);
                } else if (member.equals("pop")) {
                    Pointer last = list.pop();
                    Subprogram sub = new Subprogram("pop",
                            new SmartList<Pointer>(),
                            new Constant(last));
                    if (name != null) {
                        env.put(name, Heap.allocateList(list));
                    }
                    result = Heap.allocateSub(sub);
                } else if (member.equals("remove")) {
                    SmartList<Pointer> argNames = new SmartList<Pointer>();
                    argNames.add(Heap.allocateName("i"));
                    Subprogram sub = new Subprogram("remove", argNames,
                                                    new Remove(list));
                    if (name != null) {
                        Integer _i = PointerTools.dereferenceInt(
                                     getArg(pointerList, i + 3, 0));
                        if (_i == null) {
                            throw new MintException(
                                    "Bad argument for remove");
                        }
                        env.put(name, Heap.allocateList(list));
                    }
                    result = Heap.allocateSub(sub);
                } else if (member.equals("reverse")) {
                    Subprogram sub = new Subprogram("reverse",
                            new SmartList<Pointer>(),
                            new Constant(Heap.allocateList(list.reverse())));
                    result = Heap.allocateSub(sub);
                    if (name != null) {
                        env.put(name, Heap.allocateList(list));
                    }
                } else if (member.equals("find")) {
                    SmartList<Pointer> argNames = new SmartList<Pointer>();
                    argNames.add(Heap.allocateName("elem"));
                    if (i + 3 >= pointerList.size())
                        throw new MintException("No argument for find.");
                    Subprogram sub = new Subprogram("find", argNames,
                            new Constant(Heap.allocateInt(
                            ListTools.findPointerValue(list,
                            getArg(pointerList, i + 3, 0)))));
                    result = Heap.allocateSub(sub);
                } else if (member.equals("add") || member.equals("append")) {
                    SmartList<Pointer> argNames = new SmartList<Pointer>();
                    argNames.add(Heap.allocateName("item"));
                    if (i + 3 >= pointerList.size())
                        throw new MintException("No argument for add.");
                    list.add(getArg(pointerList, i + 3, 0));
                    Subprogram sub = new Subprogram("append", argNames,
                            new Constant(Heap.allocateList(list)));
                    result = Heap.allocateSub(sub);
                    if (name != null) {
                        env.put(name, Heap.allocateList(list));
                    }
                } else if (member.equals("sliceToEnd")) {
                    int start = PointerTools.dereferenceInt(
                                getArg(pointerList, i + 3, 0));
                    SmartList<Pointer> slice = list.subList(start);
                    SmartList<Pointer> argNames = new SmartList<Pointer>();
                    argNames.add(Heap.allocateName("start"));
                    Subprogram sub = new Subprogram("sliceToEnd", argNames,
                                     new Constant(Heap.allocateList(slice)));
                    result = Heap.allocateSub(sub);
                } else if (member.equals("slice")) {
                    int start = PointerTools.dereferenceInt(
                                getArg(pointerList, i + 3, 0));
                    Integer end = 0;
                    SmartList<Pointer> slice;
                    end = PointerTools.dereferenceInt(
                          getArg(pointerList, i + 3, 1));
                    if (end == null) {
                        slice = list.subList(start);
                    } else {
                        slice = new SmartList<Pointer>(
                                list.subList(start, end));
                    }
                    SmartList<Pointer> argNames = new SmartList<Pointer>();
                    argNames.add(Heap.allocateName("args"));
                    argNames.add(new Pointer(Constants.KEYWORD_TYPE,
                                 Constants.DOUBLE_DOT));
                    Subprogram sub = new Subprogram("slice", argNames,
                                     new Constant(Heap.allocateList(slice)));
                    result = Heap.allocateSub(sub);
                } else if (member.equals("replaceSlice")) {
                    int start = PointerTools.dereferenceInt(
                                             getArg(pointerList, i + 3, 0));
                    int end = PointerTools.dereferenceInt(
                                             getArg(pointerList, i + 3, 1));
                    Pointer sl = getArg(pointerList, i + 3, 2);
                    SmartList<Pointer> slice = PointerTools.dereferenceList(sl);
                    list.assignSublist(start, end, slice);
                    SmartList<Pointer> argNames = new SmartList<Pointer>();
                    argNames.add(Heap.allocateName("a"));
                    argNames.add(Heap.allocateName("b"));
                    argNames.add(Heap.allocateName("slice"));
                    Subprogram sub = new Subprogram("replaceSlice", argNames,
                                     new Constant(Heap.allocateList(list)));
                    result = Heap.allocateSub(sub);
                }
                break;
            } case Constants.STR_TYPE: {
                String str = PointerTools.dereferenceString(obj);
                if (member.equals("length") || member.equals("size")) {
                    Subprogram sub = new Subprogram("size",
                            new SmartList<Pointer>(),
                            new Constant(Heap.allocateInt(str.length())));
                    result = Heap.allocateSub(sub);
                } else if (member.equals("split")) {
                    SmartList<Pointer> argNames = new SmartList<Pointer>();
                    argNames.add(Heap.allocateName("substr"));
                    Subprogram sub = new Subprogram("split", argNames,
                                                    new Split(str));
                    result = Heap.allocateSub(sub);
                } else if (member.equals("charNum")) {
                    Subprogram sub = new Subprogram("charNum",
                            new SmartList<Pointer>(),
                            new Constant(Heap.allocateInt(
                            (int)str.charAt(0))));
                    result = Heap.allocateSub(sub);
                } else if (member.equals("find")) {
                    String substr = PointerTools.dereferenceString(
                                    getArg(pointerList, i + 3, 0));
                    int j;
                    for (j = 0; j < str.length(); j++) {
                        if (StrTools2.slice(str, j, j + substr.length()).
                            equals(substr)) {
                            break;
                        }
                    }
                    if (j == str.length())
                        j = -1;
                    SmartList<Pointer> argNames = new SmartList<Pointer>();
                    argNames.add(Heap.allocateName("substr"));
                    Subprogram sub = new Subprogram("find", argNames,
                                     new Constant(Heap.allocateInt(j)));
                    result = Heap.allocateSub(sub);
                } else if (member.equals("slice")) {
                    Pointer arg1 = getArg(pointerList, i + 3, 0);
                    int start = 0;
                    if (arg1 != null)
                        start = PointerTools.dereferenceInt(arg1);
                    Integer end = 0;
                    String slice;
                    Pointer arg2 = getArg(pointerList, i + 3, 1);
                    if (arg2 != null)
                        end = PointerTools.dereferenceInt(arg2);
                    if (arg2 == null || end == null) {
                        slice = StrTools2.slice(str, start);
                    } else {
                        slice = StrTools2.slice(str, start, end);
                    }
                    SmartList<Pointer> argNames = new SmartList<Pointer>();
                    argNames.add(Heap.allocateName("args"));
                    argNames.add(new Pointer(Constants.KEYWORD_TYPE,
                                 Constants.DOUBLE_DOT));
                    Subprogram sub = new Subprogram("slice", argNames,
                                     new Constant(Heap.allocateString(slice)));
                    result = Heap.allocateSub(sub);
                } else if (member.equals("sliceToEnd")) {
                    int start = PointerTools.dereferenceInt(
                                getArg(pointerList, i + 3, 0));
                    String slice = StrTools2.slice(str, start);
                    SmartList<Pointer> argNames = new SmartList<Pointer>();
                    argNames.add(Heap.allocateName("start"));
                    Subprogram sub = new Subprogram("sliceToEnd", argNames,
                                     new Constant(Heap.allocateString(slice)));
                    result = Heap.allocateSub(sub);
                } else if (member.equals("replaceSlice")) {
                    int start = PointerTools.dereferenceInt(
                                getArg(pointerList, i + 3, 0));
                    int end = PointerTools.dereferenceInt(
                              getArg(pointerList, i + 3, 1));
                    String slice = PointerTools.dereferenceString(
                                   getArg(pointerList, i + 3, 2));
                    str = StrTools2.slice(str, 0, start) + slice +
                          StrTools2.slice(str, end);
                    SmartList<Pointer> argNames = new SmartList<Pointer>();
                    argNames.add(Heap.allocateName("a"));
                    argNames.add(Heap.allocateName("b"));
                    argNames.add(Heap.allocateName("slice"));
                    Subprogram sub = new Subprogram("replaceSlice", argNames,
                                     new Constant(Heap.allocateString(str)));
                    result = Heap.allocateSub(sub);
                } else if (member.equals("upper")) {
                    str = str.toUpperCase();
                    Subprogram sub = new Subprogram("upper",
                                     new SmartList<Pointer>(),
                                     new Constant(Heap.allocateString(str)));
                    result = Heap.allocateSub(sub);
                } else if (member.equals("lower")) {
                    str = str.toLowerCase();
                    Subprogram sub = new Subprogram("lower",
                                     new SmartList<Pointer>(),
                                     new Constant(Heap.allocateString(str)));
                    result = Heap.allocateSub(sub);
                } else if (member.equals("startsWith")) {
                    String substr = PointerTools.dereferenceString(
                                    getArg(pointerList, i + 3, 0));
                    boolean b = str.startsWith(substr);
                    SmartList<Pointer> argNames = new SmartList<Pointer>();
                    argNames.add(Heap.allocateName("substr"));
                    Subprogram sub = new Subprogram("startsWith", argNames,
                                     new Constant(Heap.allocateTruth(b)));
                    result = Heap.allocateSub(sub);
                } else if (member.equals("endsWith")) {
                    String substr = PointerTools.dereferenceString(
                                    getArg(pointerList, i + 3, 0));
                    boolean b;
                    if (substr != null) {
                        b = str.endsWith(substr);
                    } else {
                        b = false;
                    }
                    SmartList<Pointer> argNames = new SmartList<Pointer>();
                    argNames.add(Heap.allocateName("substr"));
                    Subprogram sub = new Subprogram("endsWith", argNames,
                                     new Constant(Heap.allocateTruth(b)));
                    result = Heap.allocateSub(sub);
                } else if (member.equals("replace")) {
                    String substr = PointerTools.dereferenceString(
                                    getArg(pointerList, i + 3, 0));
                    String replacement = PointerTools.dereferenceString(
                                    getArg(pointerList, i + 3, 1));
                    SmartList<Pointer> argNames = new SmartList<Pointer>();
                    argNames.add(Heap.allocateName("substr"));
                    argNames.add(Heap.allocateName("replacement"));
                    Subprogram sub = new Subprogram("replace", argNames,
                                     new Constant(Heap.allocateString(
                                     str.replace(substr, replacement))));
                    result = Heap.allocateSub(sub);
                } else if (member.equals("remove")) {
                    String substr = PointerTools.dereferenceString(
                                    getArg(pointerList, i + 3, 0));
                    SmartList<Pointer> argNames = new SmartList<Pointer>();
                    argNames.add(Heap.allocateName("substr"));
                    Subprogram sub = new Subprogram("replace", argNames,
                                     new Constant(Heap.allocateString(
                                     str.replace(substr, ""))));
                    result = Heap.allocateSub(sub);
                }
                break;
            } case Constants.BYTES_TYPE: {
                SmartList<Byte> bytes = PointerTools.dereferenceBytes(obj);
                if (member.equals("length") || member.equals("size")) {
                    Subprogram sub = new Subprogram("size",
                            new SmartList<Pointer>(),
                            new Constant(Heap.allocateInt(bytes.size())));
                    result = Heap.allocateSub(sub);
                } else if (member.equals("add") || member.equals("append")) {
                    SmartList<Pointer> argNames = new SmartList<Pointer>();
                    argNames.add(Heap.allocateName("byte"));
                    if (i + 3 >= pointerList.size())
                        throw new MintException("No argument for add.");
                    bytes.add((byte)(int)
                            PointerTools.dereferenceInt(
                            getArg(pointerList, i + 3, 0)));
                    Subprogram sub = new Subprogram("append", argNames,
                            new Constant(Heap.allocateBytes(bytes)));
                    result = Heap.allocateSub(sub);
                    if (name != null) {
                        env.put(name, Heap.allocateBytes(bytes));
                    }
                } else if (member.equals("get")) {
                    SmartList<Pointer> argNames = new SmartList<Pointer>();
                    argNames.add(Heap.allocateName("i"));
                    if (i + 3 >= pointerList.size())
                        throw new MintException("No argument for get.");
                    byte item = bytes.get(PointerTools.dereferenceInt(
                                getArg(pointerList, i + 3, 0)));
                    Subprogram sub = new Subprogram("get", argNames,
                            new Constant(Heap.allocateInt((int)item)));
                    result = Heap.allocateSub(sub);
                } else if (member.equals("set")) {
                    SmartList<Pointer> argNames = new SmartList<Pointer>();
                    argNames.add(Heap.allocateName("i"));
                    argNames.add(Heap.allocateName("byte"));
                    if (i + 5 >= pointerList.size()) {
                        throw new MintException(
                                  "Not enough arguments for set.");
                    }
                    int j = PointerTools.dereferenceInt(
                            getArg(pointerList, i + 3, 0));
                    byte item = (byte)(int)
                            PointerTools.dereferenceInt(
                            getArg(pointerList, i + 3, 1));
                    bytes.set(j, item);
                    Subprogram sub = new Subprogram("set", argNames,
                            new Constant(Heap.allocateInt((int)item)));
                    result = Heap.allocateSub(sub);
                    if (name != null) {
                        env.put(name, Heap.allocateBytes(bytes));
                    }
                }
                break;
            } case Constants.WINDOW_TYPE: {
                MintWindow jf = PointerTools.dereferenceWindow(obj);
                if (member != null) {
                    if (member.equals("setSize")) {
                        int width = PointerTools.dereferenceInt(
                                    getArg(pointerList, i + 3, 0));
                        int height = PointerTools.dereferenceInt(
                                     getArg(pointerList, i + 3, 1));
                        jf.setSize(width, height);
                    } else if (member.equals("display")) {
                        jf.pack();
                        jf.setVisible(true);
                    } else if (member.equals("hide")) {
                        jf.setVisible(false);
                    } else if (member.equals("setTitle")) {
                        String title = PointerTools.dereferenceString(
                                       getArg(pointerList, i + 3, 0));
                        jf.setTitle(title);
                    } else if (member.equals("pack")) {
                        jf.pack();
                    } else if (member.equals("setLocation")) {
                        int x = PointerTools.dereferenceInt(
                                getArg(pointerList, i + 3, 0));
                        int y = PointerTools.dereferenceInt(
                                getArg(pointerList, i + 3, 1));
                        jf.setLocation(x, y);
                    } else if (member.equals("add")) {
                        Pointer p = getArg(pointerList, i + 3, 0);
                        if (p.type == Constants.BUTTON_TYPE) {
                            jf.add(PointerTools.dereferenceButton(p).
                                                getButton());
                        }
                    } else if (member.equals("setBgColor")) {
                        Pointer p = getArg(pointerList, i + 3, 0);
                        SmartList<Pointer> list = PointerTools.
                                                  dereferenceList(p);
                        int red = PointerTools.dereferenceInt(list.get(0));
                        int green = PointerTools.dereferenceInt(list.get(1));
                        int blue = PointerTools.dereferenceInt(list.get(2));
                        jf.setBackground(new Color(red, green, blue));
                    } else if (member.equals("setDrawingColor")) {
                        Pointer p = getArg(pointerList, i + 3, 0);
                        SmartList<Pointer> list = PointerTools.
                                                  dereferenceList(p);
                        int red = PointerTools.dereferenceInt(list.get(0));
                        int green = PointerTools.dereferenceInt(list.get(1));
                        int blue = PointerTools.dereferenceInt(list.get(2));
                        ArrayList<Integer> rbg = new ArrayList<Integer>();
                        rbg.add(red);
                        rbg.add(green);
                        rbg.add(blue);
                        jf.addShape(new Shape(Shape.COLOR, rbg));
                    } else if (member.equals("drawShape")) {
                        Shape s = PointerTools.dereferenceShape(
                                  getArg(pointerList, i + 3, 0));
                        jf.addShape(s);
                    } else if (member.equals("clearShapes")) {
                        jf.clearShapes();
                    }
                }
                env.put(name, Heap.allocateWindow(jf));
                SmartList<Pointer> argNames = new SmartList<Pointer>();
                argNames.add(Heap.allocateName("args"));
                argNames.add(new Pointer(Constants.KEYWORD_TYPE,
                             Constants.DOUBLE_DOT));
                Subprogram sub = new Subprogram(member, argNames,
                        new Constant(new Pointer(Constants.NULL_TYPE, 0)));
                result = Heap.allocateSub(sub);
                break;
            } case Constants.BUTTON_TYPE: {
                ButtonManager bm = PointerTools.dereferenceButton(obj);
                if (member.equals("enable")) {
                    bm.getButton().setEnabled(true);
                } else if (member.equals("disable")) {
                    bm.getButton().setEnabled(false);
                } else if (member.equals("setText")) {
                    String text = PointerTools.dereferenceString(
                                  getArg(pointerList, i + 3, 0));
                    bm.getButton().setText(text);
                } else if (member.equals("setSubprogram")) {
                    Subprogram sub = PointerTools.dereferenceSub(
                                     getArg(pointerList, i + 3, 0));
                    bm.addSubprogram(sub);
                } else if (member.equals("setSize")) {
                    int width = PointerTools.dereferenceInt(
                                getArg(pointerList, i + 3, 0));
                    int height = PointerTools.dereferenceInt(
                                 getArg(pointerList, i + 3, 1));
                    bm.getButton().setSize(new Dimension(width, height));
                    bm.getButton().setPreferredSize(
                            new Dimension(width, height));
                } else if (member.equals("setPosition")) {
                    int x = PointerTools.dereferenceInt(
                            getArg(pointerList, i + 3, 0));
                    int y = PointerTools.dereferenceInt(
                            getArg(pointerList, i + 3, 1));
                    Dimension size = bm.getButton().getPreferredSize();
                    bm.getButton().setBounds(x, y, size.width, size.height);
                }
                SmartList<Pointer> argNames = new SmartList<Pointer>();
                argNames.add(Heap.allocateName("args"));
                argNames.add(new Pointer(Constants.KEYWORD_TYPE,
                        Constants.DOUBLE_DOT));
                Subprogram sub = new Subprogram(member, argNames,
                        new Constant(new Pointer(Constants.NULL_TYPE, 0)));
                result = Heap.allocateSub(sub);
                break;
            } default: {
                throw new MintException("Unknown object type: " + obj.type);
            }
        }
        return result;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Method eval expression">
    public Pointer evalExpression(SmartList<Pointer> pointerList,
                                  int programPointer) throws MintException {
        //System.out.println(pointerList);
        if (pointerList.isEmpty()) {
            return new Pointer(Constants.NULL_TYPE, 0);
        }
        // Evaluate parentheses:
        while (shouldEvalParentheses(pointerList, env)) {
            int start = ListTools.findFirst(pointerList, Constants.OPEN_PAREN);
            int end = ListTools.findMatchingCloseParen(pointerList, start + 1);
            if (start != -1 && end != -1) {
                //Recursively evaluate what is inside the parentheses:
                SmartList<Pointer> subList = new SmartList<Pointer>(
                        pointerList.subList(start + 1, end));
                Pointer insideParens = evalExpression(subList, programPointer);
                pointerList.assignSublist(start, end + 1, insideParens);
            } else {
                throw new MintException(
                       "Mismatched parentheses for expression: " + pointerList);
            }
        }
        // Correct unary plus and minus signs:
        Pointer first = pointerList.get(0);
        if (first != null && first.type == Constants.KEYWORD_TYPE) {
            if (first.value == Constants.PLUS)
                pointerList.remove(0);
            else if (first.value == Constants.MINUS) {
                pointerList.remove(0);
                Pointer toBeNegated = evaluateName(pointerList.get(0), env);
                pointerList.set(0, Operator.negate(toBeNegated));
            }
        }
        int j = 1;
        while (j + 1 < pointerList.size()) {
            Pointer current = pointerList.get(j);
            if (current != null && current.type == Constants.KEYWORD_TYPE) {
                int keyword = PointerTools.dereferenceKeyword(current);
                if (Constants.OP_FAMILY.contains(keyword) &&
                        keyword != Constants.OPEN_PAREN &&
                        keyword != Constants.CLOSE_PAREN) {
                    Pointer next = pointerList.get(j + 1);
                    if (next.type == Constants.KEYWORD_TYPE) {
                        int keyword2 = PointerTools.dereferenceKeyword(next);
                        if (keyword2 == Constants.PLUS)
                            pointerList.remove(j + 1);
                        else if (keyword2 == Constants.MINUS) {
                            pointerList.remove(j + 1);
                            Pointer p = evaluateName(pointerList.get(j + 1),
                                    env);
                            pointerList.set(j + 1, Operator.negate(p));
                        }
                    }
                }
            }
            j++;
        }
        int simplificationLimit = 10000;
        int simplificationLevel = 0;
        Integer[] singleKwds = {Constants.INPUT, Constants.ELSE, Constants.END,
                                Constants.RETURN, Constants.CONTINUE,
                                Constants.BREAK, Constants.LEAVE,
                                Constants.DEFAULT};
        SmartList<Integer> singleKeywords = new SmartList<Integer>(singleKwds);
        int precedence = 6;
        int i = 0;
        while (pointerList.size() > 1 ||
                (pointerList.get(0) != null &&
                singleKeywords.contains(PointerTools.dereferenceKeyword(
                pointerList.get(0))))) {
            //System.out.println(pointerList);
            simplificationLevel++;
            if (simplificationLevel > simplificationLimit) {
                throw new MintException("Cannot simplify expression: " +
                        pointerList);
            }
            Pointer current = pointerList.get(i);
            String n = "";
            if (current != null)
                n = PointerTools.dereferenceName(current);
            if (n != null) {
                try {
                    Pointer value = env.getValue(n);
                    if (value != null &&
                        value.type == Constants.SUBPROGRAM_TYPE &&
                        i + 1 < pointerList.size() &&
                        PointerTools.dereferenceKeyword(pointerList.get(i + 1))
                        == Constants.OPEN_PAREN) {
                        Subprogram sub = PointerTools.dereferenceSub(value);
                        int neededArgs = sub.getArgNamesLength();
                        Pointer result = sub.execute(env, imports,
                                getArgs(pointerList, i + 1, neededArgs),
                                this);
                        int end =
                           ListTools.findMatchingCloseParen(pointerList, i + 2);
                        pointerList.assignSublist(i, end + 1, result);
                        //System.out.println("PLIST:" + pointerList);
                        i = -1;
                    }
                } catch (MintException ex) {
                    String msg = ex.getMessage();
                    if (!msg.endsWith("is not defined.")) {
                        throw new MintException(msg.replace("Error: ", ""));
                    }
                } catch (NullPointerException ex) {
                    
                }
            } else if (current.type == Constants.SUBPROGRAM_TYPE) {
                Integer keyword =
                        PointerTools.dereferenceKeyword(pointerList.get(i + 1));
                if (i + 1 < pointerList.size() && keyword != null &&
                        keyword == Constants.OPEN_PAREN) {
                    Subprogram sub = PointerTools.dereferenceSub(current);
                    int neededArgs = sub.getArgNamesLength();
                    Pointer result = sub.execute(env, imports,
                            getArgs(pointerList, i + 1, neededArgs),
                            this);
                    int end =
                            ListTools.findMatchingCloseParen(pointerList, i + 2);
                    pointerList.assignSublist(i, end + 1, result);
                    i = -1;
                }
            } else if (current.type == Constants.LIST_TYPE &&
                    shouldEvalIndex(pointerList)) {
                boolean inBounds = i - 1 >= 0;
                Pointer ptr = null;
                boolean isIndexable = false;
                if (inBounds) {
                    ptr = evaluateName(pointerList.get(i - 1), env);
                    isIndexable = ptr.type == Constants.LIST_TYPE ||
                            ptr.type == Constants.STR_TYPE ||
                            ptr.type == Constants.OBJECT_TYPE ||
                            ptr.type == Constants.BYTES_TYPE;
                }
                if (isIndexable) {
                    // Index strings:
                    if (ptr.type == Constants.STR_TYPE) {
                        String str = PointerTools.dereferenceString(
                                evaluateName(pointerList.get(i - 1), env));
                        int index = PointerTools.dereferenceInt(
                                evaluateName(PointerTools.dereferenceList(
                                pointerList.get(i)).get(0), env));
                        try {
                            if (index < 0)
                                index += str.length();
                            pointerList.assignSublist(i - 1, i + 1,
                                   Heap.allocateString("" + str.charAt(index)));
                        } catch (IndexOutOfBoundsException ex) {
                            throw new MintException("Index " + index +
                                    " is out of bounds. Must be between -" +
                                    str.length() + " and " +
                                    (str.length() - 1) + ", inclusive." +
                                    " Expression is: " + pointerList);
                        }
                    } else if (ptr.type == Constants.BYTES_TYPE) {
                        SmartList<Byte> bytes = PointerTools.dereferenceBytes(
                                evaluateName(pointerList.get(i - 1), env));
                        int index = PointerTools.dereferenceInt(
                                evaluateName(PointerTools.dereferenceList(
                                pointerList.get(i)).get(0), env));
                        try {
                            pointerList.assignSublist(i - 1, i + 1,
                                    Heap.allocateInt(bytes.get(index)));
                        } catch (IndexOutOfBoundsException ex) {
                            throw new MintException("Index " + index +
                                    " is out of bounds. Must be between -" +
                                    bytes.size() + " and " +
                                    (bytes.size() - 1) + ", inclusive." +
                                    " Expression is: " + pointerList);
                        }
                        // Index objects with a "get" method:
                    } else if (ptr.type == Constants.OBJECT_TYPE) {
                        MintObject obj = PointerTools.dereferenceObject(
                                evaluateName(pointerList.get(i - 1), env));
                        int index = PointerTools.dereferenceInt(
                                evaluateName(PointerTools.dereferenceList(
                                pointerList.get(i)).get(0), env));
                        try {
                            Subprogram get = PointerTools.dereferenceSub(
                                    obj.get("get"));
                            SmartList<Pointer> args = new SmartList<Pointer>();
                            args.add(Heap.allocateInt(index));
                            Pointer val = get.execute(env, imports, args, this);
                            pointerList.assignSublist(i - 1, i + 1, val);
                        } catch (IndexOutOfBoundsException ex) {
                            throw new MintException("Index " + index +
                                    " is out of bounds.");
                        }
                        // Index lists:
                    } else {
                        SmartList<Pointer> list =
                                PointerTools.dereferenceList(
                                evaluateName(pointerList.get(i - 1), env));
                        Integer index = PointerTools.dereferenceInt(
                                evaluateName(PointerTools.dereferenceList(
                                pointerList.get(i)).get(0), env));
                        if (ListTools.isTable(list)) {
                            Table t = ListTools.toTable(list);
                            Pointer idx = 
                                    evaluateName(PointerTools.dereferenceList(
                                    pointerList.get(i)).get(0), env);
                            try {
                                Pointer val = t.getValue(idx);
                                if (val.type == Constants.NULL_TYPE) {
                                    try {
                                        pointerList.assignSublist(i - 1, i + 1,
                                                               list.get(index));
                                    } catch (IndexOutOfBoundsException ex) {
                                        throw new MintException(
                                        "Index " + index +
                                        " is out of bounds. Must be between -" +
                                        list.size() + " and " +
                                        (list.size() - 1) + ", inclusive." +
                                        " Expression is: " + pointerList);
                                    }
                                } else {
                                    pointerList.assignSublist(i - 1, i + 1,
                                                              val);
                                }
                            } catch (IndexOutOfBoundsException ex) {
                                throw new MintException("Key " +
                                        idx.toString() +
                                        " does not exist for this table." +
                                        pointerList);
                            }
                        } else {
                            Pointer p = list.get(index);
                            if (p.type == Constants.KEYWORD_TYPE) {
                                int keyword = PointerTools.dereferenceKeyword(
                                                           p);
                                SmartList<Pointer> newList =
                                                   new SmartList<Pointer>();
                                newList.add(Heap.allocateList(list));
                                if (keyword == Constants.ELLIPSIS) {
                                    pointerList.assignSublist(i - 1, i + 1,
                                                              newList);
                                }
                            } else {
                                try {
                                    pointerList.assignSublist(i - 1, i + 1,
                                                              list.get(index));
                                } catch (IndexOutOfBoundsException ex) {
                                    throw new MintException("Index " + index +
                                        " is out of bounds. Must be between -" +
                                        list.size() + " and " +
                                        (list.size() - 1) + ", inclusive." +
                                        " Expression is: " + pointerList);
                                }
                            }
                        }
                    }
                }
            } else if (current.type == Constants.INT_TYPE ||
                       current.type == Constants.REAL_TYPE ||
                       current.type == Constants.NAME_TYPE) {
                if (current.type == Constants.NAME_TYPE)
                    current = evaluateName(current, env);
                if ((current.type == Constants.INT_TYPE ||
                    current.type == Constants.REAL_TYPE) &&
                    i + 1 < pointerList.size()) {
                    //desSystem.out.println(pointerList.size());
                    Pointer next = pointerList.get(i + 1);
                    if (next.type == Constants.INT_TYPE ||
                        next.type == Constants.REAL_TYPE ||
                        next.type == Constants.NAME_TYPE) {
                        pointerList.assignSublist(i, i + 2,
                                    Operator.multiplicationFamily(
                                    Constants.MULTIPLY, current, next));
                    }
                }
            }
            Integer keyword = null;
            if (current != null) {
                keyword = PointerTools.dereferenceKeyword(current);
            }
            if (keyword != null) {
                if (keyword == Constants.LINE_COMMENT) {
                    if (i == 0)
                        return Constants.MINT_NULL;
                    pointerList = new SmartList<Pointer>(
                                  pointerList.subList(0, i));
                    i = -1;
                } else if (keyword == Constants.INPUT) {
                    BufferedReader keyboard = new BufferedReader(
                            new InputStreamReader(System.in));
                    String input;
                    try {
                        input = keyboard.readLine();
                    } catch (IOException ex) {
                        throw new MintException("Cannot read user input.");
                    }
                    pointerList.set(i, Heap.allocateString(input));
                } else if (keyword == Constants.OPEN_BRACKET) {
                    // Build a list:
                    SmartList<Pointer> elems = getElements(pointerList, i);
                    int end =
                            ListTools.findMatchingCloseBracket(pointerList,
                            i + 1);
                    if (end == i + 1) {
                        pointerList.assignSublist(i, end + 1,
                                Heap.allocateList(new SmartList<Pointer>()));
                    } else {
                        pointerList.assignSublist(i, end + 1,
                                Heap.allocateList(elems));
                    }
                } else if (keyword == Constants.OPEN_BRACE) {
                    // Build a list:
                    SmartList<Pointer> elems = getElementsTable(pointerList, i);
                    int end =
                            ListTools.findMatchingCloseBrace(pointerList,
                            i + 1);
                    if (end == i + 1) {
                        pointerList.assignSublist(i, end + 1,
                                               Heap.allocateTable(new Table()));
                    } else {
                        Table t = new Table();
                        t.addAll(elems);
                        pointerList.assignSublist(i, end + 1,
                                                  Heap.allocateTable(t));
                    }
                } else if (Constants.UNARY_FAMILY.contains(keyword) ||
                        keyword == Constants.DOT &&
                        precedence <= 6) {
                    if (keyword == Constants.DOT)
                    {
                        if (shouldEvalDot(pointerList)) {
                            pointerList.assignSublist(i - 1, i + 2,
                                    evalDot(pointerList, i));
                        }
                    } else {
                        Pointer p;
                        Pointer result;
                        if (keyword == Constants.NOT) {
                            p = evaluateName(pointerList.get(i + 1), env);
                            result = Operator.not(p);
                        } else {
                            String name = PointerTools.dereferenceName(
                                    pointerList.get(i - 1));
                            p = evaluateName(pointerList.get(i - 1), env);
                            result = p;
                            if (keyword == Constants.INCREMENT) {
                                p = Operator.increment(p);
                            } else {
                                p = Operator.decrement(p);
                            }
                            env.put(name, p);
                        }
                        if (keyword == Constants.NOT)
                            pointerList.assignSublist(i, i + 2, result);
                        else {
                            pointerList = new SmartList<Pointer>();
                            pointerList.add(result);
                        }
                    }
                } else if (keyword == Constants.POWER && precedence <= 5) {
                    Pointer p1 = evaluateName(pointerList.get(i - 1), env);
                    Pointer p2 = evaluateName(pointerList.get(i + 1), env);
                    Pointer result = Operator.power(p1, p2);
                    pointerList.assignSublist(i - 1, i + 2, result);
                } else if (Constants.MULT_FAMILY.contains(keyword) &&
                        precedence <= 4) {
                    Pointer p1 = evaluateName(pointerList.get(i - 1), env);
                    Pointer p2 = evaluateName(pointerList.get(i + 1), env);
                    Pointer result = Operator.multiplicationFamily(keyword,
                            p1, p2);
                    pointerList.assignSublist(i - 1, i + 2, result);
                } else if (Constants.ADD_FAMILY.contains(keyword) &&
                        precedence <= 3) {
                    Pointer p1 = evaluateName(pointerList.get(i - 1), env);
                    Pointer p2 = evaluateName(pointerList.get(i + 1), env);
                    Pointer result = Operator.additionFamily(keyword, p1, p2);
                    pointerList.assignSublist(i - 1, i + 2, result);
                } else if (Constants.COMP_FAMILY.contains(keyword) &&
                        precedence <= 2) {
                    Pointer p1 = evaluateName(pointerList.get(i - 1), env);
                    Pointer p2 = evaluateName(pointerList.get(i + 1), env);
                    Pointer result = Operator.comparisonFamily(keyword, p1, p2);
                    pointerList.assignSublist(i - 1, i + 2, result);
                } else if (Constants.LOGIC_FAMILY.contains(keyword) &&
                        precedence <= 1) {
                    Pointer p1 = evaluateName(pointerList.get(i - 1), env);
                    Pointer p2 = evaluateName(pointerList.get(i + 1), env);
                    Pointer result = Operator.logicFamily(keyword, p1, p2);
                    pointerList.assignSublist(i - 1, i + 2, result);
                }  else if (Constants.TIER0_KEYWORDS.contains(keyword)) {
                    pointerList = applyTier0Keyword(keyword, pointerList, i);
                }
            }
            i++;
            if (i >= pointerList.size()) {
                i = 0;
                precedence--;
            }
        }
        if (pointerList.isEmpty())
            return Constants.MINT_NULL;
        return evaluateName(pointerList.get(0), env);
    }
    //</editor-fold>
    
    private int goAfterEnd(int programPointer, SmartList<SmartList<Pointer>>
                           pointerLists) {
        //Increment the program pointer to avoid the while loop or whatever
        //called this method.
        programPointer++;
        int neededEndStatements = 1;
        while (programPointer < pointerLists.size()) {
            SmartList<Pointer> currentList = pointerLists.get(programPointer);
            Pointer end = new Pointer(Constants.KEYWORD_TYPE, Constants.END);
            if (currentList.size() > 0 && currentList.get(0).equals(end)) {
                neededEndStatements--;
            }
            if (neededEndStatements <= 0) {
                return programPointer + 1;
            }
            if (currentList.size() > 0 &&
                ListTools.containsPointer(Constants.BLOCK_STARTERS,
                currentList.get(0))) {
                neededEndStatements++;
            }
            programPointer++;
        }
        return programPointer;
    }
    
    private int goAfterCase(int programPointer,
                        SmartList<SmartList<Pointer>> pointerLists, int value) {
        programPointer++;
        while (programPointer < pointerLists.size()) {
            SmartList<Pointer> line = pointerLists.get(programPointer);
            Pointer first = line.get(0);
            Integer kwd = PointerTools.dereferenceKeyword(first);
            if (kwd != null && kwd == Constants.CASE) {
                for (int i = 1; i < line.size(); i += 2) {
                    int j = PointerTools.dereferenceInt(line.get(i));
                    if (j == value)
                        return programPointer + 1;
                }
            } else if (kwd != null && kwd == Constants.DEFAULT) {
                return programPointer + 1;
            }
            programPointer++;
        }
        return programPointer;
    }
    
    private int continueFalseIf(int programPointer,
                                SmartList<SmartList<Pointer>>
                                pointerLists, boolean appendNull) {
        programPointer++;
        if (appendNull)
            loopProgramPointerStack.add(null);
        SmartList<Pointer> currentList = pointerLists.get(programPointer);
        SmartList<Pointer> endElseElseif = new SmartList<Pointer>();
        Pointer end = new Pointer(Constants.KEYWORD_TYPE, Constants.END);
        Pointer _else = new Pointer(Constants.KEYWORD_TYPE, Constants.ELSE);
        Pointer elseif = new Pointer(Constants.KEYWORD_TYPE, Constants.ELSEIF);
        endElseElseif.add(end);
        endElseElseif.add(_else);
        endElseElseif.add(elseif);
        while (currentList.size() >= 1 &&
               !ListTools.containsPointer(endElseElseif, currentList.get(0))) {
            if (ListTools.containsPointer(Constants.BLOCK_STARTERS,
                                          currentList.get(0))) {
                programPointer = goAfterEnd(programPointer, pointerLists);
            } else {
                programPointer++;
            }
            currentList = pointerLists.get(programPointer);
        }
        if (!pointerLists.get(programPointer).isEmpty()) {
            if (pointerLists.get(programPointer).get(0).equals(_else)) {
                programPointer++;
            } else if (pointerLists.get(programPointer).get(0).equals(end)) {
                programPointer++;
                loopProgramPointerStack.pop();
            }
        }
        return programPointer;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Method execute">
    public Pointer execute(SmartList<SmartList<Pointer>> pointerLists,
            SmartList<String> imports, Environment env)
            throws MintException {
        if (env != null)
            this.env = env;
        if (imports != null)
            this.imports = imports;
        int programPointer = 0;
        while (programPointer < pointerLists.size()) {
            //System.out.println("PP:" + programPointer);
            //System.out.println(pointerLists.get(programPointer));
            SmartList<Pointer> origPointerList = new SmartList<Pointer>();
            for (Pointer p : pointerLists.get(programPointer)) {
                origPointerList.add(p);
            }
            Pointer value = evalExpression(pointerLists.get(programPointer),
                                           programPointer);
            pointerLists.set(programPointer, origPointerList);
            if (value == null || value.type != Constants.MESSAGE_TYPE)
                programPointer++;
            else {
                Message msg = PointerTools.dereferenceMessage(value);
                switch (msg.message) {
                    case Constants.ADVANCE_BY_2:
                        programPointer += 2;
                        break;
                    case Constants.CONTINUE_FALSE_IF:
                        ifStack.add(false);
                        programPointer = continueFalseIf(programPointer,
                                pointerLists, true);
                        break;
                    case Constants.POP_PTR:
                        if (!loopProgramPointerStack.isEmpty()) {
                            Integer newPtr = loopProgramPointerStack.pop();
                            if (newPtr != null) {
                                programPointer = newPtr;
                            } else {
                                programPointer++;
                                ifStack.pop();
                            }
                        } else {
                            programPointer++;
                        }
                        break;
                    case Constants.PUSH_EMPTY_PTR:
                        ifStack.add(true);
                        loopProgramPointerStack.add(null);
                        programPointer++;
                        break;
                    case Constants.GO_AFTER_END_AND_POP:
                        programPointer = goAfterEnd(programPointer,
                                pointerLists);
                        if (loopProgramPointerStack.size() > 0 &&
                                loopProgramPointerStack.get(-1) == null) {
                            loopProgramPointerStack.pop();
                            ifStack.pop();
                        }
                        break;
                    case Constants.CONTINUE_TRUE_ELSEIF:
                        ifStack.set(-1, true);
                        programPointer++;
                        break;
                    case Constants.CONTINUE_FALSE_ELSEIF:
                        ifStack.set(-1, false);
                        programPointer = continueFalseIf(programPointer,
                                pointerLists, false);
                        break;
                    case Constants.GO_AFTER_END:
                        programPointer = goAfterEnd(programPointer,
                                                    pointerLists);
                        break;
                    case Constants.PUSH_PTR:
                        loopProgramPointerStack.add(programPointer);
                        programPointer++;
                        break;
                    case Constants.MSG_CONTINUE:
                        if (loopProgramPointerStack.isEmpty()) {
                            programPointer++;
                        } else {
                            int newPointer = loopProgramPointerStack.get(-1);
                            programPointer = newPointer + 1;
                        }
                        break;
                    case Constants.MSG_BREAK:
                        programPointer = goAfterEnd(programPointer,
                                pointerLists);
                        if (!loopProgramPointerStack.isEmpty()) {
                            loopProgramPointerStack.pop();
                        }
                        break;
                    case Constants.RETURN_VALUE:
                        return msg.contents.get(0);
                    case Constants.RESET_REPEAT_AND_GO_AFTER_END:
                        SmartList<Pointer> newList = new SmartList<Pointer>();
                        newList.add(new Pointer(Constants.KEYWORD_TYPE,
                                Constants.REPEAT));
                        newList.add(pointerLists.get(programPointer).get(0));
                        pointerLists.set(programPointer, newList);
                        programPointer = goAfterEnd(programPointer,
                                pointerLists);
                        break;
                    case Constants.PUSH_PTR_AND_DECREMENT:
                        loopProgramPointerStack.add(programPointer);
                        SmartList<Pointer> currentList =
                                pointerLists.get(programPointer);
                        if (currentList.get(0).equals(
                                new Pointer(Constants.KEYWORD_TYPE,
                                        Constants.REPEAT))) {
                            currentList.add(0, currentList.get(1));
                        }
                        SmartList<Pointer> nList = currentList.subList(2);
                        currentList.assignSublist(2, currentList.size(),
                                Heap.allocateInt(
                                PointerTools.dereferenceInt(
                                evalExpression(nList, 0)) - 1));
                        programPointer++;
                        break;
                    case Constants.DEFINE_SUB: {
                        Subprogram sub = PointerTools.dereferenceSub(
                                msg.contents.get(0));
                        programPointer++;
                        int neededEnds = 1;
                        //System.out.print(pointerLists);
                        while (programPointer < pointerLists.size()) {
                            SmartList<Pointer> line =
                                    pointerLists.get(programPointer);
                            if (line.size() > 0) {
                                Pointer p = line.get(0);
                                Pointer end = new Pointer(
                                        Constants.KEYWORD_TYPE, Constants.END);
                                if (p != null && p.equals(end)) {
                                    neededEnds--;
                                    if (neededEnds <= 0) {
                                        programPointer++;
                                        break;
                                    }
                                } else if (ListTools.containsPointer(
                                           Constants.BLOCK_STARTERS, p)) {
                                    neededEnds++;
                                }
                            }
                            sub.addToBody(line);
                            programPointer++;
                        }
                        SmartList<Pointer> retn = new SmartList<Pointer>();
                        retn.add(new Pointer(Constants.KEYWORD_TYPE,
                                Constants.RETURN));
                        // All subs must execute a return statement:
                        sub.addToBody(retn);
                        env.put(sub.getName(), Heap.allocateSub(sub));
                        break;
                    } case Constants.DEFINE_BLOCK: {
                        Block block = PointerTools.dereferenceBlock(
                                msg.contents.get(0));
                        programPointer++;
                        int neededEnds = 1;
                        while (programPointer < pointerLists.size()) {
                            SmartList<Pointer> line =
                                    pointerLists.get(programPointer);
                            if (line.size() > 0) {
                                Pointer p = line.get(0);
                                Pointer end = new Pointer(
                                        Constants.KEYWORD_TYPE, Constants.END);
                                if (p.equals(end)) {
                                    neededEnds--;
                                    if (neededEnds <= 0) {
                                        programPointer++;
                                        break;
                                    }
                                } else if (ListTools.containsPointer(
                                        Constants.BLOCK_STARTERS, p)) {
                                    neededEnds++;
                                }
                            }
                            block.addToBody(line);
                            programPointer++;
                            SmartList<Pointer> leave = new SmartList<Pointer>();
                            leave.add(new Pointer(Constants.KEYWORD_TYPE,
                                    Constants.LEAVE));
                            // All subs must execute a return statement:
                            block.addToBody(leave);
                            env.put(block.getName(), Heap.allocateBlock(block));
                        }
                        break;
                    } case Constants.MSG_LEAVE: {
                        return new Pointer(Constants.NULL_TYPE, 0);
                    } case Constants.MSG_FOR: {
                        // Convert for loop into a while loop:
                        SmartList<Pointer> _while =
                                pointerLists.get(programPointer + 1);
                        _while.add(0, new Pointer(Constants.KEYWORD_TYPE,
                                Constants.WHILE));
                        SmartList<Pointer> initialization =
                                pointerLists.get(programPointer);
                        initialization.remove(0);
                        pointerLists.set(programPointer, initialization);
                        pointerLists.set(programPointer + 1, _while);
                        int p = goAfterEnd(programPointer + 1,
                                pointerLists);
                        p -= 2;
                        SmartList<SmartList<Pointer>> pLists =
                                new SmartList<SmartList<Pointer>>();
                        SmartList<Pointer> change =
                                pointerLists.remove(programPointer + 2);
                        pLists.add(change);
                        SmartList<Pointer> end = new SmartList<Pointer>();
                        end.add(new Pointer(Constants.KEYWORD_TYPE,
                                Constants.END));
                        pLists.add(end);
                        pointerLists.assignSublist(p, p + 1, pLists);
                        break;
                    } case Constants.MSG_FOREACH: {
                        Pointer name = pointerLists.get(programPointer).get(1);
                        Pointer iterable =
                                pointerLists.get(programPointer).get(3);
                        Pointer sysVar = Heap.getNextSystemVarName();
                        SmartList<Pointer> assign0 = new SmartList<Pointer>();
                        assign0.add(sysVar);
                        assign0.add(new Pointer(Constants.KEYWORD_TYPE,
                                Constants.ASSIGN));
                        assign0.add(new Pointer(Constants.INT_TYPE, 0));
                        SmartList<Pointer> _while = new SmartList<Pointer>();
                        _while.add(new Pointer(Constants.KEYWORD_TYPE,
                                Constants.WHILE));
                        _while.add(sysVar);
                        _while.add(new Pointer(Constants.KEYWORD_TYPE,
                                Constants.LESS_THAN));
                        _while.add(iterable);
                        _while.add(new Pointer(Constants.KEYWORD_TYPE,
                                Constants.DOT));
                        _while.add(Heap.allocateName("size"));
                        _while.add(new Pointer(Constants.KEYWORD_TYPE,
                                Constants.OPEN_PAREN));
                        _while.add(new Pointer(Constants.KEYWORD_TYPE,
                                Constants.CLOSE_PAREN));
                        SmartList<Pointer> assignI = new SmartList<Pointer>();
                        assignI.add(name);
                        assignI.add(new Pointer(Constants.KEYWORD_TYPE,
                                Constants.ASSIGN));
                        assignI.add(iterable);
                        assignI.add(new Pointer(Constants.KEYWORD_TYPE,
                                Constants.OPEN_BRACKET));
                        assignI.add(sysVar);
                        assignI.add(new Pointer(Constants.KEYWORD_TYPE,
                                Constants.CLOSE_BRACKET));
                        SmartList<Pointer> increment = new SmartList<Pointer>();
                        increment.add(sysVar);
                        increment.add(new Pointer(Constants.KEYWORD_TYPE,
                                Constants.INCREMENT));
                        SmartList<SmartList<Pointer>> pLists =
                                new SmartList<SmartList<Pointer>>();
                        pLists.add(assign0);
                        pLists.add(_while);
                        pLists.add(assignI);
                        pointerLists.assignSublist(programPointer,
                                                   programPointer + 1, pLists);
                        int p = goAfterEnd(programPointer + 2,
                                pointerLists);
                        p -= 2;
                        pLists = new SmartList<SmartList<Pointer>>();
                        pLists.add(increment);
                        SmartList<Pointer> end = new SmartList<Pointer>();
                        end.add(new Pointer(Constants.KEYWORD_TYPE,
                                Constants.END));
                        pLists.add(end);
                        pointerLists.assignSublist(p, p + 1, pLists);
                        break;
                    } case Constants.MSG_SWITCH: {
                        Pointer val = pointerLists.get(programPointer).get(1);
                        val = evaluateName(val, env);
                        programPointer = goAfterCase(programPointer,
                                pointerLists, PointerTools.dereferenceInt(val));
                        break;
                    } default: {
                        throw new MintException("Unknown message: " +
                                msg.message);
                    }
                }
            }
        }
        return new Pointer(Constants.NULL_TYPE, 0);
    }
    //</editor-fold>
}
