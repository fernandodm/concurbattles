package game;

import java.util.LinkedList;

import channel.Channel;

public class Path {
private final int ID_PATH;
	
	private final int ID_CITY1;
	private final int ID_CITY2;
	private final LinkedList<Unidad> viajeros = new LinkedList<Unidad>();
	/*
	 * 
	 *
	 */
	
	public Path(int id, int idcity1, int idcity2){
		
		ID_PATH = id;
		ID_CITY1 = idcity1;
		ID_CITY2 = idcity2;
		
		final Channel<Integer> permiso = new Channel<Integer>(ID_PATH+100);
		final Channel<Unidad> accessToPath = new Channel<Unidad>(ID_PATH+200);
		final Channel<Integer> accessToCity1 = new Channel<Integer>(ID_PATH+1000); // canales para enviar a ciudad , faltaria un canala de permiso de acceso?
		final Channel<Integer> accessToCity2 = new Channel<Integer>(ID_PATH+2000); // canales para enviar a ciudad
		final Channel<Integer> arenaPath = new Channel<Integer>(ID_PATH+3000);
		final Channel<Integer> arenaPermiso = new Channel<Integer>(ID_PATH+4000);
		
		new Thread() {
			public void run() {
				
			
				
				
				//Channel<Integer> unidadNueva = new Channel<Integer>();
				
				//Thread permiso
				permiso.send(1);
				while(true) {
					Unidad viajero =accessToPath.receive();  
					viajeros.add(viajero);
					
					System.out.println("Se agrego nueva unidad " + viajero.getId() + " de bando "+ viajero.getBando()+ "esperando confirmacion de arena");
					
					arenaPermiso.receive();
					System.out.println("Permiso obtenido");
					System.out.println("Enviando unidad por arena");
					arenaPath.send(1);
				
					permiso.send(1);
				}
				
			}
		}.start();
		
		
		//thread arena
		new Thread(){
			public void run(){
				
				
				arenaPermiso.send(1);
				while(true){
					
						
						arenaPath.receive(); // canal idPath  id channel?
						
						System.out.println(" Viajero entro en arena." );
							
							Unidad viajero = viajeros.pollLast();
							
							System.out.println("Unidad " +  viajero.getId() + " de bando  "+viajero.getBando()+ "enviada a ciudad.");
				        arenaPermiso.send(1);
						
						
					
				}
				
			}
		}.start();
		
		//thread construccion unidad bando 1
		new Thread(){
			public void run(){
				int idB1 = 1;
				while(true){
					Unidad u1 = new Unidad(1);
					u1.setId(idB1);
					System.out.println("Nueva Unidad numero "+ u1.getId() +" del bando " + u1.getBando() );
					permiso.receive(); // Cuando la unidad quiera viajar, solicitara el permiso del camino.
					accessToPath.send(u1);
					idB1++;
				}
			}
			
		}.start();
		
		
		//thread construccion unidad bando 1
				new Thread(){
					public void run(){
						int idB2 = 1;
						while(true){
							Unidad u2 = new Unidad(2);
							u2.setId(idB2);
							System.out.println("Nueva Unidad numero "+ u2.getId() +" del bando " + u2.getBando() );
							permiso.receive();
							accessToPath.send(u2);
							idB2++;
						}
					}
					
				}.start();
				
		//thread de una unidad que viaja todo el tiempo
				new Thread(){
					public void run(){
						Unidad u3 = new Unidad(3);
						u3.setId(1);
						while(true){
							
							
							permiso.receive();
							accessToPath.send(u3);
							System.out.println(" Unidad numero "+ u3.getId() +" del bando " + u3.getBando() + "viajando");
							
						}
					}
					
				}.start();	
				
	}
	public static void main(String[] args) {
		Path p = new Path(1, 2, 3);
	}
}
