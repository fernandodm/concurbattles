package game;

import java.util.ArrayList;
import java.util.List;

import channel.Channel;

public class Ciudad {
	
	private final int ID_CITY;
	private int BANDO;
	private List<Unidad> UNIDADES = new ArrayList<Unidad>();
	private final List<Integer> DESTINOS;
	
	public Ciudad(int id, List<Integer> destinos){
		ID_CITY = id;
		DESTINOS = destinos;
		
		new Thread() {
			public void run() {
				
				Channel<Unidad> unidadNueva = new Channel<Unidad>(ID_CITY);
				Channel<Unidad> enviarAlCastillo = new Channel<Unidad>(getBANDO());
				Channel<Unidad> enviarALaArena = new Channel<Unidad>(ID_CITY + 100);
				
				while(true) {
					Unidad unidad = unidadNueva.receive();
					
					if(getUNIDADES().isEmpty()){
						setBANDO(unidad.getBando());
						enviarAlCastillo.send(new Unidad(getBANDO()));
						enviarALaArena.send(unidad);
					}
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
