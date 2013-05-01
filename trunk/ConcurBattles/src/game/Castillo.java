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
		DESTINOS.add(id);
		BANDO = bando;
		FLAG_CASTILLO = bando;
		ID_CITY = id;
		
		// Espera que le avisen cuando crear una nueva unidad porque
		// conquistó una nueva ciudad o se murió una unidad de lvl mayor a 1
		new Thread() {
			public void run() {
				
				Channel<Unidad> unidadNueva = new Channel<Unidad>(FLAG_CASTILLO);
				
				while(true) {
					Unidad unidad = unidadNueva.receive();
					
					System.out.println("ENTRA UNIDAD"+ unidad.getNivel());
					
					// mandarla primero a la arena del lugar
					unidad.viajar(ID_CITY, DESTINOS);
				}
			}
		}.start();
		
	}
	
	public int getBANDO() {
		return BANDO;
	}
	
	public static void main(String[] args) {
		Castillo castillo = new Castillo(1, 1);
		
		Channel<Unidad> unidadNueva = new Channel<Unidad>(castillo.FLAG_CASTILLO);
		
		unidadNueva.send(new Unidad(castillo.getBANDO()));
		unidadNueva.send(new Unidad(castillo.getBANDO()));
	}

}
