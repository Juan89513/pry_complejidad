package pry_cyopt;

import java.util.ArrayList;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;

public class ModeloTSP {

    public LpSolve solver;
    public FactoriaSitio sp;
    public double[] valoresOptimos;
    public double objetivo;

    public String resolverModelo(String rutaArchivo/*, String tipoBAndBound*/) {
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
           // JOptionPane.showMessageDialog(null,noSitios);
            int totalVariables = (4 * noSitios) + noVariablesBinarias;
            solver = LpSolve.makeLp(0, totalVariables);
            
            //FUNCION OBJETIVO (NO ESTA CLARA AÚN)
            String respuesta="";
            int acum_variables_puestas=0;
            
            for (int i = 0; i < listaSitios.size(); i++) { 
                acum_variables_puestas++;
            }
            
            respuesta=completarConCeros(acum_variables_puestas,totalVariables,"0");
            solver.strSetObjFn(respuesta);
            double MGrande=calcularMGrande() ;
            int posVariableBinaria = noSitios+1;
            
            for (int i = 0; i < listaSitios.size(); i++) { //RESTRICCIONES DE ORDEN(1)
                for (int j = 0; j < listaSitios.size(); j++) {
                     if (i!=j) {
                        double[] row1 = new double[totalVariables];
                        row1[i+1] = 1;
                        row1[j+1] = -1;
                        row1[posVariableBinaria] = 1 * MGrande;
                        double terminoIndependiente1 = MGrande - listaSitios.get(i).getTiempoEnSitio() - distanciasEntreSitios[i][j];
                        solver.addConstraint(row1, LpSolve.LE, terminoIndependiente1);
                        matriz.add(row1);
                       // System.out.println("termino independiente"+terminoIndependiente1);
                        for(int ix=0;ix<row1.length;ix++){
                            //System.out.println("row"+ix+row1[ix]);
                        }
                        //System.out.println("tipo constraint"+LpSolve.LE);
                        //System.out.println("////////////////////////////////////////");
                        terminosIndependientes.add(terminoIndependiente1);
                        posVariableBinaria++;
                    }
                }
            }
            
            // RESTRICCIONES DE LLEGADA EN TIEMPO ESTABLECIDO (2)
            //Debe llegar antes del tiempo final   
            for (int i = 0; i < listaSitios.size(); i++) { 
                double[] row = new double[totalVariables];
                row[i+1] = 1;
                double terminoIndependiente1 = listaSitios.get(i).getDisponibilidad_final();
                solver.addConstraint(row, LpSolve.LE, terminoIndependiente1);
            }
            // 2.1Debe llegar antes del tiempo inicial
            for (int i = 0; i < listaSitios.size(); i++) { 
                double[] row1 = new double[totalVariables];
                row1[i+1] = 1;
                double terminoIndependiente1 = listaSitios.get(i).getDisponibilidad_inicial();
                solver.addConstraint(row1, LpSolve.GE, terminoIndependiente1);
            }
            
            for (int i = 0; i < listaSitios.size(); i++) { //RESTRICCIONES TIEMPO LLEGADA SITIOS MAYOR O IGUAL A CERO
                double row[] = new double[totalVariables];
                row[i + 1] = 1;
                double terminoIndependiente = 0;
                solver.addConstraint(row, LpSolve.GE, terminoIndependiente);

                matriz.add(row);
                terminosIndependientes.add(terminoIndependiente);
                //indiceTotal++;
            }
            
            for (int i = noSitios + 1; i < (noSitios + noVariablesBinarias); i++) { //VARIABLES BINARIAS
                solver.setBinary(i, true);
                //System.out.println("seteando variable binaria: " +i);
            }
            
            int limite=0;
            
            for (int i = 0; i < listaSitios.size(); i++) { //RESTRICCIONES DE CIRCUITO
                int sitioSiguiente=limite+1;
                double terminoIndependiente = 1;
                //las primeras sitio variables en 1; el resto en 0
                int tmp=i+1;
                int inicio=i+listaSitios.size();
                limite=tmp*listaSitios.size();
                //Desde i+1 hasta limite
                System.out.println("variable->"+sitioSiguiente+"-"+"limite->"+limite);
                double row[] =devolverRow(3,sitioSiguiente, limite, noVariablesBinarias);
                   
                for(int j=0;j<row.length;j++){
                        System.out.println("j->"+j+"value->"+row[j]);
                }
                
                solver.addConstraint(row, LpSolve.EQ , terminoIndependiente);

                matriz.add(row);
                terminosIndependientes.add(terminoIndependiente);
                //indiceTotal++;
            }
            
            solver.writeLp("test.lp");
            //solver.setObjFn(rowObj);
            // print solution
            objetivo = solver.getObjective();
            System.out.println("Value of objective function: " + objetivo);

            // delete the problem and free memory
            solver.deleteLp();

            System.exit(1);
             
            int indiceTotal = 0;
            for (int i = 0; i < listaSitios.size(); i++) { 
                for (int j = 0; j < listaSitios.size(); j++) {
                    if (i!=j) {
                        double[] row1 = new double[totalVariables];
                        row1[i+1] = 1;
                        row1[j+1] = -1;
                        row1[posVariableBinaria] = 1 ;
                        double terminoIndependiente1 = listaSitios.get(i).getDisponibilidad_final();
                        solver.addConstraint(row1, LpSolve.LE, terminoIndependiente1);
                        matriz.add(row1);
                        terminosIndependientes.add(terminoIndependiente1);
                        posVariableBinaria++;
                    }
                }
            }
            ///IMPRIMIR EN FORMATO LP
            solver.writeLp("test.lp");
        }
        catch(LpSolveException e){
        }
        return "";
    }
    
    String completarConCeros(int numeroVariablesPuestas,int numeroTotalVariables,String rhs){
        String result="";
        for(int i=0;i<numeroTotalVariables;i++){
            if(i<numeroVariablesPuestas){
                result+="1 ";
            }
            else{
                result+="0 ";
            }
        }
        
        result+=rhs;
        return result;
    }
    public double[] devolverRow(int nro_sitios,int sitioInicial,int sitioFinal,int totalVariablesBooleanas){
        
        double[]row=new double[totalVariablesBooleanas+nro_sitios+1];
        
        for(int i=0;i<nro_sitios;i++){
            row[i+1]=0;
        }
        
        for(int i=0;i<totalVariablesBooleanas;i++){
            if(i>=sitioInicial-1 && i<sitioFinal){
                row[i+nro_sitios+1]=1;
            }
            else{
                row[i+nro_sitios+1]=0;
            }
        }
        
        return row;
    }
     private double calcularMGrande() {
        double M=0;
        
        ArrayList<Sitio> listaSitios = sp.getListaSitio();
        for(Sitio s : listaSitios) {
            M+=s.getTiempoEnSitio();
            M+=s.getDisponibilidad_final();
            M+=s.getDisponibilidad_inicial();
        }
        
        double [][] matrizAlistamiento = sp.getDistanciasSitios();
        
        for (int i=0; i<matrizAlistamiento.length; i++) {
            for (int j=0; j<matrizAlistamiento[i].length; j++) {
                M+=matrizAlistamiento[i][j];
            }
        }
        System.out.println("M->"+M);
        return M;
    }
    
    public static void main(String[] args) {
        ModeloTSP ms = new ModeloTSP();
      /*  double[]resultRow=ms.devolverRow(0,1, 3, 9);
        for(int i=0;i<resultRow.length;i++){
            System.out.println(resultRow[i]);
        }
       System.exit(1);*/
        System.out.println(ms.resolverModelo("prueba2.txt"));
    }
    
}
