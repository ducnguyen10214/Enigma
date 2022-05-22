package enigma;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Duc Nguyen
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _currSetting = 0;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _currSetting;
    }

    /** Return my current Ringstellung setting. */
    int ringSetting() {
        return _ringSetting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _currSetting = posn;
    }

    /** Set ringSetting() to POSN. */
    void setRing(int posn) {
        _ringSetting = posn;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        _currSetting = _permutation.alphabet().toInt(cposn);
    }

    /** Set ringSetting() to character CPOSN. */
    void setRing(char cposn) {
        _ringSetting = _permutation.alphabet().toInt(cposn);
    }

    /** Return the conversion of P (an integer in the range 0...size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int contactEntered = (p + _currSetting - _ringSetting) % size();
        int contactExited = _permutation.permute(contactEntered)
                - _currSetting + _ringSetting;
        if (contactExited < 0) {
            return (size() - Math.abs(contactExited)) % size();
        }
        return contactExited % size();
    }

    /** Return the conversion of E (an integer in the range 0...size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int contactEntered = (e + _currSetting - _ringSetting) % size();
        int contactExited = _permutation.invert(contactEntered)
                - _currSetting + _ringSetting;
        if (contactExited < 0) {
            return (size() - Math.abs(contactExited)) % size();
        }
        return contactExited % size();
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** My current setting. */
    private int _currSetting;

    /** My current Ringstellung setting. */
    private int _ringSetting;

}
