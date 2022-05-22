package enigma;


/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Duc Nguyen
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        String temp = cycles.trim();
        temp = temp.replace("(", " ");
        temp = temp.replace(")", " ");
        temp = temp.trim();
        _cycles = temp.split("\\s+");
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        String[] tmp = new String[_cycles.length + 1];
        System.arraycopy(_cycles, 0, tmp, 0, _cycles.length);
        tmp[_cycles.length] = cycle;
        _cycles = tmp;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int applyInput = wrap(p);
        char applyChar = _alphabet.toChar(applyInput);
        int translatedIndex = -1;
        char translatedChar = ' ';
        for (String sublist : _cycles) {
            for (int i = 0; i < sublist.length(); i += 1) {
                if (sublist.charAt(i) == applyChar) {
                    translatedIndex = (i + 1) % sublist.length();
                    translatedChar = sublist.charAt(translatedIndex);
                    return _alphabet.toInt(translatedChar);
                }
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation
     *  to C modulo the alphabet size. */
    int invert(int c) {
        int contactEntered = wrap(c);
        char charEntered = _alphabet.toChar(contactEntered);
        int translatedIndex = -1;
        char translatedChar = ' ';
        for (String sublist : _cycles) {
            for (int i = sublist.length() - 1; i >= 0; i -= 1) {
                if (sublist.charAt(i) == charEntered) {
                    if (i == 0) {
                        translatedIndex = sublist.length() - 1;
                    } else {
                        translatedIndex = i - 1;
                    }
                    translatedChar = sublist.charAt(translatedIndex);
                    return _alphabet.toInt(translatedChar);
                }
            }
        }
        return c;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int input = _alphabet.toInt(p);
        int output = permute(input);
        return _alphabet.toChar(output);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int input = _alphabet.toInt(c);
        int output = invert(input);
        return _alphabet.toChar(output);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (String sublist : _cycles) {
            if (sublist.length() == 0 || sublist.length() == 1) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Cycles of this permutation as an array of sub-arrays. */
    private String[] _cycles;

}
