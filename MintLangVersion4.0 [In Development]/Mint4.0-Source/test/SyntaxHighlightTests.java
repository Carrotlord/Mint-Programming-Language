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
            },
            new TestGroup("strings") {
                @Override
                protected boolean mainTest() {
                    String highlighted = (new SyntaxHighlight()).highlight(
                         "\"This is a\" + 'string' + \".\\\"...\\'\" + \"\""
                    );
                    return assertEquals(highlighted,
                        "<span class=\"string2\">\"This is a\"</span> + " +
                        "<span class=\"string2\">'string'</span> + " +
                        "<span class=\"string2\">\".\\\"...\\'\"</span> + " +
                        "<span class=\"string2\">\"\"</span>"
                    );
                }
            },
            new TestGroup("tags") {
                @Override
                protected boolean mainTest() {
                    String highlighted = (new SyntaxHighlight()).highlight(
                        "#Int #category,#x.y #xs.ys #Array(Int) " +
                        "#Array(Array(Int))"
                    );
                    return assertEquals(highlighted,
                        "<span class=\"tag\">#Int</span> " +
                        "<span class=\"tag\">#category</span>," +
                        "<span class=\"tag\">#x.y</span> " +
                        "<span class=\"tag\">#xs.ys</span> " +
                        "<span class=\"tag\">#Array(Int)</span> " +
                        "<span class=\"tag\">#Array(Array(Int))</span>"
                    );
                }
            },
            new TestGroup("comments") {
                @Override
                protected boolean mainTest() {
                    String highlighted = (new SyntaxHighlight()).highlight(
                        "// stuff \n/* stuff \n\n */"
                    );
                    return assertEquals(highlighted,
                        "<span class=\"comment2\">// stuff </span>\n" +
                        "<span class=\"comment2\">/* stuff \n\n */</span>"
                    );
                }
            },
            new TestGroup("actual_code") {
                @Override
                protected boolean mainTest() {
                    String highlighted = (new SyntaxHighlight()).highlight(
                        "y = null\n" +
                        "if x > 0.0\n" +
                        "    y = \"some string.\"\n" +
                        "else\n" +
                        "    y = [18, 19, #h2o]\n" +
                        "end // end of file"
                    );
                    return assertEquals(highlighted,
                        "y = <span class=\"keyword\">null</span>\n" +
                        "<span class=\"keyword\">if</span> x > " +
                        "<span class=\"number2\">0.0</span>\n" +
                        "    y = <span class=\"string2\">" +
                        "\"some string.\"</span>\n" +
                        "<span class=\"keyword\">else</span>\n" +
                        "    y = [<span class=\"number2\">18</span>, " +
                        "<span class=\"number2\">19</span>, " +
                        "<span class=\"tag\">#h2o</span>]\n" +
                        "<span class=\"keyword\">end</span> " +
                        "<span class=\"comment2\">// end of file</span>"
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
