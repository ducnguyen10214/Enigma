package enigma;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Duc Nguyen
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        this._notches = notches;
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        for (int i = 0; i < _notches.length(); i += 1) {
            char currChar = _notches.charAt(i);
            if (setting() == permutation().alphabet().toInt(currChar)) {
                return true;
            }
        }
        return false;
    }

    @Override
    void advance() {
        set((setting() + 1) % size());
    }

    /** Only moving rotors can have notch(es). */
    private String _notches;

}
