package pry_cyopt;

import java.util.ArrayList;
import javax.swing.JOptionPane;
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
            //matriz auxiliar de booleanos diciendo si pasa o no por un sitio
            boolean [][] pasaPorUnSitio = new boolean[distanciasEntreSitios.length][distanciasEntreSitios.length];
            
            //por cada nodo hay 4 variables (Tiempo de servicio, Tiempo de llegada,hora minima de llegada, hora maxima de llegada)
            //las variables binarias deben ser la cantidad de caminos que me pasen
           // JOptionPane.showMessageDialog(null,noSitios);
            //int totalVariables = (4 * noSitios) + noVariablesBinarias*2;
            int totalVariables = (2 * noSitios) + noVariablesBinarias;
            //JOptionPane.showMessageDialog(null,totalVariables);
            //System.exit(1);
            int [][] auxResultados=new int[noSitios][noSitios];
            solver = LpSolve.makeLp(0, totalVariables);
            
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
                       // row1[posVariableBinaria+noVariablesBinarias] = 1 * MGrande;
                        int tmp=i+1;
                        int tmp2=j+1;
                     //   System.out.println("i-j"+tmp+"-"+tmp2+"->"+posVariableBinaria);
                        if(posVariableBinaria!=10){
                            row1[posVariableBinaria] = 1 * MGrande;
                        }else{ // machetazoo!!!
                            row1[8] = 1 * MGrande;
                        }
                        
                        double terminoIndependiente1 = MGrande - listaSitios.get(i).getTiempoEnSitio() - distanciasEntreSitios[i][j];
                        auxResultados[i][j]=posVariableBinaria;
                        solver.addConstraint(row1, LpSolve.LE, terminoIndependiente1);
                        matriz.add(row1);
                        terminosIndependientes.add(terminoIndependiente1);
                     }
                     posVariableBinaria++;
                    
                }
            }
                        
            // RESTRICCIONES DE LLEGADA EN TIEMPO ESTABLECIDO (2.1)Debe llegar antes del tiempo final   
            for (int i = 0; i < listaSitios.size(); i++) { 
                double[] row = new double[totalVariables];
                row[i+1] = 1;
                double terminoIndependiente1 = listaSitios.get(i).getDisponibilidad_final();
                solver.addConstraint(row, LpSolve.LE, terminoIndependiente1);
            }
            
            // RESTRICCIONES DE LLEGADA EN TIEMPO ESTABLECIDO (2.2)Debe llegar antes del tiempo inicial, lo comenté porque daña el modelo
            for (int i = 0; i < listaSitios.size(); i++) { 
                double[] row1 = new double[totalVariables];
                row1[i+1] = 1;
                double terminoIndependiente1 = listaSitios.get(i).getDisponibilidad_inicial();
                //solver.addConstraint(row1, LpSolve.GE, terminoIndependiente1);
            }
            
            for (int i = 0; i < listaSitios.size(); i++) { //RESTRICCIONES TIEMPO LLEGADA SITIOS MAYOR O IGUAL A CERO (3)
                double row[] = new double[totalVariables];
                row[i + 1] = 1;
                double terminoIndependiente = 0;
                solver.addConstraint(row, LpSolve.GE, terminoIndependiente);

                matriz.add(row);
                terminosIndependientes.add(terminoIndependiente);
                //indiceTotal++;
            }
            
           // for (int i = noSitios + 1; i < (noSitios + 2*noVariablesBinarias+1); i++) { //SETEO LAS VARIABLES BINARIAS
           for (int i = noSitios + 1; i < (noSitios + noVariablesBinarias+1); i++) { 
                solver.setBinary(i, true);
            }
            
            int acum=0;
            for(int i=0;i<pasaPorUnSitio.length;i++){       //RESTRICCIONES DE CIRCUITO 4.1 PARA FILAS
                double terminoIndependiente = 1;
                boolean tiene_algo_para_agregar=false;
                double row[] = new double[totalVariables];
                for(int j=0;j<pasaPorUnSitio.length;j++){
                    acum++;
                    if(distanciasEntreSitios[i][j]>0){
                         row[noSitios+acum] =1;
                         tiene_algo_para_agregar=true;
                    }
                }
                if(tiene_algo_para_agregar){
                  /*  int tmp=i+1;
                    int tmp2=i+1;
                    for(double x:row)  {
                         System.out.println(tmp+"-"+tmp2+"->"+x);
                    }
                     */
                    solver.addConstraint(row, LpSolve.EQ , terminoIndependiente);
                }
             }
            
            acum=0;
            for(int i=0;i<noSitios;i++){       //RESTRICCIONES DE CIRCUITO 4.2 PARA COLUMNAS; solo funciona para 3 sitios por ahora
                double terminoIndependiente = 1;
                 acum++;
                boolean tiene_algo_para_agregar=false;
                double row[] = new double[totalVariables];
                for(int j=0;j<noSitios;j++){
                    if(distanciasEntreSitios[i][j]>0){
                        row[noSitios+acum] =1;
                        row[noSitios+acum+noSitios] =1;
                        row[noSitios+acum+noSitios*2] =1;
                        tiene_algo_para_agregar=true;
                    }
                }
                if(tiene_algo_para_agregar){
                    solver.addConstraint(row, LpSolve.EQ , terminoIndependiente);
                }
             }
            
            for (int i=0;i<noSitios;i++){ //RESTRICCIONES DE TIEMPO DE ESPERA (5)
                //
                double row[] = new double[totalVariables+1];
                double terminoIndependiente=listaSitios.get(i).getDisponibilidad_inicial();
                row[i+1]=1; 
                row[i+noSitios+noVariablesBinarias+1]=1; 
                solver.addConstraint(row, LpSolve.EQ , terminoIndependiente);
            }
            
            
            acum=0;
            double rowObjetivo[]=new double[1+2*noSitios+noSitios*noSitios];
            for(int i=0;i<pasaPorUnSitio.length;i++){  // FUNCION OBJETIVO (6) ; FALTA MINIMIZAR TIEMPOS DE ESPERA, ESTA COMO TSP ESTANDAR
                for(int j=0;j<pasaPorUnSitio.length;j++){
                    acum++;
                    rowObjetivo[noSitios+acum] =distanciasEntreSitios[i][j];
                 }
            }
            
            for (int i=0;i<noSitios;i++){ //RESTRICCIONES DE TIEMPO DE ESPERA (5)
                //
                rowObjetivo[i+noSitios+noVariablesBinarias+1] =1;
            }
            
            solver.setObjFn(rowObjetivo);
            solver.writeLp("test"+noSitios+".lp");
            solver.solve();
            
            //Traer funcion objetivo
            objetivo = solver.getObjective();
            System.out.println("Value of objective function: " + objetivo);

            //Interpretación de resultados
            double[] valoresOptimos = solver.getPtrVariables();
            for(int i=0;i<valoresOptimos.length;i++){
                double valor=valoresOptimos[i];
                int tmp=i+1;
                System.out.println(tmp+"-"+valor);
            }
             System.out.println("numero de sitios " + noSitios);
             String resultado="";
        for (int i = 0; i < noSitios; i++) {
            resultado += "Sitio " + (i + 1) + " tiempo llegada: " + valoresOptimos[i] + "\n";
            System.out.println("Sitio " + (i + 1) + " tiempo llegada: " + valoresOptimos[i] );
        }
        

        int cantitadVariablesBinarias = sp.cantitadVariablesBinarias();
        System.out.println("cantidad variables binarias" + 2*cantitadVariablesBinarias);

        resultado += "________________________________________________\n";
        //funcion objetivo, pasa o no pasa por el sitio ij 
        acum=0;
        System.exit(1);
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
    
    public int[] devolverRowObj(int nro_sitios,int totalVariablesBooleanas){
        int[]result=new int[nro_sitios+totalVariablesBooleanas+1];
        for(int i=0;i<nro_sitios;i++){
            result[i+1]=0;
       }
        for(int i=0;i<nro_sitios;i++){
            result[i+1]=0;
        }
        
     return null;   
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
       // System.out.println("M->"+M);
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
