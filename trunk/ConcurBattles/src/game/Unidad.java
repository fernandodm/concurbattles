package game;

import game.Unidad;

import java.io.Serializable;
import java.util.List;

import channel.Channel;

public class Unidad implements Serializable{

	private static final long serialVersionUID = 1L;
	private int bando;
	private int nivel = 1;
	private int batallasGanadas = 1;
	
	public Unidad(int bando) {
		this.setBando(bando);
	}
	
	public double probabilidadDeGanar(Unidad contrincante){
		
		return  (double) this.getNivel() / 
				(this.getNivel() + contrincante.getNivel());
	}
	
	public void pelear(Unidad contrincante){
		
		
		if (this.probabilidadDeGanar(contrincante) >= contrincante.probabilidadDeGanar(this)){
			this.ganarPelea();
			contrincante.morir();
		}else{
			contrincante.ganarPelea();
			this.morir();
		}
	}
	
	public void viajar(Integer idActual, List<Integer> caminos) {
		// Elegir un camino random, crear el canal correspondiente y mandarse
		Integer ciudadDestino = caminos.get((int) Math.floor(Math.random() * caminos.size()));
		
		int idCamino = getIdCamino(idActual, ciudadDestino);
		
		Channel<Unidad> camino = new Channel<Unidad>(idCamino);
		
		camino.send(this);
	}
	
	public Integer getIdCamino(Integer ID_1, Integer ID_2) {
		if(ID_1 < ID_2) {
			return new Integer(ID_1.toString() + ID_2.toString());
		}
		
		return new Integer(ID_2.toString() + ID_1.toString());
	}
	
	/**
	 * Computa una nueva batalla ganada,
	 * si el numero es fibonacci subis de nivel
	 */
	private void ganarPelea() {
		this.batallasGanadas += 1;
		
		if(this.esFibonacci(this.getBatallasGanadas())) {
			this.nivel += 1;
		}
	}
	
	private void morir() {
	}
	
	/**
	* Calcula si un numero pertenece a  la serie de fibonaci
	* @param numero
	* @return
	*/
	private boolean esFibonacci(int numero){
		return (  this.esCuadradoPerfecto ( (5 * (numero*numero))+4) ) ||
				(this.esCuadradoPerfecto ( 5 * (numero*numero)-4));
	}
	
	/**
	* calcula si un numero es cuadrado perfecto
	* @param num
	* @return true cuando la raiz cuadrada es entera
	*/
	private boolean esCuadradoPerfecto(int num){
		double raiz = Math.sqrt(num);
		return ( (raiz - Math.floor(raiz))  == 0);
	}

	public int getBando() {
		return bando;
	}

	public void setBando(int bando) {
		this.bando = bando;
	}

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	public int getBatallasGanadas() {
		return batallasGanadas;
	}

	public void setBatallasGanadas(int batallasGanadas) {
		this.batallasGanadas = batallasGanadas;
	}

}
