package enigma;

import java.util.Collection;
import static enigma.EnigmaException.error;

/** Class that represents a complete enigma machine.
 *  @author Duc Nguyen
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        if (numRotors <= pawls || numRotors <= 0 || pawls <= 0) {
            throw error("Invalid construction of Machine");
        }
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors.toArray();
        _mainRotors = new Rotor[numRotors];
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        if (_mainRotors.length != rotors.length) {
            throw error("insertRotors error! Length not equal");
        }
        for (int i = 0; i < rotors.length; i += 1) {
            for (int j = i + 1; j < rotors.length; j += 1) {
                if (rotors[i].equals(rotors[j])) {
                    throw error("insertRotors error! Duplicate rotor names");
                }
            }
        }
        for (int i = 0; i < _mainRotors.length; i += 1) {
            for (int j = 0; j < _allRotors.length; j += 1) {
                String currRotorName = ((Rotor) _allRotors[j]).name();
                if (rotors[i].equals(currRotorName)) {
                    _mainRotors[i] = ((Rotor) _allRotors[j]);
                }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != numRotors() - 1) {
            throw error("setRotors error! Different length");
        }
        for (int i = 1; i < _numRotors; i += 1) {
            char currChar = setting.charAt(i - 1);
            if (_alphabet.contains(currChar)) {
                _mainRotors[i].set(currChar);
            } else {
                throw error("setRotors error! Out of Alphabet");
            }
        }
    }

    /** Set my ringstellungs according to SETTING, which must be a string of
     *  numRotors() - 1 characters in my alphabet. The first letter refers to
     *  the leftmost ring setting (not counting the reflector).
     */
    void setRings(String setting) {
        if (setting.length() == _numRotors - 1) {
            for (int i = 1; i < _numRotors; i += 1) {
                _mainRotors[i].setRing(setting.charAt(i - 1));
            }
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        boolean lastRotor = true;
        for (int i = 0; i < _numRotors - 1; i += 1) {
            if (_mainRotors[i].rotates() && _mainRotors[i + 1].atNotch()) {
                _mainRotors[i].advance();
                _mainRotors[i + 1].advance();
                if (i == _numRotors - 2) {
                    lastRotor = false;
                }
                i += 1;
            }
        }
        if (lastRotor) {
            _mainRotors[_numRotors - 1].advance();
        }
        int output = _plugboard.permute(c);
        for (int i = _numRotors - 1; i >= 0; i -= 1) {
            output = _mainRotors[i].convertForward(output);
        }
        for (int i = 1; i < _numRotors; i += 1) {
            output = _mainRotors[i].convertBackward(output);
        }
        output = _plugboard.invert(output);
        return output;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String output = "";
        for (int i = 0; i < msg.length(); i += 1) {
            int currCharInt = _alphabet.toInt(msg.charAt(i));
            int convertedCharInt = convert(currCharInt);
            char convertedChar = _alphabet.toChar(convertedCharInt);
            output += convertedChar;
        }
        return output;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Total number of rotors. */
    private final int _numRotors;

    /** Total number of pawls. */
    private final int _pawls;

    /** All the Rotors available for use. */
    private Object[] _allRotors;

    /** The specified rotors formatted based on the input file. */
    private Rotor[] _mainRotors;

    /**  Plugboard setting. */
    private Permutation _plugboard;

}
