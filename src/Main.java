import java.io.*;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        int[][] nodes = new int[10][2];
        int range = 3;
        int droneRange = 15;
        int hospitals = 3;

        /*//Random generator διαφορετικών μεταξύ τους κόμβων
        Random random = new Random();

        for (int i = 0 ; i < nodes.length ; i++){

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

            myWriter = new FileWriter("points-data.txt");
            for (int n = 0 ; n < nodes.length ; n++){
                myWriter.write(nodes[n][0] + "," + nodes[n][1] + "\n");
            }

            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //Διάβασμα των σημείων από αρχείο
        try {

            File file = new File("points-data.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line = null;
            String[] data;
            int i = 0;
            while ((line = reader.readLine()) != null){
                data = line.split(",");
                nodes[i][0] = Integer.parseInt(data[0]);
                nodes[i][1] = Integer.parseInt(data[1]);
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Υπολογισμός της μέσης απόστασης μεταξύ των σημείων
        double sum = 0 ;
        double max = Double.MIN_VALUE;
        int s = 0;
        for (int i = 0 ; i < nodes.length-1 ; i++){
            for (int j = i+1 ; j < nodes.length ; j++){
                double dis = Math.sqrt((nodes[j][0] - nodes[i][0])*(nodes[j][0] - nodes[i][0]) + (nodes[j][1] - nodes[i][1])*(nodes[j][1] - nodes[i][1]));
                if (dis >= max){
                    max = dis;
                }
                sum = sum + dis;
                s++;
            }
        }
        System.out.println("Sum: " + sum + "   s: " + s);
        System.out.println("Median of distance: " + sum/s);
        System.out.println("Max: " + max);


        Model1 model1 = new Model1(nodes, range, droneRange);

        model1.Solve();

        /*Model2 model2 = new Model2(nodes, range, droneRange);

        model2.Solve();

        Model3 model3 = new Model3(nodes, range, hospitals, droneRange);

        model3.Solve();*/

    }

}
