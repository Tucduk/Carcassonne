package Jeu.View;

//detection de la posisition de la souris.
//souris recupère une ccordonnée dans la zone visible, mais il faut recuperer les coordonnée de la zone sur la map
//penser a linvisible : translation
//

import Jeu.Controller.ControlMouse;
import Jeu.Model.Carcassonne;
import Jeu.Model.Carte;
import Jeu.Model.CartePosee;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class Fenetre extends Parent {

    private static PlaceDispo placeDispo;
    private GraphicsContext graphicsContext;
    private GraphicsContext graphicsContextInfos;
    private ArrayDeque <Image> queueImage;
    private Carcassonne carcassonne;
    private int width;
    private int height;
    private Image imageAffichee;

    public Fenetre(Carcassonne newCarcassonne, int width, int height){
        this.width=width;
        this.height=height;
        carcassonne = newCarcassonne;
        Canvas canvas = new Canvas(carcassonne.getNB_CASES()*50, carcassonne.getNB_CASES()*50);
        Canvas infos = new Canvas(width, 100);
        ControlMouse controlMouse = new ControlMouse(this, "fenetreDeJeu");
        ControlMouse controlMouseInfos = new ControlMouse(this, "barreInfos");
        canvas.setOnMouseClicked(controlMouse);
        infos.setOnMouseClicked(controlMouseInfos);
        graphicsContext = canvas.getGraphicsContext2D();
        //graphicsContextInfos.drawImage(new Image("Jeu/imgCartes/fond.jpg"),0,0,width,height );
        graphicsContextInfos = infos.getGraphicsContext2D();
        placeDispo = new PlaceDispo();
        queueImage = new ArrayDeque<>();
        placerCarte(carcassonne.getCarteDeBase());
        afficherCarteSuivant();
        this.getChildren().add(canvas);
        this.getChildren().add(infos);
    }

    public Carcassonne getCarcassonne() {
        return carcassonne;
    }

    public void placerCarte(Carte carte){
        CartePosee cartePosee = new CartePosee(carte);
        carcassonne.getListPointOccupe().add(cartePosee.getPosition());
        carcassonne.getPointCarteMap().put(carte.getPosition(), cartePosee);
        Image image = cartePosee.getImageCarte();

        int x = (int) cartePosee.getPosition().getX();
        int y = (int) cartePosee.getPosition().getY();

        //bloc de test pour tester les listes
        Point p = new Point(x+1,y);
        testLDispo(p);

        p.setLocation(x-1,y);
        testLDispo(p);

        p.setLocation(x,y+1);
        testLDispo(p);

        p.setLocation(x,y-1);
        testLDispo(p);

        carcassonne.getListPointDispo().remove(cartePosee.getPosition());

        graphicsContext.drawImage(image, x*50,y*50, 50, 50);
    }

    private void testLDispo(Point p){
        ArrayList<Point> lDispo = carcassonne.getListPointDispo();
        ArrayList<Point> lOccupee = carcassonne.getListPointOccupe();
        if ( !lDispo.contains(p) && !lOccupee.contains(p)) {
            lDispo.add(new Point((int)p.getX(), (int)p.getY()));
            queueImage.addLast(placeDispo.getImagePlus());
            graphicsContext.drawImage(queueImage.getLast(),(int)p.getX()*50, (int)p.getY()*50, 50, 50);
        }
    }

    private void drawInformations(Image prochaineCarte){
        graphicsContextInfos.clearRect(0,0,width,100);
        graphicsContextInfos.setFill(Color.BLACK);
        drawLigneSeparatrice();

        String s;

        if(carcassonne.getP().getTaille()<=0){
            s = "Fin de partie";
        }
        else {
            graphicsContextInfos.drawImage(prochaineCarte, (width/2.), 30, 50, 50);

            int numJoueur = carcassonne.getNumJoueur();
            s = "Joueur " + numJoueur;
            s += " : " + carcassonne.getTabJoueur()[numJoueur - 1].getNom();

            int nbPartisans = carcassonne.getTabJoueur()[numJoueur-1].getNombrePartisansRestants();
            Color color = carcassonne.getTabJoueur()[numJoueur-1].getColor();

            String defausse = "Defausse";

            graphicsContextInfos.strokeRect(width*3/4,35,100,30);
            graphicsContextInfos.strokeText(defausse, width*3/4+20,52);

            if (nbPartisans>0){
                // A DEPLACER OUAIS

                //graphicsContextInfos.setFill(color);
                //graphicsContextInfos.fillOval((width/4.)*3, 25, 50, 50);
                //graphicsContextInfos.setFill(Color.BLACK);
                //graphicsContextInfos.strokeText("x "+nbPartisans, (width/4.)*3+50, 35);
            }
        }
        graphicsContextInfos.strokeText(s, (width/2.), 15);
        //VOIR POUR CENTRER LE TEXTE, JE SAIS COMMENT FAIRE FAUT QUE JE REGARDE SUR LE GIT
        this.imageAffichee=prochaineCarte;
    }

    private void drawLigneSeparatrice() {
        graphicsContextInfos.moveTo(0,100);
        graphicsContextInfos.lineTo(width,100);
        graphicsContextInfos.stroke();
    }

    public void rotateCarteSuivante(Carte carte){
        Image image = getImage(carte);
        drawInformations(image);
    }

    private Image getImage(Carte carte){
        int nbRotation=carte.getNbRotation();
        Image image;
        switch (nbRotation){
            case 0:
                image= carte.getDraw().img;
                break;
            case 1:
                image = carte.getDraw().img90;
                break;
            case 2:
                image = carte.getDraw().img180;
                break;
            case 3:
                image = carte.getDraw().img270;
                break;
            default:
                image=null;
        }
        return image;
    }

    public void placerPartisan(int numZone) {
        int numJoueur = (carcassonne.getNumJoueur()-1);
        Carte carte = carcassonne.getTabJoueur()[numJoueur].getCarteEnMain();
        int x = (int)carte.getPosition().getX();
        int y = (int)carte.getPosition().getY();
        Color colorJoueur = carcassonne.getTabJoueur()[numJoueur].getColor();
        System.out.println(numZone);
        switch (numZone){
            case 1:
                x=x*50+(50/6);
                y=y*50+(50/6);
                break;
            case 2:
                x=x*50+(150/6);
                y=y*50+(50/6);
                break;
            case 3:
                x=x*50+(250/6);
                y=y*50+(50/6);
                break;
            case 4:
                x=x*50+(250/6);
                y=y*50+(150/6);
                break;
            case 5:
                x=x*50+(250/6);
                y=y*50+(250/6);
                break;
            case 6:
                x=x*50+(150/6);
                y=y*50+(250/6);
                break;
            case 7:
                x=x*50+(50/6);
                y=y*50+(250/6);
                break;
            case 8:
                x=x*50+(50/6);
                y=y*50+(150/6);
                break;
            case 9:
                x=x*50+(150/6);
                y=y*50+(150/6);
                break;
        }
        graphicsContext.setFill(colorJoueur);
        graphicsContext.fillOval(x-5, y-5, 10, 10);
        carcassonne.getTabJoueur()[numJoueur].placePartisan();
    }

    public void afficheErreur(String erreur, String title){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);

        alert.setContentText(erreur);
        alert.showAndWait();
    }

    public void defausserCarte(Carte carte) {
        // A MODIFIER
        carcassonne.getDefausse().add(carte);
        carcassonne.jouer();
        drawInformations(carcassonne.getTabJoueur()[carcassonne.getNumJoueur()-1].getCarteEnMain().getDraw().getImg());
    }

    public void afficherCarteSuivant() {
        drawInformations(getImage(carcassonne.getP().getProchaineCarte()));
    }
}
