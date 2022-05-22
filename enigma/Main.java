package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.ArrayList;

import static enigma.EnigmaException.error;

/** Enigma simulator.
 *  @author Duc Nguyen
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine m = readConfig();
        String[] rotors = new String[m.numRotors()];
        if (!_input.hasNext("\\*(\\s*[A-Z]+[a-z]*)*")) {
            throw error("process error! Invalid start!");
        }
        while (_input.hasNext("\\*(\\s*[A-Z]+[a-z]*)*")) {
            String begin = _input.next();
            if (begin.equals("*")) {
                rotors[0] = _input.next();
            } else {
                rotors[0] = begin.substring(1);
            }
            for (int i = 1; i < m.numRotors(); i += 1) {
                rotors[i] = _input.next();
            }
            m.insertRotors(rotors);
            String setting = _input.next();
            setUp(m, setting);
            String ringSetting = "";
            Scanner scannerRemain = new Scanner(_input.nextLine());
            if (scannerRemain.hasNext("(\\s*[A-Z]*[a-z]*[0-9]*_*\\.*)*")) {
                ringSetting += scannerRemain.next();
            }
            m.setRings(ringSetting);
            String cycles = "";
            while (scannerRemain.hasNext("(\\([A-Z]+\\))")) {
                cycles += scannerRemain.next();
            }
            m.setPlugboard(new Permutation(cycles, _alphabet));

            while (_input.hasNextLine()
                    && _input.hasNext("(\\s*[A-Z]*[a-z]*[0-9]*_*\\.*)*")) {
                String nextLine = _input.nextLine();
                nextLine = nextLine.replaceAll("\\s+", "");
                printMessageLine(m.convert(nextLine));
            }
            if (_input.hasNextLine()
                    && !_input.hasNext("(\\s*[A-Z]*[a-z]*[0-9]*_*\\.*)*")) {
                _input.useDelimiter("[ \t*]+");
                while (_input.hasNext("(\n)+")) {
                    String empty = _input.next().replaceAll("\r", "");
                    for (int i = 0; i < empty.length(); i += 1) {
                        _output.print("\n");
                    }
                }
                _input.useDelimiter("\\s+");
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.next());
            int numRotors = _config.nextInt();
            int numPawls = _config.nextInt();
            ArrayList<String> rotorNames = new ArrayList<>();
            ArrayList<Rotor> allRotors = new ArrayList<>();
            while (_config.hasNext()) {
                Rotor rotor = readRotor();
                if (rotorNames.contains(rotor.name())) {
                    throw error("readConfig error! Duplicate rotor names");
                }
                rotorNames.add(rotor.name());
                allRotors.add(rotor);
            }
            return new Machine(_alphabet, numRotors, numPawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            Rotor output;
            String rotorName = _config.next();
            if (rotorName.contains("(") || rotorName.contains(")")
                    || rotorName.contains("*") || rotorName.contains(" ")) {
                throw error("readRotor error! Wrong format for name");
            }
            String spec = _config.next();
            if (spec.contains("(") || spec.contains(")")
                    || spec.contains("*") || spec.contains(" ")) {
                throw error("readRotor error! Wrong format for spec");
            }
            String cycles = "";
            while (_config.hasNext(".*[\\(|\\)]+.*")) {
                cycles += _config.next();
            }
            Permutation perm = new Permutation(cycles, _alphabet);
            String kind = "" + spec.charAt(0);
            String notches = spec.substring(1);
            switch (kind) {
            case "R":
                output = new Reflector(rotorName, perm);
                break;
            case "N":
                output = new FixedRotor(rotorName, perm);
                break;
            case "M":
                if (notches.length() == 0) {
                    throw error("readRotor error! Moving rotor with no notch");
                }
                for (int i = 0; i < notches.length(); i += 1) {
                    if (!_alphabet.contains(notches.charAt(i))) {
                        throw error("readRotor error! Notch not in alphabet");
                    }
                }
                output = new MovingRotor(rotorName, perm, notches);
                break;
            default:
                throw error("readRotor error! Unidentified rotor type");
            }
            return output;
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        if (settings.length() == 0) {
            throw error("setUp error! settings has length 0");
        }
        M.setRotors(settings);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i += 1) {
            _output.print(msg.charAt(i));
            if ((i + 1) % 5 == 0 && (i < msg.length() - 1)) {
                _output.print(" ");
            }
        }
        _output.print("\n");
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
