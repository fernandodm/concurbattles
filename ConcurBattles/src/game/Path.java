package game;

import java.util.ArrayList;
import java.util.List;

import channel.Channel;

public class Path extends Entidad {
	@Override
	protected void decidan(){
		 for (Unidad unidad : getUnidades()) {
				boolean decision = unidad.decidirViajar();
				if(decision){
					getUnidades().remove(unidad);
					List<ArrayList<Integer>> destino  = this.quitarCiudadAnterior(unidad.getCiudadAnterior());
					unidad.viajar(getId(), destino);
				}
				
				
			}
	}
	
	private List<ArrayList<Integer>> quitarCiudadAnterior(Integer ciudadAnterior){
		//List<ArrayList<Integer>> destinos = new ArrayList<ArrayList<Integer>>();
		//destinos.addAll(get)
		for (List<Integer> e : getCiudadesAdyacentes()) {
			ArrayList<Integer> entidad = new ArrayList<Integer>(e);
			if( !(entidad.get(1).equals(ciudadAnterior))){
				List<ArrayList<Integer>> destinos = new ArrayList<ArrayList<Integer>>();
				destinos.add(entidad);
				return destinos;
			}
		}
		return null;
	}
	public Path(List<ArrayList<Integer>> ciudadesA, int elId  ){
		this.setId(elId);
		this.setCiudadesAdyacentes(ciudadesA);
		final Channel<String> arenaControl = new Channel<String>(getId()+3100);
		final Channel<Unidad> arena = new Channel<Unidad>(getId()+2200);
		final Channel<Integer> permisoPuerta = new Channel<Integer>(getId()+2300);
		final Channel<Unidad> puerta = new Channel<Unidad>(getId()+2400);
		
		//thread puerta
		new Thread(){
			public void run(){
				permisoPuerta.send(1);
				while (true){
					Unidad unidad = puerta.receive();
					arenaControl.send("add");
					arena.send(unidad);
					permisoPuerta.send(1);
				}
			}
		}.start();
		
		//thread arena
				new Thread(){
					public void run(){
						
						while (true){
							String orden = arenaControl.receive();
							if(orden.equals("add")){
								Unidad unidad = arena.receive();
								
								//verificar si hay unidades de getBando() contrario
								if (hayUnidadesDeBandoContrarioA(unidad)){
									iniciarPelea(unidad);
								}
								if(unidad.isEstoyVivo()){
									getUnidades().add(unidad);
									
									
								}
							  decidan();		
							 //decidan las unidades que hacer, o se van o se quedan y se repite el ciclo
								
								
								
								
								
							}
							
						}
					}
				}.start();
	}
}
