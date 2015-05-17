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
            }
        };
        setSubtests(tests);
    }
    
    @Override
    protected boolean mainTest() {
        return true;
    }
}
