import ilog.concert.*;
import ilog.cplex.*;

import java.io.FileWriter;
import java.io.IOException;

//Επιλύει ένα πιο σύνθετο σενάριο, κατά το οποίο καλούμαστε να βρούμε τους κατάλληλους κόμβους για την δημιουργία των
//σταθμών ώστε να καλύψουμε μόνο τους κόμβους που αποτελούν νοσοκομεία, με την προϋπόθεση ότι οι εμβέλειες των σταθμών
//καλύπτονται μεταξύ τους ώστε να έχουμε μονοπάτι άπο κάθε νοσοκομείο σε κάθε άλλο
public class Model3 {

    private int[][] nodes;
    private int range;
    private int hospitals;
    private int droneRange;

    public Model3(int[][] nodes, int range, int hospitals, int droneRange){
        this.nodes = nodes;
        this.range = range;
        this.hospitals = hospitals;
        this.droneRange = droneRange;
    }

    public void Solve(){

        try{

            IloCplex cplex = new IloCplex();
            //μεταβλητή απόφασης που δηλώνει αν κάποιος κόμβος γίνει σταθμός
            IloNumVar[] x = new IloNumVar[nodes.length];
            for (int n = 0 ; n < nodes.length ; n++){
                x[n] = cplex.boolVar();
            }

            //Περιορισμός που ελέγχει αν ο κάθε κόμβος που αποτελεί νοσοκομείο ανήκει στο range ενός τουλάχιστον σταθμού. Αυτό που
            //κάνει είναι: για κάθε κόμβο βρίσκω ένα sum το αποτελείται από το γινόμενο το 1 με την μεταβλητή
            //απόφασης μόνο στην περίπτωση όπου ο κόμβος ανήκει στο range της τρέχουσας μεταβλητής απόφασης
            //και ο λόγος που κάνω το γινόμενο με την μεταβλητή απόφασης είναι γιατί θέλω να το ελέγξω μόνο μς
            //τους κόμβους που αποτελούν σταθμούς
            for (int n = 0 ; n < hospitals ; n++){

                IloLinearNumExpr sum = cplex.linearNumExpr();
                for (int i = 0 ; i < nodes.length ; i++){
                    if ((nodes[n][0] - nodes[i][0])*(nodes[n][0] - nodes[i][0]) + (nodes[n][1] - nodes[i][1])*(nodes[n][1] - nodes[i][1]) <= range*range){
                        sum.addTerm(1,x[i]);
                    }
                }

                cplex.addGe(sum,1);
            }

            //Περιορισμός που ελέγχει αν ο κάθε σταθμός απέχει όσο και η απόσταση που μπορεί να πετάξει κάθε drone από
            //τουλάχιστον έναν άλλον σταθμό
            for (int n = 0 ; n < nodes.length ; n++){

                IloNumExpr sum = cplex.numExpr();
                for (int i = 0 ; i < nodes.length ; i++){
                    if (Math.sqrt((nodes[n][0] - nodes[i][0])*(nodes[n][0] - nodes[i][0]) + (nodes[n][1] - nodes[i][1])*(nodes[n][1] - nodes[i][1])) <= droneRange){
                        sum = cplex.sum(sum,cplex.prod(x[n],cplex.prod(x[i],1)));
                    }
                }

                cplex.addGe(sum,1);
            }

            //Αντικειμενική συνάρτηση
            IloLinearNumExpr obj = cplex.linearNumExpr();
            for (int n = 0 ; n < nodes.length ; n++){
                obj.addTerm(1,x[n]);
            }

            cplex.addMinimize(obj);

            if (cplex.solve()){
                System.out.println("Σταθμοί του Μοντέλου 3:");
                for (int n = 0 ; n < nodes.length ; n++){
                    if (cplex.getValue(x[n]) == 1){
                        System.out.println(nodes[n][0] + "  " + nodes[n][1]);
                    }
                }

                //Δημιουργία αρχείου με τους σταθμούς για το MatLab
                try{
                    FileWriter myWriter = new FileWriter("station3.txt");
                    for (int n = 0 ; n < nodes.length ; n++){
                        if (cplex.getValue(x[n]) == 1){
                            myWriter.write("p = nsidedpoly(1000,'Center',[" + nodes[n][0] + " " + nodes[n][1] +"],'Radius'," + range + ") \n");
                            myWriter.write("plot(p,'FaceColor','r') \n");
                            myWriter.write("hold on \n");
                        }
                    }
                    myWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        catch (IloException exc){
            exc.printStackTrace();
        }

    }

}