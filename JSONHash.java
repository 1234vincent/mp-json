import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * JSON hashes/objects.
 */
public class JSONHash implements JSONValue, Iterable<KVPair<JSONString, JSONValue>> {

    // +--------+------------------------------------------------------
    // | Fields |
    // +--------+

    private ArrayList<KVPair<JSONString, JSONValue>>[] table;
    private int size;  // Number of key-value pairs in the hash

    // +--------------+------------------------------------------------
    // | Constructors |
    // +--------------+

    @SuppressWarnings("unchecked")
    public JSONHash() {
        table = (ArrayList<KVPair<JSONString, JSONValue>>[]) new ArrayList[10];  // initial capacity
        for (int i = 0; i < table.length; i++) {
            table[i] = new ArrayList<>();
        }
        size = 0;
    }

    // +-------------------------+-------------------------------------
    // | Standard object methods |
    // +-------------------------+

    /**
     * Convert to a string (e.g., for printing).
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        Iterator<KVPair<JSONString, JSONValue>> it = this.iterator();
        while (it.hasNext()) {
            KVPair<JSONString, JSONValue> pair = it.next();
            sb.append(pair.toString());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Compare to another object.
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof JSONHash)) return false;
        JSONHash that = (JSONHash) other;
        if (this.size() != that.size()) return false;
        Iterator<KVPair<JSONString, JSONValue>> it = this.iterator();
        while (it.hasNext()) {
            KVPair<JSONString, JSONValue> pair = it.next();
            JSONValue thatValue = that.get(pair.key());
            if (!pair.value().equals(thatValue)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compute the hash code.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        for (KVPair<JSONString, JSONValue> pair : this) {
            hash += pair.hashCode();
        }
        return hash;
    }

    // +--------------------+------------------------------------------
    // | Additional methods |
    // +--------------------+

    /**
     * Write the value as JSON.
     */
    @Override
    public void writeJSON(PrintWriter pen) {
        pen.print(this.toString());
    }

    /**
     * Get the underlying value.
     */
    @Override
    public Iterator<KVPair<JSONString, JSONValue>> getValue() {
        return this.iterator();
    }

    // +-------------------+-------------------------------------------
    // | Hashtable methods |
    // +-------------------+

    /**
     * Get the value associated with a key.
     */
    public JSONValue get(JSONString key) {
        int index = hash(key);
        for (KVPair<JSONString, JSONValue> pair : table[index]) {
            if (pair.key().equals(key)) {
                return pair.value();
            }
        }
        return null;
    }

    /**
     * Set the value associated with a key, replacing the existing KVPair if the key exists.
     */
    public void set(JSONString key, JSONValue value) {
        int index = hash(key);
        for (int i = 0; i < table[index].size(); i++) {
            if (table[index].get(i).key().equals(key)) {
                table[index].set(i, new KVPair<>(key, value));  // Replace with new KVPair
                return;
            }
        }
        table[index].add(new KVPair<>(key, value));
        size++;
    }

    /**
     * Get all of the key/value pairs.
     */
    @Override
    public Iterator<KVPair<JSONString, JSONValue>> iterator() {
        ArrayList<KVPair<JSONString, JSONValue>> result = new ArrayList<>();
        for (ArrayList<KVPair<JSONString, JSONValue>> bucket : table) {
            result.addAll(bucket);
        }
        return result.iterator();
    }

    /**
     * Find out how many key/value pairs are in the hash table.
     */
    public int size() {
        return size;
    }

    // +------------------+--------------------------------------------
    // | Helper methods |
    // +------------------+

    /**
     * Compute the index in the table for a given key.
     */
    private int hash(JSONString key) {
        return (key.hashCode() & 0x7fffffff) % table.length;
    }

} // class JSONHash
