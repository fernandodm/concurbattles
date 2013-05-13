package game;

import java.util.ArrayList;

import channel.Channel;

public class Entidad {
	private static int bando = 0;
    private static int id;
	private static ArrayList<Integer> ciudadesAdyacentes = new ArrayList<Integer>();
	private static ArrayList<ArrayList<Integer>> unidades = new ArrayList<ArrayList<Integer>>();
	
	private static boolean hayUnidadesDeBandoContrarioA(int bando){
		for (ArrayList<Integer> unidad : unidades) {
			if(! unidad.get(0).equals(bando)){
				return true;
			}
		}
		return false;
	}
	
	private static void iniciarPelea(ArrayList<Integer> unidad){
		for (ArrayList<Integer> enemigo : unidades) {
			
			
			unidad.pelear(enemigo);
			
			if(enemigo.estoyVivo){
			   break;
			}else{
			  unidades.remove(enemigo);
			}
			
			
			
		}
	}
	
	
	private static void decidan(){
       for (ArrayList<Integer> unidad : unidades) {
			boolean decision = unidad.decidirViajar();
			if(decision){
				unidades.remove(unidad);
				unidad.viajar(id, ciudadesAdyacentes);
			}
			
			
		}
	}
	public static void main(String[] args) {
		
		final Channel<String> arenaControl = new Channel<String>(id+96);
		final Channel<ArrayList<Integer>> arena = new Channel<ArrayList<Integer>>(id+97);
		final Channel<Integer> permisoPuerta = new Channel<Integer>(id+98);
		final Channel<ArrayList<Integer>> puerta = new Channel<ArrayList<Integer>>(id+99);
		
		//thread puerta
		new Thread(){
			public void run(){
				permisoPuerta.send(1);
				while (true){
					ArrayList<Integer> unidad = puerta.receive();
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
								ArrayList<Integer> unidad = arena.receive();
								
								//verificar si hay unidades de bando contrario
								if (hayUnidadesDeBandoContrarioA(unidad.get(0))){
									iniciarPelea(unidad);
								}
								if(unidad.estoyVivo){
									unidades.add(unidad);
									//verificar bando, si es distinto, conquisto
									if(id!= unidad.bando){
										id = unidad.bando;
										//crear unidad en castillo de dicho bando
										
										// avisar a castillo de bando, que cree otra unidad.
										//(new Channel<Unidad>(id)).send(new Unidad(id))
										
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
