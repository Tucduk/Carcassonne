package Jeu.Model;

import javafx.scene.paint.Color;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Carcassonne {

    private final int NB_CARTES = 72;

    private static Pioche p;

    private int nbJoueur; // Nombre de joueur
    private int numJoueur; // Numéro du joueur courant
    private Joueur[] tabJoueur; // Tableau de joueurs

    private Map<Point, CartePosee> pointCarteMap; // Map qui contient pour chaque point sa Carte
    private ArrayList<Point> listPointDispo; // Liste de point où l'on peut ajouter une carte
    private ArrayList<Point> listPointOccupe; // Liste de point où il y a déjà une carte posée
    private ArrayList<Carte> defausse; // Liste de carte où il y a la défausse

    private Carte carteDeBase; // Carte posée au départ (à modifier)

    public Carcassonne(){
        // Initialisation des listes et maps

        pointCarteMap = new HashMap<>();
        listPointDispo = new ArrayList<>();
        listPointOccupe = new ArrayList<>();
        defausse = new ArrayList<>();
        p = new Pioche(NB_CARTES);
        // A MODIFIER
        carteDeBase = new Carte(TypeCarte.cartePPPP);

        //carteDeBase.setPosition(new Point(NB_CASES/2,NB_CASES/2));
        //listPointDispo.add(new Point(NB_CASES/2+1,NB_CASES/2));
        //listPointDispo.add(new Point(NB_CASES/2-1,NB_CASES/2));
        //listPointDispo.add(new Point(NB_CASES/2,NB_CASES/2+1));
        //listPointDispo.add(new Point(NB_CASES/2,NB_CASES/2-1));
        //pointCarteMap.put(new Point(NB_CASES/2,NB_CASES/2), carteDeBase);

        carteDeBase.setPosition(new Point(10,10));
        listPointDispo.add(carteDeBase.getPosition());

        // Attributs à modifier en fonction du menu permettant de sélectionner le nombre de joueur et leur nom
    }

    /*
     * Fonction qui permet d'initialiser les joueurs
     */
    public void initialisationJoueurs(String[] nomJoueur, Color[] couleursJoueurs){
        this.nbJoueur=nomJoueur.length;
        tabJoueur = new Joueur[nbJoueur];
        for (int i = 0; i < nbJoueur ; i++) {
            tabJoueur[i]= new Joueur(i+1, p, couleursJoueurs[i]);
            tabJoueur[i].setNom(nomJoueur[i]);
        }
        numJoueur = (int) (Math.random()*(nbJoueur-1))+1;
    }

    /*
     * Demande au joueur suivant de jouer
     */
    public void jouer(){
        for (Joueur aTabJoueur : tabJoueur) {
            if (aTabJoueur.getIdJoueur() == numJoueur) {
                aTabJoueur.joue();
            }
        }
    }

    /*
     * Permet de passer au joueur suivant
     */
    public void joueurSuivant(){
        // C'est très mal fait
        numJoueur++;
        numJoueur = numJoueur %(nbJoueur+1);
        if(numJoueur == 0){
            numJoueur++;
        }
    }

    public void verifZonesDejaOccupees(CartePosee cartePosee) {
        ArrayList<Integer> zonesDejaOccupees = new ArrayList<>();
        int x = (int)cartePosee.getPosition().getX();
        int y = (int)cartePosee.getPosition().getY();

        Point point = new Point(x-1, y);
        if(listPointOccupe.contains(point)){
            CartePosee c = pointCarteMap.get(point);
            if (c.getZonesOccupees().contains(4)) zonesDejaOccupees = contamination(zonesDejaOccupees, cartePosee, 12);
            if (c.getZonesOccupees().contains(5)) zonesDejaOccupees = contamination(zonesDejaOccupees, cartePosee, 11);
            if (c.getZonesOccupees().contains(6)) zonesDejaOccupees = contamination(zonesDejaOccupees, cartePosee, 10);
        }

        point = new Point(x+1, y);
        if(listPointOccupe.contains(point)){
            CartePosee c = pointCarteMap.get(point);
            if (c.getZonesOccupees().contains(12)) zonesDejaOccupees = contamination(zonesDejaOccupees, cartePosee, 4);
            if (c.getZonesOccupees().contains(11)) zonesDejaOccupees = contamination(zonesDejaOccupees, cartePosee, 5);
            if (c.getZonesOccupees().contains(10)) zonesDejaOccupees = contamination(zonesDejaOccupees, cartePosee, 6);
        }

        point = new Point(x, y+1);
        if(listPointOccupe.contains(point)){
            CartePosee c = pointCarteMap.get(point);
            if (c.getZonesOccupees().contains(1)) zonesDejaOccupees = contamination(zonesDejaOccupees, cartePosee, 9);
            if (c.getZonesOccupees().contains(2)) zonesDejaOccupees = contamination(zonesDejaOccupees, cartePosee, 8);
            if (c.getZonesOccupees().contains(3)) zonesDejaOccupees = contamination(zonesDejaOccupees, cartePosee, 7);
        }

        point = new Point(x, y-1);
        if(listPointOccupe.contains(point)){
            CartePosee c = pointCarteMap.get(point);
            if (c.getZonesOccupees().contains(9)) zonesDejaOccupees = contamination(zonesDejaOccupees, cartePosee, 1);
            if (c.getZonesOccupees().contains(8)) zonesDejaOccupees = contamination(zonesDejaOccupees, cartePosee, 2);
            if (c.getZonesOccupees().contains(7)) zonesDejaOccupees = contamination(zonesDejaOccupees, cartePosee, 3);
        }

        for (int i = 0; i < zonesDejaOccupees.size() ; i++) {
            System.out.println(zonesDejaOccupees.get(i));
        }

        cartePosee.setZonesOccupees(zonesDejaOccupees);
    }

    private ArrayList<Integer> contamination(ArrayList<Integer> zonesDejaOccupees, CartePosee cartePosee, int caseContaminee) {
        int indicePresent = -1;
        for (int i = 0; i < cartePosee.getZonesControlleesParLesPoints().length; i++) {
            for (int j = 0; j < cartePosee.getZonesControlleesParLesPoints()[i].length; j++) {
                if (cartePosee.getZonesControlleesParLesPoints()[i][j] == caseContaminee){
                    indicePresent = i;
                }
            }
            if (indicePresent!=-1) {
                for (int j = 0; j < cartePosee.getZonesControlleesParLesPoints()[indicePresent].length; j++) {
                    zonesDejaOccupees.add(cartePosee.getZonesControlleesParLesPoints()[indicePresent][j]);
                }
            }
        }
        return zonesDejaOccupees;
    }

    public Carte getCarteDeBase() { return carteDeBase; }

    public int getNB_CASES() { return NB_CARTES * 2 - 1; }

    public Joueur[] getTabJoueur() { return tabJoueur; }

    public int getNumJoueur() { return numJoueur; }

    public Pioche getP() { return p; }

    public Map<Point, CartePosee> getPointCarteMap() { return pointCarteMap; }

    public ArrayList<Point> getListPointDispo() { return listPointDispo; }

    public ArrayList<Point> getListPointOccupe() { return listPointOccupe; }

    public ArrayList<Carte> getDefausse() { return defausse; }
}
