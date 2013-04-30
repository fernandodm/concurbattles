package game;

import java.util.ArrayList;
import java.util.List;

import channel.Channel;

public class Castillo {
	
	private final int ID_CITY;
	private final int FLAG_CASTILLO;
	private final int BANDO;
	private final List<Integer> DESTINOS = new ArrayList<Integer>();
	
	public Castillo(int bando, int id) {
		
		BANDO = bando;
		FLAG_CASTILLO = bando;
		ID_CITY = id;
		
		// Espera que le avisen cuando crear una nueva unidad porque
		// conquistó una nueva ciudad o se murió una unidad de lvl mayor a 1
		new Thread() {
			public void run() {
				
				Channel<Integer> unidadNueva = new Channel<Integer>(FLAG_CASTILLO);
				
				while(true) {
					unidadNueva.receive();
					Unidad nuevaUnidad = new Unidad(BANDO);
					nuevaUnidad.viajar(ID_CITY, DESTINOS);
				}
			}
		}.start();
		
	}
}
