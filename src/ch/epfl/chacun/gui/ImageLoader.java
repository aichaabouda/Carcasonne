package ch.epfl.chacun.gui;
import javafx.scene.image.Image;

import static java.lang.StringTemplate.STR;
/**
 * IMAGELOADER
 * a pour but de charger les images des tuiles.
 * Elle fournit des méthodes statiques pour charger les images des tuiles de taille normale et grande.
 * Les chemins des images sont construits en fonction de l'identifiant de la tuile et de la taille spécifiée.
 * Les tailles des images sont définies comme des constantes publiques dans la classe.
 *
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */


public final class ImageLoader {

    private ImageLoader(){} // Le constructeur est privé pour empêcher l'instanciation de la classe

    // Définition des constantes pour les tailles des images
    public static final int LARGE_TILE_PIXEL_SIZE=512;  //Taille des grandes tuiles
    public static final int LARGE_TILE_FIT_SIZE=256; //Taille d'affichage des grandes tuiles
    public static final int NORMAL_TILE_PIXEL_SIZE=256; //Taille des tuiles normales
    public static final int NORMAL_TILE_FIT_SIZE= 128; //Taille d'affichage des tuiles normales
    public static final int MARKER_PIXEL_SIZE=96; //Taille du marqueur
    public static final int MARKER_FIT_SIZE=48; //Taille d'affichage du marqueur


    /**
     * Construit le nom de l'image en fonction de l'identifiant de la tuile.
     * Elle ajoute 0 si l'identifiant est inférieur à 10, car ils sont notés avec un 0 dans le dossier de ressources
     * @param idTile l'identifiant de la tuile
     * @return le nom de l'image correspondant à l'identifiant de la tuile
     */
    private static String nameImage(int idTile){
        return (idTile<10) ?
                STR."0\{idTile}"
                : STR."\{idTile}";
    }

    /**
     * Charge l'image de la tuile de taille normale correspondant à l'identifiant de la tuile.
     *
     * @param idTile l'identifiant de la tuile
     * @return l'image de la tuile de taille normale
     */
    public static Image normalImageForTile(int idTile){
        return new Image(STR."/\{NORMAL_TILE_PIXEL_SIZE}/\{nameImage(idTile)}.jpg");
    }

    /**
     * Charge l'image de la grande tuile correspondant à l'identifiant de la tuile.
     *
     * @param idTile l'identifiant de la tuile
     * @return l'image de la grande tuile
     */
    public static Image largeImageForTile(int idTile){
        return new Image(STR."/\{LARGE_TILE_PIXEL_SIZE}/\{nameImage(idTile)}.jpg");
    }
}


