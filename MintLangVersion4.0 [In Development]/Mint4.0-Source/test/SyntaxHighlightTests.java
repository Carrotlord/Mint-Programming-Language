package test;

import tools.SyntaxHighlight;

/**
 * Tests for syntax highlighter.
 * @author Jiangcheng Oliver Chu
 */
public class SyntaxHighlightTests extends TestGroup {
    public SyntaxHighlightTests() {
        super("syntax_highlight_tests");
        TestGroup[] tests = {
            new TestGroup("has_word") {
                @Override
                protected boolean mainTest() {
                    boolean hasWord = (new SyntaxHighlight()).hasWord("break");
                    return assertEquals(hasWord, true);
                }
            },
            new TestGroup("keywords") {
                @Override
                protected boolean mainTest() {
                    String highlighted = (new SyntaxHighlight()).highlight(
                                          "if else then continue into");
                    return assertEquals(highlighted,
                        "<span class=\"keyword\">if</span> " +
                        "<span class=\"keyword\">else</span> " +
                        "<span class=\"keyword\">then</span> " +
                        "<span class=\"keyword\">continue</span> " +
                        "<span class=\"keyword\">into</span>"
                    );
                }
            },
            new TestGroup("integers") {
                @Override
                protected boolean mainTest() {
                    String highlighted = (new SyntaxHighlight()).highlight(
                                          "3 15623 0 81 -2 -20");
                    return assertEquals(highlighted,
                        "<span class=\"number2\">3</span> " +
                        "<span class=\"number2\">15623</span> " +
                        "<span class=\"number2\">0</span> " +
                        "<span class=\"number2\">81</span> " +
                        "<span class=\"number2\">-2</span> " +
                        "<span class=\"number2\">-20</span>"
                    );
                }
            },
            new TestGroup("reals_rationals") {
                @Override
                protected boolean mainTest() {
                    String highlighted = (new SyntaxHighlight()).highlight(
                                          "3.0 1.56e23 -0.0 -8:1 -2.0e-9:-20");
                    return assertEquals(highlighted,
                        "<span class=\"number2\">3.0</span> " +
                        "<span class=\"number2\">1.56e23</span> " +
                        "<span class=\"number2\">-0.0</span> " +
                        "<span class=\"number2\">-8</span>:" +
                        "<span class=\"number2\">1</span> " +
                        "<span class=\"number2\">-2.0e-9</span>:" +
                        "<span class=\"number2\">-20</span>"  
                    );
                }
            }
        };
        setSubtests(tests);
    }
    
    @Override
    protected boolean mainTest() {
        return true;
    }
}
