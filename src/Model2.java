import ilog.concert.*;
import ilog.cplex.*;

import java.io.FileWriter;
import java.io.IOException;

//Επιλύει ένα πιο σύνθετο σενάριο, κατά το οποίο υποθέτουμε ότι μπορεί να υπάρξει τουλάχιστον ένας κόμβος που να μην
//μπορεί να βρεθεί μέσα στην εμβέλεια κάποιου σταθμού άρα για να αποφύγουμε την αποτυχία του αλγορίθμου δεν κοιτάμε
//να καλύψουμε όλους τους κόμβους όπως στο Model1 αλλά να τους μεγιστοποιήσουμε
public class Model2 {

    private int[][] nodes;
    private int range;
    private int droneRange;

    public Model2(int[][] nodes, int range, int droneRange){
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

            IloLinearNumExpr sum = cplex.linearNumExpr();
            for (int n = 0 ; n < nodes.length ; n++){

                for (int i = 0 ; i < nodes.length ; i++){
                    if ((nodes[n][0] - nodes[i][0])*(nodes[n][0] - nodes[i][0]) + (nodes[n][1] - nodes[i][1])*(nodes[n][1] - nodes[i][1]) <= range*range){
                        sum.addTerm(1,x[i]);
                    }
                }

            }

            cplex.addMaximize(cplex.diff(sum,obj));

            if (cplex.solve()){
                System.out.println("Σταθμοί του Μοντέλου 2 :");
                for (int n = 0 ; n < nodes.length ; n++){
                    if (cplex.getValue(x[n]) == 1){
                        System.out.println(nodes[n][0] + "  " + nodes[n][1]);
                    }
                }

                //Δημιουργία αρχείου με τους σταθμούς για το MatLab
                try{
                    FileWriter myWriter = new FileWriter("station2.txt");
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