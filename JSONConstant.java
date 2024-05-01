import java.io.PrintWriter;

/**
 * Represents immutable JSON constants.
 */
public class JSONConstant implements JSONValue {

    // +---------------+-----------------------------------------------
    // | Static fields |
    // +---------------+

    public static final JSONConstant TRUE = new JSONConstant(true);
    public static final JSONConstant FALSE = new JSONConstant(false);
    public static final JSONConstant NULL = new JSONConstant(null);

    // +--------+------------------------------------------------------
    // | Fields |
    // +--------+

    private final Object value;

    // +--------------+------------------------------------------------
    // | Constructors |
    // +--------------+

    /**
     * Constructs a new constant.
     * @param value The value to hold, which should be either a Boolean or null.
     */
    private JSONConstant(Object value) {
        this.value = value;
    }

    // +-------------------------+-------------------------------------
    // | Standard object methods |
    // +-------------------------+

    @Override
    public String toString() {
        if (value == null) {
            return "null";
        } else {
            return value.toString();
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof JSONConstant)) return false;
        JSONConstant that = (JSONConstant) other;
        return this.value == that.value; // Works because Boolean instances are cached by the JVM
    }

    @Override
    public int hashCode() {
        return (value != null) ? value.hashCode() : 0;
    }

    // +--------------------+------------------------------------------
    // | Additional methods |
    // +--------------------+

    @Override
    public void writeJSON(PrintWriter pen) {
        pen.print(this.toString());
    }

    @Override
    public Object getValue() {
        return this.value;
    }

} // class JSONConstant
