/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pry_cyopt;

import java.util.ArrayList;

/**
 *
 * @author juan
 */
public class FactoriaSitio {
    
    /**
     * @param args the command line arguments
     */
    
    int noSitios;
    ArrayList<Sitio> listaSitios;
    double [][] distanciasSitios;

    public double[][] getDistanciasSitios() {
        return distanciasSitios;    }
    
    public void imprimirInformacionSitios() {
        System.out.println("Cantidad Sitios: " + noSitios + "\nSitios\tT.Preparacion\tT.Entrega");
        for (int i=0; i<listaSitios.size(); i++) {
            System.out.println(i+"\t"+listaSitios.get(i).getTiempoEnSitio()+"\t"+listaSitios.get(i).getDisponibilidad_final());
        }
        System.out.println("_______________________\nMatriz distancias");
        for (int i=0; i<distanciasSitios.length; i++) {
            String fila = "";
            for (int j=0; j<distanciasSitios[i].length; j++) {
                fila+=distanciasSitios[i][j]+"\t";
            }
            System.out.println(fila);
        }
    }

    public void setNositios(int noSitios) {
        this.noSitios = noSitios;
    }

    public void setListaSitios(ArrayList<Sitio> listaSitios) {
        this.listaSitios = listaSitios;
    }



    public ArrayList<Sitio> getListaSitio() {
        return listaSitios;
    }
    
    
    private void transformarTiempoAlistamiento(double [][] arregloAlistamientos) { 
        //SI SE HIZO EL sitioInicial Y SE VA A CONTINUAR CON LA sitioFinal TOMA UN TIEMPO DE tiempoAlistamiento
        distanciasSitios = new double[noSitios][noSitios]; 
        for (int i=0; i<arregloAlistamientos.length; i++) {
            int sitioInicial = (int) arregloAlistamientos[i][0];
            int sitioFinal = (int) arregloAlistamientos[i][1];
            double tiempoAlistamiento = arregloAlistamientos[i][2];
            
            distanciasSitios[sitioInicial-1][sitioFinal-1]=tiempoAlistamiento;
        }       
        
    }   

    
    public void asignarValores (String rutaArchivo){
        
        //LEYENDO ARCHIVO Y GUARDANDO CADA LÍNEA EN lineasArchivo
        Lector lector = new Lector();
        ArrayList<String> lineasArchivo = lector.leerArchivo(rutaArchivo);
        
        noSitios = Integer.parseInt(lineasArchivo.get(0));
        
        listaSitios = new ArrayList();
        
        
        for (int i=1; i<=noSitios; i++) { //GUARDANDO CADA SITIO EN listaSitio
            String lineaSitio = lineasArchivo.get(i);
            String [] tmp = lineaSitio.split(" ");
            double tiempoEnSitio = Double.parseDouble(tmp[1]);
            double tiempoInicial = Double.parseDouble(tmp[2]);
            double tiempoFinal = Double.parseDouble(tmp[3]);
            
            Sitio sitio = new Sitio();
            sitio.setTiempoEnSitio(tiempoEnSitio);
            sitio.setDisponibilidad_inicial(tiempoInicial);
            sitio.setDisponibilidad_final(tiempoFinal);
            
            listaSitios.add(sitio);
        }      
        
        
        //QUITANDO INFORMACIÓN YA TOMADA DE lineasArchivo
        ArrayList<String> lineasAlistamiento = removerSitios(noSitios, lineasArchivo);
        
        
        double arregloAlistamientos [][] = new double [lineasAlistamiento.size()][3];
        
        //CONSTRUYENDO ARREGLO DE ALISTAMIENTOS CON FORMATO IGUAL QUE EN ARCHIVO
        for (int i=0; i<lineasAlistamiento.size(); i++) {
            String [] tmp = lineasAlistamiento.get(i).split(" ");
            //System.out.println("partiendo linea " +i);
            arregloAlistamientos[i][0] = Double.parseDouble(tmp[0]);
            arregloAlistamientos[i][1] = Double.parseDouble(tmp[1]);
            arregloAlistamientos[i][2] = Double.parseDouble(tmp[2]);
        }
        
        transformarTiempoAlistamiento(arregloAlistamientos); //CREANDO MATRIZ DE ALISTAMIENTO
    }
    
    private ArrayList<String> removerSitios(int n, ArrayList<String> lineasArchivo) {
        
        ArrayList<String> lineasAlistamiento = new ArrayList();        
        for (int i=n+1; i<lineasArchivo.size(); i++) {
            lineasAlistamiento.add(lineasArchivo.get(i));
        }
        
        return lineasAlistamiento;
    }

    public int getNoSitios() {
        return noSitios;
    }
    
    public int cantitadVariablesBinarias() {
        //return noSitios*(noSitios-1);
        return noSitios*noSitios;
    }

    
}
