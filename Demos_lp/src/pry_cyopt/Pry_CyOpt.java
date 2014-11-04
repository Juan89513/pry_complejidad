/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pry_cyopt;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

/**
 *
 * @author juan
 */
public class Pry_CyOpt {

    public static void main(String[] args) {
        try {
            LpSolve solver = LpSolve.makeLp(0, 4);
            // add constraints
            solver.strAddConstraint("3 2 2 1", LpSolve.LE, 4);
            solver.strAddConstraint("0 4 3 1", LpSolve.GE, 3);
            // set objective function
            solver.strSetObjFn("2 3 -2 3");
            // solve the problem
            solver.solve();
            // print solution
            System.out.println("Value  of  objective   function:    "+ solver.getObjective());
            double[] var = solver.getPtrVariables();
            for (int i = 0; i < var.length;
            i++) {
            System.out.println("Value of var[" + i + "] = " + var[i]);
            }
            // delete the problem and free memory
            solver.deleteLp();
        }catch (LpSolveException e) {
            e.printStackTrace();
        }
    }
}
