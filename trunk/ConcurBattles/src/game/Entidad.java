package game;

import java.util.ArrayList;

import channel.Channel;

public class Entidad {
	private static int bando = 0;
    private static int id;
	private static ArrayList<Integer> ciudadesAdyacentes = new ArrayList<Integer>();
	private static ArrayList<Unidad> unidades = new ArrayList<Unidad>();
	
	private static boolean hayUnidadesDeBandoContrarioA(Unidad unaUnidad){
		for (Unidad unidad : unidades) {
			if(! (unidad.getBando() == unaUnidad.getBando())){
				return true;
			}
		}
		return false;
	}
	
	private static void iniciarPelea(Unidad unaUnidad){
		for (Unidad enemigo : unidades) {
			
			
			unaUnidad.pelear(enemigo);
			
			if(enemigo.isEstoyVivo()){
			   break;
			}else{
			  unidades.remove(enemigo);
			}
			
			
			
		}
	}
	
	
	private static void decidan(){
       for (Unidad unidad : unidades) {
			boolean decision = unidad.decidirViajar();
			if(decision){
				unidades.remove(unidad);
				unidad.viajar(id, ciudadesAdyacentes);
			}
			
			
		}
	}
	public static void main(String[] args) {
		
		final Channel<String> arenaControl = new Channel<String>(id+96);
		final Channel<Unidad> arena = new Channel<Unidad>(id+97);
		final Channel<Integer> permisoPuerta = new Channel<Integer>(id+98);
		final Channel<Unidad> puerta = new Channel<Unidad>(id+99);
		
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
								
								//verificar si hay unidades de bando contrario
								if (hayUnidadesDeBandoContrarioA(unidad)){
									iniciarPelea(unidad);
								}
								if(unidad.isEstoyVivo()){
									unidades.add(unidad);
									//verificar bando, si es distinto, conquisto
									if(bando!= unidad.getBando()){
										bando = unidad.getBando();
										//crear unidad en castillo de dicho bando
										
										// avisar a castillo de bando, que cree otra unidad.
										(new Channel<Unidad>(bando)).send(new Unidad(id));
										
									}
								}
							  decidan();		
							 //decidan las unidades que hacer, o se van o se quedan y se repite el ciclo
								
								
								
								
								
							}
							
						}
					}
				}.start();
		
	}
}
