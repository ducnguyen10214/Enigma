package enigma;
import static enigma.EnigmaException.error;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Duc Nguyen
 */
class Alphabet {

    /** the Alphabet from which we use for further purposes. */
    private String _alphabet;

    /**
     * A new alphabet containing CHARS. The K-th character has index
     * K (numbering from 0). No character may be duplicated.
     */
    Alphabet(String chars) {
        if (chars.length() == 0 || chars.equals("")
                || chars == null || chars.matches("\\*\\(\\)\\~\\`\\[\\]")) {
            throw error("Alphabet error! Unqualified chars");
        }
        for (int i = 0; i < chars.length(); i += 1) {
            for (int j = i + 1; j < chars.length(); j += 1) {
                if (chars.charAt(i) == chars.charAt(j)) {
                    throw error("Duplicate characters in alphabet");
                }
            }
        }
        _alphabet = chars;
    }

    /**
     * A default alphabet of all upper-case characters.
     */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /**
     * Returns the size of the alphabet.
     */
    int size() {
        return _alphabet.length();
    }

    /**
     * Returns true if CH is in this alphabet.
     */
    boolean contains(char ch) {
        for (int i = 0; i < _alphabet.length(); i += 1) {
            if (_alphabet.charAt(i) == ch) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns character number INDEX in the alphabet, where
     * 0 <= INDEX < size().
     */
    char toChar(int index) {
        if (index >= 0 && index < _alphabet.length()) {
            return _alphabet.charAt(index);
        }
        throw error("Out of bound!");
    }

    /**
     * Returns the index of character CH which must be in
     * the alphabet. This is the inverse of toChar().
     */
    int toInt(char ch) {
        for (int i = 0; i < _alphabet.length(); i += 1) {
            if (_alphabet.charAt(i) == ch) {
                return i;
            }
        }
        throw error("Character not found to be converted!");
    }

}
