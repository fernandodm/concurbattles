package game;

import java.util.ArrayList;
import java.util.List;

import channel.Channel;

public class Ciudad {
	
	private final int ID_CITY;
	private int BANDO;
	private List<Unidad> UNIDADES = new ArrayList<Unidad>();
	private final List<Integer> DESTINOS = new ArrayList<Integer>();
	
	//falta pasarle como parametro los destinos
	public Ciudad(int id){
		ID_CITY = id;
		
		
		new Thread() {
			public void run() {
				
				Channel<Unidad> unidadNueva = new Channel<Unidad>(ID_CITY);
				
				while(true) {
					Unidad unidad = unidadNueva.receive();
					
					System.out.println("ENTRA UNIDAD al castillo "+BANDO + " UNIDAD lvl: "+ unidad.getNivel());
					
					// mandarla primero a la arena del lugar
					unidad.viajar(ID_CITY, DESTINOS);
				}
			}
		}.start();
	}
	
	
	public List<Integer> getDESTINOS() {
		return DESTINOS;
	}

	public int getBANDO() {
		return BANDO;
	}
	public void setBANDO(int bANDO) {
		BANDO = bANDO;
	}
	public List<Unidad> getUNIDADES() {
		return UNIDADES;
	}
	public void setUNIDADES(List<Unidad> uNIDADES) {
		UNIDADES = uNIDADES;
	}
	public int getID_CITY() {
		return ID_CITY;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
