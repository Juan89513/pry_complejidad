/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pry_cyopt;

/**
 *
 * @author juan
 */
public class Sitio {
    String nombre;
    double tiempoEnSitio;
    double disponibilidad_inicial;
    double disponibilidad_final;
    
    public Sitio() {
    }

    public Sitio(String nombre, double tiempoEnSitio, double disponibilidad_inicial ,double disponibilidad_final) {
        this.nombre = nombre;
        this.tiempoEnSitio = tiempoEnSitio;
        this.disponibilidad_inicial = disponibilidad_inicial;
        this.disponibilidad_final = disponibilidad_final;
    
    }

    public void setDisponibilidad_inicial(double disponibilidad_inicial) {
        this.disponibilidad_inicial = disponibilidad_inicial;
    }

    public void setDisponibilidad_final(double disponibilidad_final) {
        this.disponibilidad_final = disponibilidad_final;
    }

    public double getDisponibilidad_inicial() {
        return disponibilidad_inicial;
    }

    public double getDisponibilidad_final() {
        return disponibilidad_final;
    }
    
    

    public String getNombre() {
        return nombre;
    }

    public double getTiempoEnSitio() {
        return tiempoEnSitio;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTiempoEnSitio(double tiempoEnSitio) {
        this.tiempoEnSitio = tiempoEnSitio;
    }
    
    
    
}
