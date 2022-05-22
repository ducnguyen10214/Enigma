package enigma;

import org.junit.Test;
import static org.junit.Assert.*;

public class AlphabetTest {
    @Test
    public void goodTests() {
        Alphabet alpha = new Alphabet();
        assertEquals(26, alpha.size());
        assertTrue(alpha.contains('A'));
        assertFalse(alpha.contains('@'));
        assertEquals(0, alpha.toInt('A'));
        assertEquals(25, alpha.toInt('Z'));
        assertEquals('A', alpha.toChar(0));
        assertEquals('Z', alpha.toChar(25));

        Alphabet beta = new Alphabet("ABCD");
        assertEquals(4, beta.size());
        assertFalse(beta.contains('@'));
        assertFalse(beta.contains('!'));
        assertFalse(beta.contains(')'));
        assertEquals(1, beta.toInt('B'));
        assertEquals(2, beta.toInt('C'));
    }

    @Test(expected = EnigmaException.class)
    public void badConstructors() {
        Alphabet a = new Alphabet("");
        Alphabet a1 = new Alphabet("$+-!~()");
        Alphabet a2 = new Alphabet("AAZ");
        Alphabet a3 = new Alphabet("abc");
        Alphabet a4 = new Alphabet(" ");
    }

    @Test(expected = EnigmaException.class)
    public void badToChar() {
        Alphabet alpha = new Alphabet("ABCD");
        alpha.toInt('Z');
        alpha.toInt('H');
    }

    @Test(expected = EnigmaException.class)
    public void badToInt() {
        Alphabet alpha = new Alphabet("ABCD");
        alpha.toChar(5);
        alpha.toChar(-1);
        alpha.toChar(99);
    }
}
