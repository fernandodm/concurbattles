package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import channel.Channel;

public class Unidad implements Serializable {

	private static final long serialVersionUID = 1L;
	private int bando;
	private int nivel = 1;
	private int batallasGanadas = 1;
	private String id;
	private boolean estoyVivo;
	private Integer canalDePermiso;
	private Integer msj;
	private Integer ciudadAnterior;
	
	public Integer miCanal;
	
	private static Integer idIncremental = 1;

	public Integer getCiudadAnterior() {
		return ciudadAnterior;
	}

	public void setCiudadAnterior(Integer ciudadAnterior) {
		this.ciudadAnterior = ciudadAnterior;
	}

	public Unidad(int bando) {
		
		this.setBando(bando);
		id = ((bando == 1) ? "g" : "s") + (idIncremental++).toString();
		
		this.setEstoyVivo(true);
		this.setCiudadAnterior(bando);
		miCanal = GeneradorDeCanal.generarNumeroDeCanal();
		canalDePermiso = GeneradorDeCanal.generarNumeroDeCanal();
		
		Channel<String> notificacionUI = new Channel<String>(Juego.inputChannel);
		notificacionUI.send(id +" "+ bando + "");
		
		/*
		new Thread(){
			public void run() {
				while(!Juego.gameOver()) {
					
					/////////////////////////////////////////
					//Channel<Integer> canalPermiso = new Channel<Integer> (canlDePermiso.receive());
					//Channel<Integer> canalParaMensaje = new Channel<Integer> (miCanal.receive());
					//canalPermiso.receive();
					//canalParaMensaje.send("sacar");
					////////////////////////////////////////
					
					
					
					if(canalDePermiso != null) {
						Channel<String> permiso = new Channel<String>(getCanalDePermiso());
						Channel<String> mensaje = new Channel<String>(getMsj());
					
						permiso.receive();
						mensaje.send("sacar");
					}
				}	
			}
		}.start();*/
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
	 * Elijo un destino random y me mando por ese canal
	 * 
	 * @param idActual
	 * @param caminos
	 */
	public void viajar(Integer idActual, List<ArrayList<Integer>> destinos) {

		if(isEstoyVivo()) {
			if(! destinos.isEmpty()) {
				// Elegir un camino random, crear el canal correspondiente y mandarse
				List<Integer> destino = destinos.get((int) Math.floor(Math.random() * destinos.size()));
				
				
				int permisoDestino = (  getPermisoDestino(destino.get(0))  )   + destino.get(1);
				int accesoDestino = getAccesoDestino(destino.get(0)) + destino.get(1) ;
				/*
				switch (destino.get(0)) {
				
				case 1:
					System.out.println("Unidad " +id+" de bando "+bando+" ha elegido viajar " );
					break;
				case 2:
					System.out.println("Unidad " +id+" de bando "+bando+" ha elegido viajar " );
					break;
				case 3:
					System.out.println("Unidad " +id+" de bando "+bando+" ha elegido viajar " );
					break;
				}*/
				Channel<Unidad> pCamino = new Channel<Unidad>(permisoDestino);
				Channel<Unidad> aCamino = new Channel<Unidad>(accesoDestino);
				pCamino.receive();
				aCamino.send(this);

				//System.out.println("Viajo a la ciudad "+ destino.get(1));
				System.out.println(id +" de bando "+ bando +" decidio viajar a "+ destino.get(1));
				
				Channel<String> notificacionUI = new Channel<String>(Juego.inputChannel);
				if(destinos.size() == 2) {
					notificacionUI.send(this.getId() +" "+ destino.get(destinos.size() - 1) + " 0.5");	
				} else {
					notificacionUI.send(this.getId() +" "+ destino.get(destinos.size() - 1) + "");
				}
				


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
	 
	 * @return el id del camino que comunica las ciudades
	 */
	public Integer getPermisoDestino(Integer tipoDestino) {
		switch (tipoDestino) {
		case 1:
			return 1000+300;
			
		case 2:
			return 2000+300;
			
		case 3:
			return 3000+300;
			

		
		}
		return null;
		
		
	}
	
	public Integer getAccesoDestino(Integer tipoDestino) {
		switch (tipoDestino) {
		case 1:
			return 1000+400;
			
		case 2:
			return 2000+400;
			
		case 3:
			return 3000+400;
			

		
		}
		return null;
		
		
	}
	
	/**
	 * Computa una nueva batalla ganada,
	 * si el numero es fibonacci subis de nivel
	 */
	private void ganarPelea() {
		this.batallasGanadas += 1;
		
		if(this.esFibonacci(this.getBatallasGanadas())) {
			this.nivel += 1;
			System.out.println("Unidad" + this.getId() + " de bando " + this.getBando() + " ha sibido de nivel");
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
		
		Channel<String> notificacionUI = new Channel<String>(Juego.inputChannel);
		notificacionUI.send(id);
	}
	
	/**
	 *<El santo grial>
	 *
	 *Decido si quiero viajar o no y respondo con booleano
	 *
	 * @return
	 */
	public boolean decidirViajar(){
		return (Math.random()) > 0.0001;
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
	
	public Integer getMsj() {
		return msj;
	}

	public void setMsj(Integer msj) {
		this.msj = msj;
	}
	
	public boolean isEstoyVivo() {
		return estoyVivo;
	}

	public void setEstoyVivo(boolean estoyVivo) {
		this.estoyVivo = estoyVivo;
	}

	public Integer getCanalDePermiso() {
		return canalDePermiso;
	}

	public void setCanalDePermiso(Integer canalDePermiso) {
		this.canalDePermiso = canalDePermiso;
	}

}
