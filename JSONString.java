import java.io.PrintWriter;

/**
 * JSON strings.
 */
public class JSONString implements JSONValue {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The underlying string.
   */
  String value;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Build a new JSON string for a particular string.
   */
  public JSONString(String value) {
    this.value = value;
  } // JSONString(String)

  // +-------------------------+-------------------------------------
  // | Standard object methods |
  // +-------------------------+

  /**
   * Convert to a string (e.g., for printing).
   */
  public String toString() {
    return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
}

  /**
   * Compare to another object.
   */
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    } else if (other instanceof JSONString) {
      JSONString that = (JSONString) other;
      return this.value.equals(that.value);
    } else {
      return false;
    } // if (
  } // equals(Object)

  /**
   * Compute the hash code.
   */
  public int hashCode() {
    return this.value.hashCode(); // STUB
  } // hashCode()

  // +--------------------+------------------------------------------
  // | Additional methods |
  // +--------------------+

  /**
   * Write the value as JSON.
   */
  public void writeJSON(PrintWriter pen) {
    pen.print("\"");
    pen.print(value.replace("\\", "\\\\").replace("\"", "\\\""));
    pen.print("\"");
  } // writeJSON(PrintWriter)

  /**
   * Get the underlying value.
   */
  public String getValue() {
    return this.value;
  } // getValue()

} // class JSONString
