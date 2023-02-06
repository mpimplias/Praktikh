import ilog.concert.*;
import ilog.cplex.*;

import java.io.FileWriter;
import java.io.IOException;

//Επιλύει το απλό σενάριο, κατά το οποίο υποθέτουμε ότι όλοι οι κόμβοι μπορούν να ανήκουν στην εμβέλεια
//κάποιου σταθμού
public class Model1 {

    private int[][] nodes;
    private int range;
    private int droneRange;

    public Model1(int[][] nodes, int range, int droneRange){
        this.nodes = nodes;
        this.range = range;
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

            //Περιορισμός που ελέγχει αν ο κάθε κόμβος ανήκει στο range ενός τουλάχιστον σταθμού. Αυτό που
            //κάνει είναι: για κάθε κόμβο βρίσκω ένα sum το αποτελείται από το γινόμενο του 1 με τη μεταβλητή
            //απόφασης μόνο στην περίπτωση όπου ο κόμβος ανήκει στο range της τρέχουσας μεταβλητής απόφασης
            //και ο λόγος που κάνω το γινόμενο με τη μεταβλητή απόφασης είναι γιατί θέλω να το ελέγξω μόνο με
            //τους κόμβους που αποτελούν σταθμούς
            for (int n = 0 ; n < nodes.length ; n++){

                IloLinearNumExpr sum = cplex.linearNumExpr();
                for (int i = 0 ; i < nodes.length ; i++){
                    if (Math.sqrt((nodes[n][0] - nodes[i][0])*(nodes[n][0] - nodes[i][0]) + (nodes[n][1] - nodes[i][1])*(nodes[n][1] - nodes[i][1])) <= range){
                        sum.addTerm(1,x[i]);
                    }
                }

                cplex.addGe(sum,1);
            }

            //Περιορισμός που ελέγχει αν ο κάθε σταθμός απέχει όσο και η απόσταση που μπορεί να πετάξει κάθε drone από
            //τουλάχιστον έναν άλλον σταθμό
            for (int n = 0 ; n < nodes.length-1 ; n++){

                IloNumExpr sum = cplex.numExpr();
                for (int i = n+1 ; i < nodes.length ; i++){
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
                System.out.println("Σύνολο σταθμών του μοντέλου 1: " + cplex.getObjValue());
                System.out.println("Σταθμοί του Μοντέλου 1 :");
                for (int n = 0 ; n < nodes.length ; n++){
                    if (cplex.getValue(x[n]) == 1){
                        System.out.println(nodes[n][0] + "  " + nodes[n][1]);
                    }
                }

                //Δημιουργία αρχείου με τους σταθμούς για το MatLab
                try{
                    FileWriter myWriter = new FileWriter("station1.txt");
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
