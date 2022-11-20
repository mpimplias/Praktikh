import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        int[][] nodes = new int[10][2];
        int range = 3;

        //Random generator διαφορετικών μεταξύ τους κόμβων
        Random random = new Random();

        for (int i = 0 ; i < nodes.length-1 ; i++){

            boolean flag;
            int x;
            int y;
            do {
                x = random.nextInt(10);
                y = random.nextInt(10);

                flag = true;
                for (int j = 0 ; j < nodes.length ; j++){
                    if (x == nodes[j][0] && y == nodes[j][1]){
                        flag = false;
                    }
                }
            }while(!flag);

            nodes[i][0] = x;
            nodes[i][1] = y;
        }
        nodes[9][0]=100;
        nodes[9][1]=100;
        for (int n = 0 ; n < nodes.length ; n++){
            System.out.println(nodes[n][0] + "  " + nodes[n][1]);
        }

        //Δημιουργία αρχείου με τα σημεία για το MatLab
        try {
            FileWriter myWriter = new FileWriter("points.txt");
            for (int n = 0 ; n < nodes.length ; n++) {
                myWriter.write("x=" + nodes[n][0] + "\n");
                myWriter.write("y=" + nodes[n][1] + "\n");
                myWriter.write("plot(x,y,'.','markersize',8) \n");
                myWriter.write("hold on \n");
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Model1 model1 = new Model1(nodes, range);

        model1.Solve();

        /*Model2 model2 = new Model2(nodes, range);

        model2.Solve();

        Model3 model3 = new Model3(nodes, range, 3);

        model3.Solve();*/

    }

}
