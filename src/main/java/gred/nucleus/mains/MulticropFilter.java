package gred.nucleus.mains;

import loci.formats.FormatException;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class MulticropFilter {

    public static void main(String[] args) throws IOException, FormatException,Exception {
        ArrayList<Rectangle> listRectangle = new ArrayList();
        //Scanner readFile =new Scanner("");
        File file = new File("/media/tridubos/DATA1/IMAGE_PB/CropOverlap/AUTOCROP/20220122_1581344088063_Ath_Col0--HON4-wt_Cot_D13_STD_FIXE_H258_H4_w11 DAPI SIM variable.txt");
        try {

            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if ((!(line.matches("^#.*")))
                        && (!(line.matches("^FileName.*")))) {
                    //System.out.println(line);
                    int x1 = Integer.parseInt(line.split("\t")[3]);
                    int y1 = Integer.parseInt(line.split("\t")[4]);
                    int x2 = Integer.parseInt(line.split("\t")[3]) + Integer.parseInt(line.split("\t")[6]);
                    int y2 = Integer.parseInt(line.split("\t")[4]) + Integer.parseInt(line.split("\t")[7]);
                    //System.out.println(x1 + " " + y1 + " " + x2 + " " + y2);
                    listRectangle.add(new Rectangle(x1, y1, x2, y2));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ArrayList<String> rectangleIntersect = new ArrayList();

        for (int i = 0; i < listRectangle.size(); i++) {
            for (int y = listRectangle.size()-1; y > 0; y--) {
                if (((i != y)) && (!((rectangleIntersect.contains(i + "-" + y)))) && (!((rectangleIntersect.contains(y + "-" + i))))) {
                    if (listRectangle.get(i).intersects(listRectangle.get(y))) {
                        rectangleIntersect.add(i + "-" + y);
                        rectangleIntersect.add(y + "-" + i);
                        System.out.println(rectangleIntersect.get(i) + "  "+i);

                    }
                }
            }
        }
        System.out.println(" euu ");

        for (int i = 0; i < listRectangle.size(); i++) {
            if(rectangleIntersect.get(i).matches(Integer.toString(i)+"-*")){
                System.out.println(rectangleIntersect.get(i) + "  "+i);

            }

        }
    }
}

