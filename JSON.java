import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Utilities for our simple implementation of JSON.
 */
public class JSON {
    // +---------------+-----------------------------------------------
    // | Static fields |
    // +---------------+
    static int pos;  // The current position in the input.

    // +----------------+----------------------------------------------
    // | Static methods |
    // +----------------+

    /**
     * Parse a string into JSON.
     */
    public static JSONValue parse(String source) throws ParseException, IOException {
        return parse(new StringReader(source));
    }

    /**
     * Parse a file into JSON.
     */
    public static JSONValue parseFile(String filename) throws ParseException, IOException {
        try (FileReader reader = new FileReader(filename)) {
            return parse(reader);
        }
    }

    /**
     * Parse JSON from a reader.
     */
    public static JSONValue parse(Reader source) throws ParseException, IOException {
        pos = 0;
        JSONValue result = parseKernel(source);
        if (-1 != skipWhitespace(source)) {
            throw new ParseException("Characters remain at end", pos);
        }
        return result;
    }

    // +---------------+-----------------------------------------------
    // | Local helpers |
    // +---------------+

    /**
     * Parse JSON from a reader, keeping track of the current position.
     */
    static JSONValue parseKernel(Reader source) throws ParseException, IOException {

        int ch = skipWhitespace(source);
        if (ch == -1) {
            throw new ParseException("Unexpected end of file", pos);
        }

        switch (ch) {
            case '"': return parseString(source);
            case '[': return parseArray(source);
            case '{': return parseObject(source);
            case 't': case 'f': return parseBoolean(source);
            case 'n': return parseNull(source);
            default:
            System.out.println(ch);
                if (Character.isDigit(ch) || ch == '-') {
                    System.out.println(ch);
                    return parseNumber(source, ch);
                }
                throw new ParseException("Unexpected character", pos);
        }
    }

    static JSONValue parseString(Reader source) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        boolean escaped = false;
        while ((c = source.read()) != -1) {
            char character = (char) c;
            if (escaped) {
                if (character == 'n') sb.append('\n');
                else if (character == 't') sb.append('\t');
                else if (character == '\\') sb.append('\\');
                else if (character == '"') sb.append('"');
                escaped = false;
            } else if (character == '\\') {
                escaped = true;
            } else if (character == '"') {
                break;
            } else {
                sb.append(character);
            }
            pos++;
        }
        return new JSONString(sb.toString());
    }

    static JSONValue parseArray(Reader source) throws IOException, ParseException {
        JSONArray array = new JSONArray();
        int c;
        // boolean first = true;
        array.add(parseKernel(source));
        while ((c = skipWhitespace(source)) != ']') {
            if (c == -1) throw new ParseException("Unterminated array", pos);
            // if (!first && c == ',') c = skipWhitespace(source);
            // if (first) first = false;
            pos--;
            array.add(parseKernel(source));
        }
        return array;
    }

    static JSONValue parseObject(Reader source) throws IOException, ParseException {
        JSONHash object = new JSONHash();
        int c;
        boolean first = true;
        while ((c = skipWhitespace(source)) != '}') {
            if (c == -1) throw new ParseException("Unterminated object", pos);
            if (!first && c == ',') c = skipWhitespace(source);
            if (first) first = false;

            if (c != '"') throw new ParseException("Expected '\"' at the beginning of key in object", pos);
            JSONString key = (JSONString) parseString(source);
            if (skipWhitespace(source) != ':') throw new ParseException("Expected ':' after key in object", pos);
            JSONValue value = parseKernel(source);
            object.set(key, value);
        }
        return object;
    }

    static JSONValue parseBoolean(Reader source) throws IOException {
        source.mark(4);  // To handle true/false
        char[] tf = new char[5];
        int n = source.read(tf, 0, 4);

        if (n == 4 || n == 3) {
            String str = new String(tf).trim();
            if ("rue".equals(str)) return JSONConstant.TRUE;
            if ("alse".equals(str)) return JSONConstant.FALSE;
        }
        throw new IOException("Invalid boolean value");
    }

    static JSONValue parseNull(Reader source) throws IOException {
        source.mark(3);
        char[] nul = new char[4];
        int n = source.read(nul, 0, 4);
        if (n == 4 && "ull".equals(new String(nul).trim())) {
            return JSONConstant.NULL;
        }
        throw new IOException("Invalid null value");
    }

    static JSONValue parseNumber(Reader source, int firstChar) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append((char) firstChar);
        while (true) {
            source.mark(1);
            int c = source.read();
            if (Character.isDigit(c) || c == '.' || c == 'e' || c == 'E' || c == '-') {
                sb.append((char) c);
            } else {
                source.reset();
                break;
            }
        }
        String num = sb.toString();
        if (num.contains(".") || num.contains("e") || num.contains("E")) {
            return new JSONReal(new BigDecimal(num));
        } else {
            return new JSONInteger(new BigInteger(num));
        }
    }

    /**
     * Skip whitespace characters in the input source.
     */
    static int skipWhitespace(Reader source) throws IOException {
        int ch;
        do {
            ch = source.read();
            pos++;
        } while (Character.isWhitespace(ch));
        return ch;
    }
}
