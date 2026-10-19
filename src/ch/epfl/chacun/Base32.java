package ch.epfl.chacun;

/**
 * BASE32
 * Classe pour l'encodage et le décodage en Base32.
 * Cette classe ne peut pas être instanciée.
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */

public class Base32 {
    private Base32() {}// Constructeur privé pour une classe non instanciable

    /** Alphabet utilisé pour l'encodage en Base32 */
    public static final String ALPHABET="ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    /**
     * Vérifie si une chaîne est valide en Base32, donc si tous ses caractères font partie de l'alphabet Base32.
     *
     * @param s la chaîne à vérifier
     * @return true si la chaîne est valide, false sinon
     */
    public static boolean isValid(String s){
        return s.chars().allMatch(c-> ALPHABET.indexOf(c) !=-1);

    }

    /**
     * Encode une valeur de 5 bits en un caractère Base32.
     *
     * @param value la valeur de 5 bits à encoder
     * @return le caractère Base32 correspondant
     * @throws IllegalArgumentException si la valeur ne peut pas être représentée sur 5 bits
     * (si elle est supérieure à 31).
     */
    public static String encodeBits5(int value){
        Preconditions.checkArgument((value >> 5) == 0);
        //Le masque value & 0x1F assure que seuls les 5 bits de poids faible de value sont pris en compte.
        return String.valueOf(ALPHABET.charAt(value & 0x1F));
    }

    /**
     * Encode une valeur de 10 bits en deux caractères Base32.
     *
     * @param value la valeur de 10 bits à encoder
     * @return la chaîne de deux caractères Base32 correspondante
     * @throws IllegalArgumentException Si la valeur ne peut pas être représentée sur 10 bits
     * (si elle est supérieure à 1023).
     */
    public static String encodeBits10(int value) {
        Preconditions.checkArgument((value >> 10) ==0);
        int high = (value >> 5) & 0x1F; // Les 5 bits de poids fort
        int low = value & 0x1F;         // Les 5 bits de poids faible
        return encodeBits5(high) + encodeBits5(low);

    }

    /**
     * Décode une chaîne de 1 ou 2 caractères Base32 en une valeur entière.
     * @param s la chaîne à décoder
     * @return la valeur entière correspondant à la chaîne Base32
     * @throws IllegalArgumentException si la longueur de la chaîne n'est pas égale à 1 ou 2
     * @throws IllegalArgumentException si la chaîne contient des caractères non valides,
     * donc si la chaine n'est pas en Base32
     */

    public static int decode(String s){
        Preconditions.checkArgument(s.length()==1 || s.length()==2);
        Preconditions.checkArgument(isValid(s));

        int value=0;
        for (int i = 0; i < s.length(); i++) {
            int charValue = ALPHABET.indexOf(s.charAt(i));
            value= (value << 5) | charValue;
        }

        return value;

    }
}
