package game;

import java.io.Serializable;
import java.util.List;

import channel.Channel;

public class Unidad implements Serializable {

	private static final long serialVersionUID = 1L;
	private int bando;
	private int nivel = 1;
	private int batallasGanadas = 1;
	private String id;
	private boolean estoyVivo;
	private Channel<String> canalDePermiso = null;
	private Channel<String> msj = null;
	
	private static Integer idIncremental = 1;

	public Unidad(int bando) {
		
		id = ((bando == 1) ? "g" : "s") + (idIncremental++).toString();
		
		this.setBando(bando);
		this.setEstoyVivo(true);
		new Thread(){
			public void run() {
				while(!Juego.gameOver()) {
					if(canalDePermiso != null) {
						canalDePermiso.receive();
						msj.send("sacar");	
					}
				}	
			}
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
		if(isEstoyVivo()) {
			if(! caminos.isEmpty()) {
				// Elegir un camino random, crear el canal correspondiente y mandarse
				Integer ciudadDestino = caminos.get((int) Math.floor(Math.random() * caminos.size()));
				
				int permisoCamino = getPermisoCamino(idActual, ciudadDestino);
				int accesoCamino = getAccesoCamino(idActual, ciudadDestino);
				
				Channel<Unidad> pCamino = new Channel<Unidad>(permisoCamino);
				Channel<Unidad> aCamino = new Channel<Unidad>(accesoCamino);
				pCamino.receive();
				aCamino.send(this);
				
				
				/*
				/////////////// TO TESTING ////////////////////
				// Para testear voy directo a la ciudad
				Channel<Unidad> camino = new Channel<Unidad>(ciudadDestino);
				// Le subo el nivel cada vez que sale de viaje
				this.nivel++;
				//////////////////////////////////////////////
				*/
			}
		}
	}
	
	/**
	 * @param ID_1 de una ciudad/castillo
	 * @param ID_2 de otra ciudad/castillo
	 * @return el id del camino que comunica las ciudades
	 */
	public Integer getPermisoCamino(Integer ID_1, Integer ID_2) {
		if(ID_1 > ID_2) {
			return 2000 + ID_1 + ID_2;
			//return new Integer(ID_1.toString() + ID_2.toString());
		}
		
		return 1000 + ID_1 + ID_2;
		//return new Integer(ID_2.toString() + ID_1.toString());
	}
	
	public Integer getAccesoCamino(Integer ID_1, Integer ID_2) {
		if(ID_1 > ID_2) {
			return 4000 + ID_1 + ID_2;
		}
		
		return 3000 + ID_1 + ID_2;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Channel<String> getMsj() {
		return msj;
	}

	public void setMsj(Channel<String> msj) {
		this.msj = msj;
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
