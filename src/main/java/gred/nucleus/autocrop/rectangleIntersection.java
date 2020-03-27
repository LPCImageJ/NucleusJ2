package gred.nucleus.autocrop;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class rectangleIntersection {

    ArrayList<Rectangle> listRectangle = new ArrayList();

    ArrayList<String> zSlices = new ArrayList();


    ArrayList<String> rectangleIntersect = new ArrayList();

    HashMap<Integer, Integer> countIntersect = new HashMap<Integer, Integer>();

    ArrayList<String> finalListRectange = new ArrayList<String>();
    boolean newBoxesAdded= false;


    public rectangleIntersection(ArrayList<Rectangle> _listRectangle, ArrayList<String> _zSlices) {
        this.listRectangle = _listRectangle;
        this.zSlices=_zSlices;
    }
    public rectangleIntersection(HashMap <Double,Box> _boxes,AutocropParameters m_autocropParameters) {
       for (Map.Entry<Double , Box> entry : _boxes.entrySet()) {
            Box box=entry.getValue();
            int boxWith=box.getXMax()-box.getXMin();
            int boxHeigth=box.getYMax()-box.getYMin();
            int boxSlice=box.getZMax()-box.getZMin();

            this.listRectangle.add(new Rectangle(box.getXMin(),box.getYMin(), boxWith,boxHeigth));
            this.zSlices.add(box.getZMin()+"-"+boxSlice);
        }
    }
    public void runRectangleRecompilation(){
        this.newBoxesAdded= true;
        int tours =0;
        while(this.newBoxesAdded) {
            tours++;
            computeIntersection();
            rectangleRegroupement();
            recompileRectangle();
        }

    }
    public void computeIntersection() {
        this.rectangleIntersect.clear();

        for (int i = 0; i < this.listRectangle.size(); i++) {
            for (int y = 0; y < this.listRectangle.size(); y++) {

                if (((i != y)) && (!((this.rectangleIntersect.contains(i + "-" + y)))) && (!((this.rectangleIntersect.contains(y + "-" + i))))) {

                    if (listRectangle.get(i).intersects(this.listRectangle.get(y))) {

                        if (perceOf2Rect(this.listRectangle.get(i), this.listRectangle.get(y)) > 50 ||
                                perceOf2Rect(this.listRectangle.get(y), this.listRectangle.get(i)) > 50) {

                            this.rectangleIntersect.add(i + "-" + y);
                            this.rectangleIntersect.add(y + "-" + i);
                            if (this.countIntersect.containsKey(i)) {
                                this.countIntersect.put(i, this.countIntersect.get(i) + 1);
                            } else {
                                this.countIntersect.put(i, 1);
                            }
                        }
                    }
                }
            }
        }
    }

    public void rectangleRegroupement() {
        this.finalListRectange.clear();
        for (Map.Entry<Integer, Integer> entry : this.countIntersect.entrySet()) {
            String listRectangleConnected = "" + entry.getKey();
            String listRectangleConnectedStartTurn = "" + entry.getKey();
            for (int i = 0; i < this.rectangleIntersect.size(); i++) {

                String[] splitIntersect = this.rectangleIntersect.get(i).split("-");
                if (splitIntersect[0].equals(Integer.toString(entry.getKey()))) {


                    String[] splitlist = this.rectangleIntersect.get(i).split("-");
                    listRectangleConnected = listRectangleConnected + "-" + splitlist[splitlist.length - 1];
                    this.rectangleIntersect.remove(i);
                    this.rectangleIntersect.remove(this.rectangleIntersect.indexOf(splitlist[splitlist.length - 1] + "-" + entry.getKey()));


                    ArrayList<String> listAParcourir = new ArrayList<String>();
                    listAParcourir.add(splitlist[splitlist.length - 1]);
                    while (listAParcourir.size() > 0) {
                        for (int y = 0; y < this.rectangleIntersect.size(); y++) {
                            String[] splitIntersect2 = this.rectangleIntersect.get(y).split("-");

                            if (splitIntersect2[0].equals(listAParcourir.get(0))) {

                                String[] splitlist2 = this.rectangleIntersect.get(y).split("-");
                                listAParcourir.add(splitlist2[splitlist.length - 1]);
                                listRectangleConnected = listRectangleConnected + "-" + splitlist2[splitlist.length - 1];
                                String[] splitCurrentRectangleConnected = listRectangleConnected.split("-");
                                this.rectangleIntersect.remove(y);
                                this.rectangleIntersect.remove(this.rectangleIntersect.indexOf(splitlist2[splitlist.length - 1] + "-" + listAParcourir.get(0)));
                                for (int SupprRedondantConnection = 0; SupprRedondantConnection < splitCurrentRectangleConnected.length; SupprRedondantConnection++) {

                                    if (this.rectangleIntersect.contains(splitlist2[splitlist.length - 1] + "-" + splitCurrentRectangleConnected[SupprRedondantConnection])) {
                                        this.rectangleIntersect.remove(this.rectangleIntersect.indexOf(splitlist2[splitlist.length - 1] + "-" + splitCurrentRectangleConnected[SupprRedondantConnection]));

                                    }
                                    if (this.rectangleIntersect.contains(splitCurrentRectangleConnected[SupprRedondantConnection] + "-" + splitlist2[splitlist.length - 1])) {
                                        this.rectangleIntersect.remove(this.rectangleIntersect.indexOf(splitCurrentRectangleConnected[SupprRedondantConnection] + "-" + splitlist2[splitlist.length - 1]));
                                    }
                                }
                                y = 0;
                            }
                        }
                        listAParcourir.remove(0);
                    }
                }
                if (!(listRectangleConnected.equals(listRectangleConnectedStartTurn))) {
                    i--;
                    listRectangleConnectedStartTurn = listRectangleConnected;
                }
            }
            this.finalListRectange.add(listRectangleConnected);
        }
    }
    public void recompileRectangle() {
         this.newBoxesAdded= false;
        ArrayList<Rectangle> listOfRectangleToAdd = new ArrayList<Rectangle>();
        ArrayList<String> listOfRectangleZSliceToAdd = new ArrayList<String>();
        ArrayList<Rectangle> listOfRectangleToRemove = new ArrayList<Rectangle>();

        for (int i = 0; i < this.finalListRectange.size(); i++) {

            String[] splitlist2 = this.finalListRectange.get(i).split("-");
            double xMixNewRectangle = 0;
            double yMinNewRectangle = 0;
            double maxWidth = 0;
            double maxHeigth = 0;
            int minZSlice = 0;
            int maxZSlice = 0;
            if (splitlist2.length > 1) {
                for (int y = 0; y < splitlist2.length; y++) {
                    int tmp = Integer.valueOf(splitlist2[y]);
                    if ((this.listRectangle.get(tmp).getX() < xMixNewRectangle) || (xMixNewRectangle == 0)) {
                        xMixNewRectangle = this.listRectangle.get(tmp).getX();
                    }
                    if ((this.listRectangle.get(tmp).getY() < yMinNewRectangle) || (yMinNewRectangle == 0)) {
                        yMinNewRectangle = this.listRectangle.get(tmp).getY();
                    }
                    if (((this.listRectangle.get(tmp).getX() + this.listRectangle.get(tmp).getWidth()) > maxWidth) || (maxWidth == 0)) {
                        maxWidth = this.listRectangle.get(tmp).getX() + this.listRectangle.get(tmp).getWidth();
                    }
                    if (((this.listRectangle.get(tmp).getY() + this.listRectangle.get(tmp).getHeight()) > maxHeigth) || (maxHeigth == 0)) {
                        maxHeigth = this.listRectangle.get(tmp).getY() + this.listRectangle.get(tmp).getHeight();
                    }

                    String[] zSliceTMP = this.zSlices.get(tmp).split("-");
                    if ((Integer.valueOf(zSliceTMP[0]) < minZSlice) || (minZSlice == 0)) {
                        minZSlice = Integer.valueOf(zSliceTMP[0]);
                    }
                    if (((Integer.valueOf(zSliceTMP[0] + Integer.valueOf(zSliceTMP[1])) > maxZSlice) || (maxZSlice == 0))) {
                        maxZSlice = Integer.valueOf(zSliceTMP[0]) + Integer.valueOf(zSliceTMP[1]);
                    }
                    listOfRectangleToRemove.add(new Rectangle((int) this.listRectangle.get(tmp).getX(),
                            (int) this.listRectangle.get(tmp).getY(),
                            (int) this.listRectangle.get(tmp).getWidth(),
                            (int) this.listRectangle.get(tmp).getHeight()));


                }

                maxZSlice = (int) maxZSlice - (int) minZSlice;

                listOfRectangleZSliceToAdd.add((int) minZSlice + "-" + (int) maxZSlice);
                maxWidth = (int) maxWidth - (int) xMixNewRectangle;
                maxHeigth = (int) maxHeigth - (int) yMinNewRectangle;
                listOfRectangleToAdd.add(new Rectangle((int) xMixNewRectangle, (int) yMinNewRectangle, (int) maxWidth, (int) maxHeigth));
            }
        }

        for (int i = 0; i < listOfRectangleToAdd.size(); i++) {
            this.newBoxesAdded= true;

            listRectangle.add(listOfRectangleToAdd.get(i));
           zSlices.add(listOfRectangleZSliceToAdd.get(i));
        }
        for (int i = 0; i < listOfRectangleToRemove.size(); i++) {
            this.newBoxesAdded= true;
            int indexRectangleRemove=listRectangle.indexOf(listOfRectangleToRemove.get(i));
            listRectangle.remove(indexRectangleRemove);
            zSlices.remove(indexRectangleRemove);
        }

    }


    public static double perceOf2Rect(Rectangle2D r1, Rectangle2D r2){
        Rectangle2D r = new Rectangle2D.Double();
        Rectangle2D.intersect(r1, r2, r);

        double fr1 = r1.getWidth() * r1.getHeight();                // area of "r1"
        double f   = r.getWidth() * r.getHeight();                  // area of "r" - overlap
        return (fr1 == 0 || f <= 0) ? 0 : (f / fr1) * 100;          // overlap percentage
    }

    public HashMap<Double,Box> getNewBoxes(){
        HashMap<Double,Box> boxes= new HashMap<Double,Box>();


        for(int i=0;i<listRectangle.size();i++){
            String[] zSliceTMP = this.zSlices.get(i).split("-");
            Integer.valueOf(zSliceTMP[0]);
           /*
            System.out.println((short)(this.listRectangle.get(i).getX())+" "+
                    (short)this.listRectangle.get(i).getY()+" "+
                    (short)this.listRectangle.get(i).getWidth()+" "+
                    (short)this.listRectangle.get(i).getHeight()+" "+
                    Short.valueOf(zSliceTMP[0])+" "+
                    Short.valueOf(zSliceTMP[1]));
*/
            short tmpXmax=(short)(this.listRectangle.get(i).getX()+this.listRectangle.get(i).getWidth());
            short tmpYmax=(short)(this.listRectangle.get(i).getY()+this.listRectangle.get(i).getHeight());

            short tmpZmax=(short)(Short.valueOf(zSliceTMP[0])+     Short.valueOf(zSliceTMP[1]));
            if( tmpZmax==0){
                tmpZmax=1;
            }
            Box box= new Box((short)(this.listRectangle.get(i).getX()),
                    tmpXmax,
                    (short)(this.listRectangle.get(i).getY()),
                    tmpYmax,
                    Short.valueOf(zSliceTMP[0]),
                    tmpZmax);
            boxes.put((double)i,box);
        }
        return boxes;
    }

}
