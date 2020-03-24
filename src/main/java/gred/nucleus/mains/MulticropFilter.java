package gred.nucleus.mains;

import gred.nucleus.autocrop.Box;
import loci.formats.FormatException;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MulticropFilter {

    public static void main(String[] args) throws IOException, FormatException,Exception {
        ArrayList<Rectangle> listRectangle = new ArrayList();
        //Scanner readFile =new Scanner("");
        File file = new File("/home/titus/Bureau/TEST_NJ/AUTOCROP/autocrop/autocrop_20x20y/test1/test1.txt");
        try {

            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if ((!(line.matches("^#.*")))
                        && (!(line.matches("^FileName.*")))) {
                    //System.out.println(line);
                    int x1 = Integer.parseInt(line.split("\t")[3]);
                    int y1 = Integer.parseInt(line.split("\t")[4]);
                    int x2 = Integer.parseInt(line.split("\t")[6]);
                    int y2 = Integer.parseInt(line.split("\t")[7]);
                    //System.out.println(x1 + " " + y1 + " " + x2 + " " + y2);
                    listRectangle.add(new Rectangle(x1, y1, x2, y2));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(listRectangle.get(0));
        ArrayList<String> rectangleIntersect = new ArrayList();
        HashMap<Integer, Integer> countIntersect = new HashMap<Integer, Integer>();
        for (int i = 0; i < listRectangle.size(); i++) {

            for (int y = 0; y< listRectangle.size(); y++) {
                if(i==2 && y==8 ){
                    System.out.println(perceOf2Rect(listRectangle.get(i), listRectangle.get(y)) +" et "+
                            perceOf2Rect(listRectangle.get(y), listRectangle.get(i)) );
                }
                if (((i != y)) && (!((rectangleIntersect.contains(i + "-" + y)))) && (!((rectangleIntersect.contains(y + "-" + i))))) {
                    if (listRectangle.get(i).intersects(listRectangle.get(y))) {
                        if (perceOf2Rect(listRectangle.get(i), listRectangle.get(y)) > 50 ||
                                perceOf2Rect(listRectangle.get(y), listRectangle.get(i)) > 50) {
                            rectangleIntersect.add(i + "-" + y);
                            rectangleIntersect.add(y + "-" + i);
                            System.out.println(i + "  " + y);
                            if (countIntersect.containsKey(i)) {
                                countIntersect.put(i, countIntersect.get(i) + 1);
                            } else {
                                countIntersect.put(i, 1);
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < rectangleIntersect.size(); i++) {
            System.out.println("indice "+i +" egale a "+rectangleIntersect.get(i));

        }

            System.out.println(" euu "+countIntersect.get(40));
        ArrayList<String> finalListRectange = new ArrayList<String>();
        for (Map.Entry<Integer, Integer> entry : countIntersect.entrySet()) {
                String listRectangleConnected = "" + entry.getKey();
                String listRectangleConnectedStartTurn="" + entry.getKey();
                System.out.println("Avant la boucle " + listRectangleConnected + "  "+entry.getKey());
                for (int i = 0; i < rectangleIntersect.size(); i++) {
                    System.out.println("La on test : " + rectangleIntersect.get(i)+ "et la taille "+rectangleIntersect.size());
                    for (int ii = 0; ii < rectangleIntersect.size(); ii++) {
                        System.out.println("indice "+ii +" egale a "+rectangleIntersect.get(ii));

                    }
                    String[] splitIntersect = rectangleIntersect.get(i).split("-");
                    if (splitIntersect[0].equals(Integer.toString(entry.getKey()))) {
                        System.out.println("et on le passe " +rectangleIntersect.size());
                        String[] splitlist = rectangleIntersect.get(i).split("-");

                        listRectangleConnected = listRectangleConnected + "-" + splitlist[splitlist.length - 1];

                        rectangleIntersect.remove(i);
                        rectangleIntersect.remove(rectangleIntersect.indexOf(splitlist[splitlist.length - 1] + "-" + entry.getKey()));

                        System.out.println("Dans la boucle : " + listRectangleConnected+"  "+rectangleIntersect.size());

                        ArrayList<String> listAParcourir = new ArrayList<String>();
                        listAParcourir.add(splitlist[splitlist.length - 1]);
                        while (listAParcourir.size() > 0) {
                            System.out.println("ici on y est");

                            for (int y = 0; y < rectangleIntersect.size(); y++) {
                                if (rectangleIntersect.get(y).matches(listAParcourir.get(0) + "-*")) {
                                    String[] splitlist2 = rectangleIntersect.get(i).split("-");
                                    listAParcourir.add(splitlist2[splitlist.length - 1]);
                                    listRectangleConnected = listRectangleConnected + "-" + splitlist2[splitlist.length - 1];
                                    rectangleIntersect.remove(y);
                                    rectangleIntersect.remove(rectangleIntersect.indexOf(splitlist2[splitlist.length - 1] + "-" + listAParcourir.get(0)));


                                }
                            }
                            listAParcourir.remove(0);
                            //string.substring(string.length() - 1));
                        }
                    }
                     if(!(listRectangleConnected.equals(listRectangleConnectedStartTurn))) {
                         i--;
                         listRectangleConnectedStartTurn=listRectangleConnected;
                     }
                }
                finalListRectange.add(listRectangleConnected);
            }

        System.out.println("A la fin on a les listes suivante : ");
        for (int i = 0; i < finalListRectange.size(); i++) {
            System.out.println(finalListRectange.get(i));
        }
    }
    public static double perceOf2Rect(Rectangle2D r1, Rectangle2D r2){
        Rectangle2D r = new Rectangle2D.Double();
        Rectangle2D.intersect(r1, r2, r);

        double fr1 = r1.getWidth() * r1.getHeight();                // area of "r1"
        double f   = r.getWidth() * r.getHeight();                  // area of "r" - overlap
        return (fr1 == 0 || f <= 0) ? 0 : (f / fr1) * 100;          // overlap percentage
    }
}

