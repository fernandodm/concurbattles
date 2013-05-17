package game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import channel.Channel;

public class Castillo {
	
	private final int ID_CITY;
	private final int FLAG_CASTILLO;
	private final int BANDO;
	private List<Integer> DESTINOS;
	
	Integer nroMsjs = GeneradorDeCanal.generarNumeroDeCanal();
	Integer nroPermiso = GeneradorDeCanal.generarNumeroDeCanal();
	
	//private final Channel<String> permisoEspecial = new Channel<String>(GeneradorDeCanal.generarNumeroDeCanal());
	private final Channel<String> msj = new Channel<String>(nroMsjs);
	private final Channel<Unidad> enviarALaArena = new Channel<Unidad>(GeneradorDeCanal.generarNumeroDeCanal());
	private final Channel<String> permiso = new Channel<String>(nroPermiso);
	
	List<CaminoDobleEntrada> caminos = new ArrayList<CaminoDobleEntrada>();
	
	private final Channel<Unidad> unidadNueva;
	
	public Castillo(int bando, int id, List<Integer> destinos) {
		this.setDESTINOS(destinos);
		BANDO = bando;
		FLAG_CASTILLO = bando;
		ID_CITY = id;
		
		unidadNueva = new Channel<Unidad>(FLAG_CASTILLO);
		
		for(Integer nroCiudad : DESTINOS) {
			if(this.ID_CITY < nroCiudad) {
				final int nroCiu = nroCiudad;
				
				new Thread() {
					public void run() {
						new CaminoDobleEntrada(ID_CITY, nroCiu);
					}
				}.start();
			}
		}
		
		unidadNueva.send(new Unidad(getBANDO()));
		
		// Espera que le avisen cuando crear una nueva unidad porque
		// conquistó una nueva ciudad o se murió una unidad de lvl mayor a 1
		new Thread(){
			public void run(){
				
				Set<Unidad> unidades = new HashSet<Unidad>();
				//permisoEspecial.send("permiso");
				permiso.send("permiso");

				while(! Juego.gameOver()){
				
					Unidad unidad = enviarALaArena.receive();
					String mensaje = msj.receive();
					
					if(mensaje.equals("agregar")){
						
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
							
						// Si no hay defensa, entro
						}else{
							if(getBANDO() != unidad.getBando()){
								Juego.setGameOver(true);
							}else{
								// Si el lugar me pertenece, entro
								unidades.add(unidad);
							}
						}
					} else if(mensaje.equals("sacar")) {
							//unidades.remove(unidad);
							//unidad.setCanalDePermiso(null);
							//unidad.viajar(getID_CITY(), getDESTINOS());
							if(! unidades.isEmpty()) {
								Unidad unit = null;
								
								for(Unidad u : unidades) {
									if(unit != null) {
										break;
									}	
									unit = u;
								}
								
								//unit.viajar(getID_CITY(), getDESTINOS());
								unidades.remove(unit);
							}
					}
				
				permiso.send("permiso");	
				}
			}
			
		}.start();
		
		new Thread() {
			public void run() {
				
				Channel<String> notificacionUI = new Channel<String>(Juego.inputChannel);
				
				while(! Juego.gameOver()) {
					
					Unidad unidad = unidadNueva.receive();	
					
					notificacionUI.send(unidad.getId() +" "+ getID_CITY());
					System.out.println(nroPermiso);
					//////////////////////////////////////////////////
					/**
					new Channel<Integer>(unidad.getCanalDePermiso()).send(nroDePermiso);
					new Channel<Integer>(unidad.miCanal).send(nroMsjs);
					*/
					/////////////////////////////////////////////////
					unidad.setCanalDePermiso(nroPermiso);
					unidad.setMsj(nroMsjs);
					
					permiso.receive();
					
					System.out.println("Enviar a la arena del castillo");
					
					enviarALaArena.send(unidad);
					msj.send("agregar");
				}				
			}
		}.start();
		
		new Thread() {
			public void run() {
				
				while(! Juego.gameOver()) {
					
					permiso.receive();
					
					System.out.println("Sacar al primero de la arena para viajar");
					
					msj.send("sacar");
					
					try {
						Thread.sleep(0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
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
