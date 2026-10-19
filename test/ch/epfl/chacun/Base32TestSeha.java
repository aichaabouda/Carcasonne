package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Base32TestSeha {


    @Test
    public void isValidWorksWithEmptyString() {
        String s = "";
        assertThrows(IllegalArgumentException.class, () -> Base32.isValid(s));
    }

    @Test
    public void isValidWorksWithNonValidLength() {
        String s = "AAA";
        assertThrows(IllegalArgumentException.class, () -> Base32.isValid(s));
    }

    @Test
    public void isValidWorksWithNonTrivialString() {
        String s = "AB";
        String s1 = "A2";
        assertTrue(Base32.isValid(s) && Base32.isValid(s1));
    }

    @Test
    public void encodeBits5Works() {
        assertEquals(String.valueOf(Base32.ALPHABET.charAt(16)), Base32.encodeBits5(80));
        assertEquals(String.valueOf(Base32.ALPHABET.charAt(0)), Base32.encodeBits5(0));
    }

    @Test
    public void encodeBits10Works() {
        String s = Base32.encodeBits10(80);
        assertEquals("CQ", s);
    }

    @Test
    public void decodeWorks() {
        assertEquals(80, Base32.decode("CQ"));
        assertEquals(97, Base32.decode("DB"));
    }
}
