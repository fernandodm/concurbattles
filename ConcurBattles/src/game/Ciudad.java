package game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


// import channel.Channel;
import ar.edu.unq.tpi.pconc.Channel;

public class Ciudad {
	
	private final int ID_CITY;
	private int BANDO;
	private final List<Integer> DESTINOS;
	
	Channel<String> permisoEspecial = new Channel<String>(GeneradorDeCanal.generarPermisoEspecial());
	Channel<String> msj = new Channel<String>(GeneradorDeCanal.generarNumeroDeCanal());
	Channel<Unidad> enviarALaArena = new Channel<Unidad>(GeneradorDeCanal.generarOtroNumeroDeCanal());
	Channel<String> permiso = new Channel<String>(GeneradorDeCanal.generarPermiso());
	Channel<Unidad> enviarAlCastillo = new Channel<Unidad>(getBANDO());
	
	List<CaminoDobleEntrada> caminos = new ArrayList<CaminoDobleEntrada>();
	
	public Ciudad(int id, List<Integer> destinos){
		ID_CITY = id;
		DESTINOS = destinos;
	
		final Channel<Unidad> unidadNueva = new Channel<Unidad>(ID_CITY);
		
		for(Integer nroCiudad : DESTINOS) {
			if(this.ID_CITY < nroCiudad) {
				caminos.add(new CaminoDobleEntrada(this.ID_CITY, nroCiudad));
			}
		}
		
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
						unidades.add(unidad);
							if(getBANDO() != unidad.getBando()){
								setBANDO(unidad.getBando());
								enviarAlCastillo.send(new Unidad(getBANDO()));
							}
						}else{
							unidades.add(unidad);
							setBANDO(unidad.getBando());
							enviarAlCastillo.send(new Unidad(getBANDO()));
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
				permisoEspecial.receive(); //recibe un permiso de la arena
				
				while(! Juego.gameOver()) {
					
					Unidad unidad = unidadNueva.receive();
					unidad.setCanalDePermiso(permiso);
					unidad.setMsj(msj);
					
					permiso.receive();
					
					msj.send("agregar");
					enviarALaArena.send(unidad);
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
	
	public List<Integer> getDESTINOS() {
		return DESTINOS;
	}

	public int getBANDO() {
		return BANDO;
	}
	public void setBANDO(int bANDO) {
		BANDO = bANDO;
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
