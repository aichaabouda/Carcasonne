package ch.epfl.chacun;
/*
 * POINTS
 *
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */

import static ch.epfl.chacun.Preconditions.checkArgument;

/**
 * Points offre des méthodes statiques pour calculer les points obtenus dans différentes situations du jeu.
 * Toutes les méthodes sont statiques et la classe est finale pour empêcher l'instanciation.
 */

public final class Points {

    // Constructeur privé pour empêcher l'instanciation de la classe
    private Points() {}

    /**
     * Calcule le nombre de points obtenus par les cueilleurs majoritaires d'une forêt fermée.
     *
     * @param tileCount        le nombre de tuiles formant la forêt fermée
     * @param mushroomGroupCount le nombre de groupes de champignons dans la forêt
     * @return le nombre de points obtenus
     * @throws IllegalArgumentException si les arguments ne satisfont pas les conditions spécifiées
     */
    public static int forClosedForest(int tileCount , int mushroomGroupCount ){
        checkArgument(tileCount>1  && mushroomGroupCount>=0) ;
        return 2*tileCount + 3* mushroomGroupCount ;
    }
    /**
     * Calcule le nombre de points obtenus par les pêcheurs majoritaires d'une rivière fermée.
     *
     * @param tileCount le nombre de tuiles formant la rivière fermée
     * @param fishCount le nombre de poissons dans la rivière
     * @return le nombre de points obtenus
     * @throws IllegalArgumentException si les arguments ne satisfont pas les conditions spécifiées
     */
    public  static int forClosedRiver(int tileCount, int fishCount){
        checkArgument( tileCount>1 && fishCount >=0);
        return tileCount+ fishCount;
    }

    /**
     * Calcule le nombre de points obtenus par les chasseurs majoritaires d'un pré.
     *
     * @param mammothCount le nombre de mammouths dans le pré
     * @param aurochsCount le nombre d'aurochs dans le pré
     * @param deerCount    le nombre de cerfs dans le pré
     * @return le nombre de points obtenus
     * @throws IllegalArgumentException si les arguments ne satisfont pas les conditions spécifiées
     */
    public static int forMeadow( int mammothCount , int aurochsCount , int deerCount ){
        checkArgument( mammothCount >= 0 && aurochsCount>=0 && deerCount >=0);
        return 3* mammothCount + 2*aurochsCount + deerCount ;
    }
    /**
     * Calcule le nombre de points obtenus par les pêcheurs majoritaires d'un réseau hydrographique.
     *
     * @param fishCount le nombre de poissons dans le réseau hydrographique
     * @return le nombre de points obtenus
     * @throws IllegalArgumentException si l'argument ne satisfait pas les conditions spécifiées
     */
    public static int forRiverSystem(int fishCount) {
        checkArgument(fishCount>=0);
        return fishCount ;
    }

    /**
     * Calcule le nombre de points obtenus par le joueur déposant la pirogue dans un réseau hydrographique.
     *
     * @param lakeCount le nombre de lacs dans le réseau hydrographique
     * @return le nombre de points obtenus
     * @throws IllegalArgumentException si l'argument ne satisfait pas les conditions spécifiées
     */
    public static int forLogboat(int lakeCount){
        checkArgument(lakeCount>0);
        return 2*lakeCount;
    }

    /**
     * Calcule le nombre de points supplémentaires obtenus par les pêcheurs majoritaires
     * du réseau hydrographique contenant le radeau.
     *
     * @param lakeCount le nombre de lacs dans le réseau hydrographique
     * @return le nombre de points obtenus
     * @throws IllegalArgumentException si l'argument ne satisfait pas les conditions spécifiées
     */
    public static int forRaft( int lakeCount ) {
        checkArgument(lakeCount>0);
        return lakeCount ;
    }

}
