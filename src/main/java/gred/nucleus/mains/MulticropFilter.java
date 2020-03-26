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
        ArrayList<String> zSlices = new ArrayList();
        //Scanner readFile =new Scanner("");
        File file = new File("/home/titus/Bureau/TEST_NJ/AUTOCROP/autocrop/SEG_HON4_TEST/test_interessant.txt");
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
                    zSlices.add(""+line.split("\t")[5]+"-"+line.split("\t")[8]);
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
           // System.out.println("indice "+i +" egale a "+rectangleIntersect.get(i));

        }


        ArrayList<String> finalListRectange = new ArrayList<String>();
        for (Map.Entry<Integer, Integer> entry : countIntersect.entrySet()) {
                String listRectangleConnected = "" + entry.getKey();
                String listRectangleConnectedStartTurn="" + entry.getKey();
                for (int i = 0; i < rectangleIntersect.size(); i++) {

                    String[] splitIntersect = rectangleIntersect.get(i).split("-");
                    if (splitIntersect[0].equals(Integer.toString(entry.getKey()))) {


                        String[] splitlist = rectangleIntersect.get(i).split("-");
                        listRectangleConnected = listRectangleConnected + "-" + splitlist[splitlist.length - 1];
                        rectangleIntersect.remove(i);
                        rectangleIntersect.remove(rectangleIntersect.indexOf(splitlist[splitlist.length - 1] + "-" + entry.getKey()));


                        ArrayList<String> listAParcourir = new ArrayList<String>();
                        listAParcourir.add(splitlist[splitlist.length - 1]);
                        while (listAParcourir.size() > 0) {
                            for (int y = 0; y < rectangleIntersect.size(); y++) {
                                String[] splitIntersect2 = rectangleIntersect.get(y).split("-");

                                if (splitIntersect2[0].equals(listAParcourir.get(0))) {

                                    String[] splitlist2 = rectangleIntersect.get(y).split("-");
                                    listAParcourir.add(splitlist2[splitlist.length - 1]);
                                    listRectangleConnected = listRectangleConnected + "-" + splitlist2[splitlist.length - 1];
                                    String[] splitCurrentRectangleConnected=listRectangleConnected.split("-");
                                    rectangleIntersect.remove(y);
                                    rectangleIntersect.remove(rectangleIntersect.indexOf(splitlist2[splitlist.length - 1] + "-" + listAParcourir.get(0)));
                                    for(int SupprRedondantConnection=0;SupprRedondantConnection<splitCurrentRectangleConnected.length;SupprRedondantConnection++) {

                                        //rectangleIntersect.remove(rectangleIntersect.indexOf(splitlist2[splitlist.length - 1] + "-" + splitCurrentRectangleConnected[SupprRedondantConnection]));

                                        System.out.println("\t\t\t\t"+ splitlist2[splitlist.length - 1] + "-" + splitCurrentRectangleConnected[SupprRedondantConnection] +"-------------"
                                        +rectangleIntersect.contains(rectangleIntersect.indexOf(splitlist2[splitlist.length - 1] + "-" + splitCurrentRectangleConnected[SupprRedondantConnection])));
                                        if(rectangleIntersect.contains(splitlist2[splitlist.length - 1] + "-" + splitCurrentRectangleConnected[SupprRedondantConnection])) {
                                            rectangleIntersect.remove(rectangleIntersect.indexOf(splitlist2[splitlist.length - 1] + "-" + splitCurrentRectangleConnected[SupprRedondantConnection]));

                                        }
                                        if(rectangleIntersect.contains( splitCurrentRectangleConnected[SupprRedondantConnection]+"-" +splitlist2[splitlist.length - 1])) {
                                            rectangleIntersect.remove(rectangleIntersect.indexOf(splitCurrentRectangleConnected[SupprRedondantConnection]+"-" +splitlist2[splitlist.length - 1]));

                                        }
                                        System.out.println("eu la list" +listRectangleConnected);
                                    }

                                    y=0;

                                }
                            }
                            listAParcourir.remove(0);
                        }
                    }
                     if(!(listRectangleConnected.equals(listRectangleConnectedStartTurn))) {
                         i--;
                         listRectangleConnectedStartTurn=listRectangleConnected;
                     }
                }
                finalListRectange.add(listRectangleConnected);
            }

        System.out.println("On a des rectancles :"+listRectangle.size());

        ArrayList<Rectangle> listOfRectangleToAdd = new ArrayList<Rectangle>();
        ArrayList<String> listOfRectangleZSliceToAdd = new ArrayList<String>();

        ArrayList<Rectangle> listOfRectangleToRemove = new ArrayList<Rectangle>();
        for (int i = 0; i < finalListRectange.size(); i++) {
            //ArrayList<Rectangle> listRectangle = new ArrayList();

            String[] splitlist2 = finalListRectange.get(i).split("-");
            double xMixNewRectangle=0;
            double yMinNewRectangle=0;
            double maxWidth=0;
            double maxHeigth=0;
            int minZSlice=0;
            int maxZSlice=0;
            if (splitlist2.length > 1) {
                for (int y=0;y<splitlist2.length;y++) {
                    int tmp=Integer.valueOf(splitlist2[y]);
                    if((listRectangle.get(tmp).getX()<xMixNewRectangle) || (xMixNewRectangle==0)){
                        xMixNewRectangle=listRectangle.get(tmp).getX();
                    }
                    if((listRectangle.get(tmp).getY()<yMinNewRectangle) || (yMinNewRectangle==0)){
                        yMinNewRectangle=listRectangle.get(tmp).getY();
                    }
                    if(((listRectangle.get(tmp).getX()+listRectangle.get(tmp).getWidth())>maxWidth) || (maxWidth==0)){
                        maxWidth=listRectangle.get(tmp).getX()+listRectangle.get(tmp).getWidth();
                    }
                    if(((listRectangle.get(tmp).getY()+listRectangle.get(tmp).getHeight())>maxHeigth) || (maxHeigth==0)){
                        maxHeigth=listRectangle.get(tmp).getY()+listRectangle.get(tmp).getHeight();
                    }

                    String[] zSliceTMP=zSlices.get(tmp).split("-");
                    if((Integer.valueOf(zSliceTMP[0])<minZSlice)|| (minZSlice==0)){
                        minZSlice=Integer.valueOf(zSliceTMP[0]);
                    }
                    if(((Integer.valueOf(zSliceTMP[0]+Integer.valueOf(zSliceTMP[1]))>maxZSlice)|| (maxZSlice==0))){
                        maxZSlice=Integer.valueOf(zSliceTMP[0])+Integer.valueOf(zSliceTMP[1]);
                    }
                    listOfRectangleToRemove.add(new Rectangle((int)listRectangle.get(tmp).getX(), (int) listRectangle.get(tmp).getY(), (int) listRectangle.get(tmp).getWidth(), (int) listRectangle.get(tmp).getHeight()));


                }
                maxZSlice=(int) maxZSlice- (int)minZSlice;
                listOfRectangleZSliceToAdd.add((int)minZSlice+"-"+(int)maxZSlice);
                maxWidth=(int)maxWidth-(int) xMixNewRectangle;
                maxHeigth=(int)maxHeigth-(int) yMinNewRectangle;
                listOfRectangleToAdd.add(new Rectangle((int) xMixNewRectangle, (int) yMinNewRectangle, (int) maxWidth, (int) maxHeigth));
                System.out.println((int) xMixNewRectangle+" "+ (int) yMinNewRectangle+" "+ (int) maxWidth+" "+ (int) maxHeigth);
            }
        }
        System.out.println(""+listRectangle.get(0));

        for (int i = 0; i < listOfRectangleToAdd.size(); i++) {
            listRectangle.add(listOfRectangleToAdd.get(i));
            zSlices.add(listOfRectangleZSliceToAdd.get(i));
        }
        System.out.println(""+listRectangle.get(0));
        for (int i = 0; i < listOfRectangleToRemove.size(); i++) {
            int indexRectangleRemove=listRectangle.indexOf(listOfRectangleToRemove.get(i));
            listRectangle.remove(indexRectangleRemove);
            zSlices.remove(indexRectangleRemove);
        }
        System.out.println("On en a plus que :"+listRectangle.size());

        System.out.println("On en a plus que :"+zSlices.size());


    }
    public static double perceOf2Rect(Rectangle2D r1, Rectangle2D r2){
        Rectangle2D r = new Rectangle2D.Double();
        Rectangle2D.intersect(r1, r2, r);

        double fr1 = r1.getWidth() * r1.getHeight();                // area of "r1"
        double f   = r.getWidth() * r.getHeight();                  // area of "r" - overlap
        return (fr1 == 0 || f <= 0) ? 0 : (f / fr1) * 100;          // overlap percentage
    }
}

