package game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import channel.Channel;
import ar.edu.unq.tpi.pconc.Channel;

public class Castillo {
	
	private final int ID_CITY;
	private final int FLAG_CASTILLO;
	private final int BANDO;
	private List<Integer> DESTINOS;
	
	Channel<String> permisoEspecial = new Channel<String>(GeneradorDeCanal.generarPermisoEspecial());
	Channel<String> msj = new Channel<String>(GeneradorDeCanal.generarNumeroDeCanal());
	Channel<Unidad> enviarALaArena = new Channel<Unidad>(GeneradorDeCanal.generarOtroNumeroDeCanal());
	Channel<String> permiso = new Channel<String>(GeneradorDeCanal.generarPermiso());
	
	List<CaminoDobleEntrada> caminos = new ArrayList<CaminoDobleEntrada>();
	
	public Castillo(int bando, int id, List<Integer> destinos) {
		this.setDESTINOS(destinos);
		BANDO = bando;
		FLAG_CASTILLO = bando;
		ID_CITY = id;
		
		for(Integer nroCiudad : DESTINOS) {
			if(this.ID_CITY < nroCiudad) {
				caminos.add(new CaminoDobleEntrada(this.ID_CITY, nroCiudad));
			}
		}
		
		final Channel<Unidad> unidadNueva = new Channel<Unidad>(FLAG_CASTILLO);
		unidadNueva.send(new Unidad(getBANDO()));
		
		// Espera que le avisen cuando crear una nueva unidad porque
		// conquistó una nueva ciudad o se murió una unidad de lvl mayor a 1
		new Thread(){
			public void run(){
				
				Set<Unidad> unidades = new HashSet<Unidad>();
				permisoEspecial.send("permiso");
				permiso.send("permiso");

				while(! Juego.gameOver()){
				
					Unidad unidad = enviarALaArena.receive();
					if(msj.equals("agregar")){
						
						if(!unidades.isEmpty()){
							
							while(hayUnidadContrariaDe(unidad.getBando(), unidades)){
								
								for(Unidad each : unidades){
									
									if(each.getBando() != unidad.getBando()){
										unidad.pelear(each);
										
										if(!unidad.isEstoyVivo()){
											
											unidad = each;
																								
										}else{
											unidades.remove(each);
										}
									}
								}
							}
							if(getBANDO() != unidad.getBando()){
								Juego.setGameOver(true);
							}else{
								unidades.add(unidad);
							}
						}else{
							if(getBANDO() != unidad.getBando()){
								Juego.setGameOver(true);
							}else{
								unidades.add(unidad);
							}
						}
					}else{
						if(msj.equals("sacar")){
							unidades.remove(unidad);
							unidad.setCanalDePermiso(null);
							unidad.viajar(getID_CITY(), getDESTINOS());
						}
					}
				permiso.send("permiso");	
				}
			}
			
		}.start();
		
		new Thread() {
			public void run() {
				
				while(! Juego.gameOver()) {
					
					Unidad unidad = unidadNueva.receive();
					
					Channel<String> notificacionUI = new Channel<String>(Juego.inputChannel);
					notificacionUI.send(unidad.getId() +" "+ getID_CITY());
					
					unidad.setCanalDePermiso(permiso);
					unidad.setMsj(msj);
					
					permiso.receive();
					
					enviarALaArena.send(unidad);
					msj.send("agregar");
				}				
			}
		}.start();
	}
	
	public boolean hayUnidadContrariaDe(int unBando, Set<Unidad> lista){
		
		boolean hayUnidad = false;
		for (Unidad each : lista) {
			if(each.getBando() == unBando){
				hayUnidad = true;
				return hayUnidad;
			}
		}		
		return hayUnidad;
	}
	
	public int getBANDO() {
		return BANDO;
	}
	
	public int getID_CITY() {
		return ID_CITY;
	}
	
	public void setDESTINOS(List<Integer> destinos) {
		this.DESTINOS = destinos;
	}
	
	public List<Integer> getDESTINOS() {
		return this.DESTINOS;
	}
	
	public static void main(String[] args) {
		List<Integer> destinosC1 = new ArrayList<Integer>(1);
		destinosC1.add(2);
		Castillo castillo = new Castillo(1, 1, destinosC1);
		
		List<Integer> destinosC2 = new ArrayList<Integer>(1);
		destinosC2.add(1);
		new Castillo(2, 2, destinosC2);
		
		Channel<Unidad> unidadNueva = new Channel<Unidad>(castillo.FLAG_CASTILLO);
		
		unidadNueva.send(new Unidad(castillo.getBANDO()));
		unidadNueva.send(new Unidad(castillo.getBANDO()));
	}

}
