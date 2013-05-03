package game;

import java.io.Serializable;
import java.util.List;

import channel.Channel;

public class Unidad implements Serializable {

	private static final long serialVersionUID = 1L;
	private int bando;
	private int nivel = 1;
	private int batallasGanadas = 1;
	private int id;
	private boolean estoyVivo;
	private Channel<String> canalDePermiso;

	public Unidad(int bando) {
		this.setBando(bando);
		this.setEstoyVivo(true);
		new Thread(){
			//ir elgiendo camino.
			/*
			 * 
			 * while (estoyVivo()){
			 * 		
			 * 		canalDePermiso = null
			 * 		elegirCiudad()
			 *      permiso.receive() 
			 * 
			 * }
			 * 
			 * 
			 */
		};
	}
	

	/**
	 * Si el contrincante es de otro bando,
	 * se pelea usando el algoritmo de probabilidades
	 * @param contrincante
	 */
	public void pelear(Unidad contrincante){
		// Si son de distinto bando se pelean
		if(contrincante.getBando() != this.getBando()) {
			
			double misProbabilidadesDeGanar = (double) this.getNivel() / 
			(this.getNivel() + contrincante.getNivel());
			
			if (misProbabilidadesDeGanar >= Math.random()){
				this.ganarPelea();
				contrincante.morir();
			}else{
				contrincante.ganarPelea();
				this.morir();
			}
		}
	}
	
	/**
	 * Elijo un camino random y me mando por ese canal
	 * ¿como sabe el canal a que ciudad sacarme desp?
	 * @param idActual
	 * @param caminos
	 */
	public void viajar(Integer idActual, List<Integer> caminos) {
		if(! caminos.isEmpty()) {
			// Elegir un camino random, crear el canal correspondiente y mandarse
			Integer ciudadDestino = caminos.get((int) Math.floor(Math.random() * caminos.size()));
			
			//int idCamino = getIdCamino(idActual, ciudadDestino);
			
			//Channel<Unidad> camino = new Channel<Unidad>(idCamino);
			
			
			/////////////// TO TESTING ////////////////////
			// Para testear voy directo a la ciudad
			Channel<Unidad> camino = new Channel<Unidad>(ciudadDestino);
			// Le subo el nivel cada vez que sale de viaje
			this.nivel++;
			//////////////////////////////////////////////
			
			
			camino.send(this);
		}
	}
	
	/**
	 * @param ID_1 de una ciudad/castillo
	 * @param ID_2 de otra ciudad/castillo
	 * @return el id del camino que comunica las ciudades
	 */
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
	
	/**
	 * Si mi nivel es mayor a 1, le mando a mi castillo,
	 * para que agregue una nueva unidad
	 */
	private void morir() {
		if(this.getNivel() > 1) {
			(new Channel<Unidad>(this.getBando())).send(new Unidad(this.getBando()));
		}
		this.setEstoyVivo(false);
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
	public boolean isEstoyVivo() {
		return estoyVivo;
	}

	public void setEstoyVivo(boolean estoyVivo) {
		this.estoyVivo = estoyVivo;
	}

	public Channel<String> getCanalDePermiso() {
		return canalDePermiso;
	}

	public void setCanalDePermiso(Channel<String> canalDePermiso) {
		this.canalDePermiso = canalDePermiso;
	}

}
