package tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A syntax highlighter for Mint that outputs HTML.
 * @author Jiangcheng Oliver Chu
 */
public class SyntaxHighlight {
    private static PrefixTrie masterTrie;
    private static RegexTrie numericTrie;
    private static RegexTrie stringTrie;
    private static RegexTrie tagTrie;
    private static RegexTrie commentTrie;
    private static boolean initialized = false;
    public static final String[] MINT_KEYWORDS = {
        "if", "else", "when", "case", "while", "switch", "default", "for",
        "each", "of", "continue", "break", "sub", "import", "and", "or", "xor",
        "not", "true", "false", "null", "then", "flip", "class", "operator",
        "end", "print", "input", "show", "repeat", "return", "try", "catch",
        "into", "in", "this", "inherit", "andb", "orb", "xorb", "notb"
    };
    
    public SyntaxHighlight() {
        if (!initialized) {
            masterTrie = new PrefixTrie();
            masterTrie.addAll(MINT_KEYWORDS);
            /* numericTrie detects floating point and integer literals. */
            numericTrie = new RegexTrie(
                "-?(0x|0o|0b)?(\\d\\.)?\\d+([eE]-?\\d+)?(\\[\\d+\\])?");
            /* stringTrie detects double or single quoted strings. */
            stringTrie = new RegexTrie("\\[\"'](.|\\\\\\\")*?\\[\"']");
            /* stringTrie detects valid tag syntax. */
            tagTrie = new RegexTrie(
                "#[_A-Za-z][_A-Za-z0-9\\(\\)\\.,]*?[_A-Za-z0-9\\)]");
            /* commentTrie detects single and multiline comments. */
            commentTrie = new RegexTrie(
                "(//.*\n)|(/\\*(.|\n)*\\*/)");
            masterTrie.setChild(tagTrie, '#');
            masterTrie.setChild(stringTrie, '"');
            masterTrie.setChild(stringTrie, '\'');
            masterTrie.setChild(commentTrie, '/');
            masterTrie.setChild(numericTrie, '-');
            for (char c = '0'; c <= '9'; c++) {
                masterTrie.setChild(numericTrie, c);
            }
            initialized = true;
        }
    }

    public String highlight(String code) {
        String result = "";
        for (int i = 0; i < code.length(); i++) {
            String grabbed = masterTrie.grab(code, i);
            if (grabbed != null && grabbed.length() > 0) {
                result += wrapAny(grabbed);
                i += grabbed.length() - 1;
            } else {
                result += code.charAt(i);
            }
        }
        return result;
    }
    
    private String wrapAny(String innerHTML) {
        if (!innerHTML.isEmpty()) {
            switch (innerHTML.charAt(0)) {
                case '#':
                    return wrap(innerHTML, "tag");
                case '"':
                case '\'':
                    return wrap(innerHTML, "string2");
                case '/':
                    return wrap(innerHTML, "comment2");
                case '0': case '1': case '2': case '3':
                case '4': case '5': case '6': case '7':
                case '8': case '9': case '-':
                    return wrap(innerHTML, "number2");
                default:
                    return wrap(innerHTML, "keyword");
            }
        } else {
            return "";
        }
    }
    
    private String wrap(String innerHTML, String className) {
        return "<span class=\"" + className + "\">" + innerHTML + "</span>";
    }
    
    public boolean hasWord(String word) {
        return masterTrie.contains(word);
    }
    
    private interface Trie {
        boolean contains(String s, int runningIndex);
        boolean contains(String s);
        void add(String s, int runningIndex);
        String getMatch();
        String grab(String s, int runningIndex);
    }
    
    /**
     * A trie for string prefix matching, emphasizing speed over memory.
     * @author Jiangcheng Oliver Chu
     */
    private class PrefixTrie implements Trie {
        private static final int ASCII_RANGE = 94;
        private final Trie[] children = new Trie[ASCII_RANGE];
        private String lastMatch = null;
        private boolean isLeaf = true;
        
        PrefixTrie() {}

        @Override
        public boolean contains(String s, int runningIndex) {
            if (runningIndex >= s.length()) {
                return isLeaf;
            }
            int first = s.charAt(runningIndex) - ' ';
            Trie firstChild = children[first];
            if (firstChild == null) {
                return isLeaf;
            } else {
                if (firstChild.contains(s, runningIndex + 1)) {
                    setMatch(s.substring(runningIndex));
                    return true;
                } else {
                    return false;
                }
            }
        }

        @Override
        public String grab(String s, int runningIndex) {
            if (runningIndex >= s.length()) {
                if (isLeaf) {
                    return "";
                } else {
                    return null;
                }
            }
            int first = s.charAt(runningIndex) - ' ';
            Trie firstChild = children[first];
            if (firstChild == null) {
                if (isLeaf) {
                    return "";
                } else {
                    return null;
                }
            } else {
                String rest = firstChild.grab(s, runningIndex + 1);
                if (rest == null) {
                    return null;
                } else {
                    return s.charAt(runningIndex) + rest;
                }
            }
        }
        
        @Override
        public boolean contains(String s) {
            return contains(s, 0);
        }
        
        @Override
        public void add(String s, int runningIndex) {
            if (runningIndex < s.length()) {
                int first = s.charAt(runningIndex) - ' ';
                if (children[first] == null) {
                    children[first] = new PrefixTrie();
                }
                children[first].add(s, runningIndex + 1);
                isLeaf = false;
            }
        }
        
        public void add(String s) {
            add(s, 0);
        }
        
        public void addAll(String[] strs) {
            for (String s : strs) {
                add(s);
            }
        }

        public void setChild(Trie t, int childIndex) {
            children[childIndex - ' '] = t;
        }
        
        @Override
        public String getMatch() {
            return lastMatch;
        }
        
        public void setMatch(String matchedString) {
            lastMatch = matchedString;
        }
    }

    /**
     * A regex wrapper that acts like a Trie.
     * @author Jiangcheng Oliver Chu
     */
    private class RegexTrie extends PrefixTrie {
        private final Pattern triePattern;
        
        RegexTrie(String regex) {
            triePattern = Pattern.compile(regex);
        }
        
        @Override
        public boolean contains(String s, int runningIndex) {
            Matcher trieMatcher = triePattern.matcher(s);
            trieMatcher.region(runningIndex, s.length());
            if (trieMatcher.lookingAt()) {
                setMatch(s);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean contains(String s) {
            return triePattern.matcher(s).lookingAt();
        }
    }
}
