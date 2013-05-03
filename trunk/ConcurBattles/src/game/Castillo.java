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
	
	Channel<String> permisoEspecial = new Channel<String>(GeneradorDeCanal.generarPermisoEspecial());
	Channel<String> msj = new Channel<String>(GeneradorDeCanal.generarNumeroDeCanal());
	Channel<Unidad> enviarALaArena = new Channel<Unidad>(GeneradorDeCanal.generarOtroNumeroDeCanal());
	Channel<String> permiso = new Channel<String>(GeneradorDeCanal.generarPermiso());
	
	
	public Castillo(int bando, int id, List<Integer> destinos) {
		this.setDestinos(destinos);
		BANDO = bando;
		FLAG_CASTILLO = bando;
		ID_CITY = id;
		
		Channel<Unidad> unidadNueva = new Channel<Unidad>(FLAG_CASTILLO);
		unidadNueva.send(new Unidad(getBANDO()));
		
		// Espera que le avisen cuando crear una nueva unidad porque
		// conquistó una nueva ciudad o se murió una unidad de lvl mayor a 1
		new Thread(){
			public void run(){
				
				Set<Unidad> unidades = new HashSet<Unidad>();
				permisoEspecial.send("permiso");
				boolean elJuegoSigue = true;
				while(elJuegoSigue){
				
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
								elJuegoSigue = false;
							}else{
								unidades.add(unidad);
							}
						}else{
							if(getBANDO() != unidad.getBando()){
								elJuegoSigue = false;
							}else{
								unidades.add(unidad);
							}
						}
					}else{
						if(msj.equals("sacar")){
							unidades.remove(unidad);
						}
					}
				permiso.send("permiso");	
				}
			}
			
		}.start();
		
		while(true) {
			
			Unidad unidad = unidadNueva.receive();
			unidad.setCanalDePermiso(permiso);
			msj.send("agregar");
			enviarALaArena.send(unidad);
			permiso.receive();
			
		}
		
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
	
	public void setDestinos(List<Integer> destinos) {
		this.DESTINOS = destinos;
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
