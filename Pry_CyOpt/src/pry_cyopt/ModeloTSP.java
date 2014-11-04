/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pry_cyopt;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import lpsolve.LpSolve;


public class ModeloTSP {

    public LpSolve solver;
    public FactoriaSitio sp;
    public double[] valoresOptimos;
    public double objetivo;

    public String resolverModelo(String rutaArchivo//, String tipoBAndBound
    ) {
        ArrayList<Double> terminosIndependientes = new ArrayList();
        double timeElapsed=0;
        long totalIter=0;
        ArrayList<double[]> matriz = new ArrayList();
    try {
            sp = new FactoriaSitio();
            sp.asignarValores(rutaArchivo);
            int noSitios = sp.getNoSitios();
            int noVariablesBinarias = sp.cantitadVariablesBinarias();
            ArrayList<Sitio> listaSitios = sp.getListaSitio();
            double[][] distanciasEntreSitios = sp.getDistanciasSitios();
            
            //por cada nodo hay 4 variables (Tiempo de servicio, Tiempo de llegada,hora minima de llegada, hora maxima de llegada)
            //las variables binarias deben ser la cantidad de caminos que me pasen
            int totalVariables = (4 * noSitios) + noVariablesBinarias;
            solver = LpSolve.makeLp(0, totalVariables);
            
            // RESTRICCIONES

            int posVariableBinaria = noSitios+1;
            int indiceTotal = 0;
            for (int i = 0; i < listaSitios.size(); i++) { //RESTRICCIONES DE ORDEN
                for (int j = 0; j < listaSitios.size(); j++) {

                    if (i!=j) {
                        double[] row1 = new double[totalVariables];
                        row1[i+1] = 1;
                        row1[j+1] = -1;
                        row1[posVariableBinaria] = 1 ;
                        double terminoIndependiente1 = listaSitios.get(i).getTiempoEnSitio();
                        
                        solver.addConstraint(row1, LpSolve.LE, terminoIndependiente1);
                        matriz.add(row1);
                        terminosIndependientes.add(terminoIndependiente1);
                        posVariableBinaria++;
                    }
                }
            }
            solver.writeLp("test.lp");
            /* IMPRIMIR EN FORMATO .LP
            solver.writeLp("model.lp");*/

    }catch(Exception e){
    }
    
/*
            // add constraints

            int posVariableBinaria = noSitios+1;
            int indiceTotal = 0;
            for (int i = 0; i < listaSopas.size(); i++) { //RESTRICCIONES DE ORDEN
                for (int j = 0; j < listaSopas.size(); j++) {

                    if (i!=j) {
                        double[] row1 = new double[totalVariables];
                        row1[i+1] = 1;
                        row1[j+1] = -1;
                        row1[posVariableBinaria] = 1 * MGrande;
                        double terminoIndependiente1 = MGrande - listaSopas.get(i).getDuracionPreparacion() - tiemposAlistamiento[i][j];
                        
                        solver.addConstraint(row1, LpSolve.LE, terminoIndependiente1);
                        matriz.add(row1);
                        terminosIndependientes.add(terminoIndependiente1);
                        posVariableBinaria++;
                    }
                }
            }

            for (int i = 0; i < listaSopas.size(); i++) { //RESTRICCIONES DE SOPAS CON VARIABLES DE HOLGURA
                double[] row = new double[totalVariables];
                row[i + 1] = 1;
                int indiceSMas = (2 * i) + noSopas + noVariablesBinarias;
                int indiceSMenos = (2 * i + 1) + noSopas + noVariablesBinarias;
                row[indiceSMas + 1] = 1;
                row[indiceSMenos + 1] = -1;
                double terminoIndependiente = listaSopas.get(i).getTiempoEntrega() - listaSopas.get(i).getDuracionPreparacion();

                //System.out.println("termino independiente" + indiceTotal + " = " + terminoIndependiente);
                indiceTotal++;

                matriz.add(row);
                solver.addConstraint(row, LpSolve.EQ, terminoIndependiente);
                terminosIndependientes.add(terminoIndependiente);
            }


            for (int i = 0; i < listaSopas.size(); i++) { //RESTRICCIONES TIEMPO INICIO DE SOPAS MAYOR O IGUAL A CERO
                double row[] = new double[totalVariables];
                row[i + 1] = 1;
                double terminoIndependiente = 0;
                solver.addConstraint(row, LpSolve.GE, terminoIndependiente);

                matriz.add(row);
                terminosIndependientes.add(terminoIndependiente);
                indiceTotal++;
            }

            for (int i = 0; i < listaSopas.size(); i++) { //RESTRICCIONES VARIABLES DE HOLGURA MAYORES O IGUALES A CERO
                double[] row1SMas = new double[totalVariables];
                double[] row2SMenos = new double[totalVariables];

                int indiceSMas = (2 * i) + noSopas + noVariablesBinarias;
                int indiceSMenos = (2 * i + 1) + noSopas + noVariablesBinarias;
                row1SMas[indiceSMas + 1] = 1;
                row2SMenos[indiceSMenos + 1] = 1;
                double terminoIndependiente1 = 0;
                double terminoIndependiente2 = 0;

                solver.addConstraint(row1SMas, LpSolve.GE, terminoIndependiente1);

                matriz.add(row1SMas);
                indiceTotal++;
                matriz.add(row2SMenos);
                indiceTotal++;
                terminosIndependientes.add(terminoIndependiente1);
                terminosIndependientes.add(terminoIndependiente2);
                solver.addConstraint(row2SMenos, LpSolve.GE, terminoIndependiente2);
            }

            for (int i = 0; i < listaSopas.size(); i++) { //RESTRICCION PARA LA FUNCION OBJETIVO R MAYOR O IGUAL A SMENOS (RETRASO)
                double[] row = new double[totalVariables];
                row[totalVariables - 1] = 1;
                int indiceSMenos = (2 * i + 1) + noSopas + noVariablesBinarias;
                row[indiceSMenos + 1] = -1;
                double terminoIndependiente = 0;

                matriz.add(row);
                terminosIndependientes.add(terminoIndependiente);
                indiceTotal++;
                solver.addConstraint(row, LpSolve.GE, terminoIndependiente);
            }

            double [] restriccionTotalVariablesBinariasEnUno = new double[totalVariables];
            for (int i = noSopas + 1; i < (noSopas + noVariablesBinarias + 1); i++) { //SETEANDO VARIABLES BINARIAS
                solver.setBinary(i, true);
                restriccionTotalVariablesBinariasEnUno[i] = 1;
                //System.out.println("seteando variable binaria: " +i);
            }
            
            solver.addConstraint(restriccionTotalVariablesBinariasEnUno, LpSolve.EQ, noSopas-1);          

            // set objective function
            double[] rowObj = new double[totalVariables];
            rowObj[totalVariables - 1] = 1;
            //solver.addConstraint(rowObj, LpSolve.GE, 0);
            matriz.add(rowObj);

            agregarRestriccionesDeIAN(noSopas, totalVariables, matriz);
            agregarRestriccionesDeNAI(noSopas, totalVariables, matriz);
            
            sp.imprimirInformacionSopas();
            solver.setObjFn(rowObj);

            // solve the problem
            
            this.setReglaBranchAndBound(tipoBAndBound);
            
            System.out.println("resolviendo problema");
            solver.solve();
            timeElapsed = solver.timeElapsed();
            totalIter = solver.getTotalIter();
            //JOptionPane.showMessageDialog(null, "no iteraciones= "+solver.getTotalIter());
            //JOptionPane.showMessageDialog(null, "tiempo transcurrido= "+timeElapsed);
            
            solver.writeLp("model"+noSopas+".lp");
            
            //imprimirMatrizCoeficientes(matriz);

            // print solution
            objetivo = solver.getObjective();
            System.out.println("Value of objective function: " + objetivo);

            valoresOptimos = solver.getPtrVariables();
            for (int i = 0; i < valoresOptimos.length; i++) {
                System.out.println("Value of var[" + i + "] = " + valoresOptimos[i]);
            }

            // delete the problem and free memory
            solver.deleteLp();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //imprimirMatrizCoeficientes(matriz);
        return getResultadoModelo(tipoBAndBound, timeElapsed, totalIter);

    }//FINAL CONSTRUIR MATRIZ MODELO
    
    private void setReglaBranchAndBound(String tipoBAndB) {//0 default, 1 CEILING, 2 FLOOR
        
        if (tipoBAndB.equals("Ceiling")) solver.setBbRule(LpSolve.BRANCH_CEILING);
        if (tipoBAndB.equals("Floor")) solver.setBbRule(LpSolve.BRANCH_FLOOR);
        System.out.println("");
    }
    
    private void agregarRestriccionesDeIAN(int noSopas, int totalVariables, ArrayList<double[]> matriz) throws LpSolveException {
        int indiceIAN = noSopas+1;
        for (int i=0; i<noSopas; i++) {
            double [] row = new double[totalVariables];
            for (int j=0; j<noSopas; j++) {
                if (i!=j) {
                    row[indiceIAN] = 1;
                    indiceIAN++;
                }
            }
            matriz.add(row);
            solver.addConstraint(row, LpSolve.LE, 1);
        }
    }
    
    private void agregarRestriccionesDeNAI(int noSopas, int totalVariables, ArrayList<double[]> matriz) throws LpSolveException {
        
        int indicePrimeraFilaPrimerRestriccion = 2*noSopas;
        double row1[] = new double[totalVariables];
        
        for(int i=1; i<noSopas; i++) {
            row1[indicePrimeraFilaPrimerRestriccion] = 1;
            indicePrimeraFilaPrimerRestriccion+=noSopas-1;
        }
        matriz.add(row1);
        solver.addConstraint(row1, LpSolve.LE, 1);
        
        int incremetador = (2*noSopas)-1;
        
        for (int i=0; i<noSopas-1; i++) {
            int indicePrimeraBinaria = noSopas+1+i;
            double [] row = new double[totalVariables];
            for (int j=0; j<noSopas-1; j++) {
                if (i!=j) {
                    row[indicePrimeraBinaria] = 1;
                    indicePrimeraBinaria+=noSopas-1;
                } else {
                    row[indicePrimeraBinaria] = 1;
                    indicePrimeraBinaria+=incremetador;
                }
            }
            matriz.add(row);
            solver.addConstraint(row, LpSolve.LE, 1);
        }
    }

    public String getResultadoModelo(String tipoBAndB, double tiempoSolucion, long totalIteraciones) {
        String resultado = "";

        int noSopas = sp.getNoSopas();

        System.out.println("numero de sopas " + noSopas);
        for (int i = 0; i < noSopas; i++) {
            resultado += "Sopa " + (i + 1) + " tiempo inicio: " + valoresOptimos[i] + "\n";
        }

        int cantitadVariablesBinarias = sp.cantitadVariablesBinarias();
        System.out.println("cantidad variables binarias" + cantitadVariablesBinarias);

        resultado += "________________________________________________\n";

        for (int i = 0; i < noSopas; i++) {
            //SMas es la holgura positiva (lo que se puede haber adelantado una sopa
            //SMenos es la hogura negativa (lo que se puede haber retrazado una sopa)
            int indiceSMas = (2 * i) + noSopas + cantitadVariablesBinarias;
            int indiceSMenos = (2 * i + 1) + noSopas + cantitadVariablesBinarias;

            if (valoresOptimos[indiceSMas] > 0 && valoresOptimos[indiceSMenos] > 0) {
                resultado += "Sopa " + (i + 1) + " tiene un tiempo de retrazo de: \n";
            }
            if (valoresOptimos[indiceSMas] == 0) {
                resultado += "Sopa " + (i + 1) + " se entrego " + valoresOptimos[indiceSMenos] + " mas tarde\n";
            }
            if (valoresOptimos[indiceSMenos] == 0) {
                resultado += "Sopa " + (i + 1) + " se entrego " + valoresOptimos[indiceSMas] + " mas temprano\n";
            }
        }
        resultado += "________________________________________________\n";
        resultado += "El M justo calculado es: " + MGrande + "\n";
        resultado += "El valor óptimo para la función objetivo es: " + objetivo+"\n";
        resultado += "El orden de las sopas debe ser:\n";
        resultado += ordenDePreparacion()+"\n";
        resultado += "________________________________________________\n";
        resultado +="Regla de Branch & Bound: "+tipoBAndB+"\n";
        resultado +="Tiempo de calculo: "+tiempoSolucion+"\n";
        resultado +="No total de iteraciones: "+ totalIteraciones;
        
        return resultado;
    }

    public String ordenDePreparacion() {

        int noSopas = sp.getNoSopas();
        int[] ordenSopas = new int[noSopas];

        for (int i = 0; i<noSopas; i++) {
            if(valoresOptimos[i]==0) ordenSopas[0]=i;
        }
        
        double sgteMenor;
        
        for(int i=1; i<noSopas; i++) {
            sgteMenor=Double.MAX_VALUE;
            int indiceSgteMenor=-1;
            for(int j=0; j<noSopas; j++) {
                if(valoresOptimos[j]<sgteMenor && valoresOptimos[j]>valoresOptimos[ordenSopas[i-1]]) {
                    sgteMenor=valoresOptimos[j];
                    indiceSgteMenor=j;
                }
                ordenSopas[i] = indiceSgteMenor;
            }
        }
        
        String stringOrden = "";
        
        
        for (int i=0; i<ordenSopas.length; i++) {
            if (i==ordenSopas.length-1) stringOrden += (ordenSopas[i]+1);
            else stringOrden += (ordenSopas[i]+1)+" -> ";
        }
        
        return stringOrden;
    }

    public void borrarModelo() {
        // delete the problem and free memory
        solver.deleteLp();
    }

    private void imprimirMatrizCoeficientes(ArrayList<double[]> matriz) {
        for (double[] row : matriz) {
            System.out.println("_____________");
            String fila = "";
            for (double coeficiente : row) {
                fila += coeficiente + "\t";
            }
            System.out.println(fila);
        }
    }

    public ModeloSopas() {
    }

    public ModeloSopas(SoupFactory sp) {
        this.sp = sp;
    }

    public void setMGrande(int MGrande) {
        this.MGrande = MGrande;
    }

    public double getMGrande() {
        return MGrande;
    }

    public LpSolve getSolver() {
        return solver;
    }

    public static void main(String[] args) {
        ModeloSopas ms = new ModeloSopas();
        
        System.out.println(ms.resolverModelo("benchmark 7 sopas.txt", "Ceiling"));
    }
    
    private double calcularMGrande() {
        double M=0;
        
        ArrayList<Sopa> listaSopas = sp.getListaSopas();
        for(Sopa s : listaSopas) {
            M+=s.getDuracionPreparacion();
            M+=s.getTiempoEntrega();
        }
        
        double [][] matrizAlistamiento = sp.getTiemposAlistamiento();
        
        for (int i=0; i<matrizAlistamiento.length; i++) {
            for (int j=0; j<matrizAlistamiento[i].length; j++) {
                M+=matrizAlistamiento[i][j];
            }
        }
        
        return M;
                */
        return "";
    }
}
