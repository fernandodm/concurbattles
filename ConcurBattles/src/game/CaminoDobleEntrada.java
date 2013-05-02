package game;

import java.util.LinkedList;

import channel.Channel;

public class CaminoDobleEntrada {
private final int ID_PATH;
	
	private final int ID_CITY1;
	private final int ID_CITY2;
	private final LinkedList<Unidad> viajerosLado1 = new LinkedList<Unidad>();
	private final LinkedList<Unidad> viajerosLado2 = new LinkedList<Unidad>();
	//private final Unidad viajeroLado1 = null;
	//private final Unidad viajeroLado2 = null;
	/*
	 * 
	 *
	 */
	
	
	public CaminoDobleEntrada(int id, int idcity1, int idcity2){
		
		ID_PATH = id;
		ID_CITY1 = idcity1;
		ID_CITY2 = idcity2;
		
		final Channel<Integer> permisoLado1 = new Channel<Integer>(ID_PATH+100);
		final Channel<Integer> permisoLado2 = new Channel<Integer>(ID_PATH+200);
		final Channel<Unidad> entradaLado1 = new Channel<Unidad>(ID_PATH+300);
		final Channel<Unidad> entradaLado2 = new Channel<Unidad>(ID_PATH+300);
		final Channel<Integer> accessToCity1 = new Channel<Integer>(ID_PATH+1000); // canales para enviar a ciudad , faltaria un canala de permiso de acceso?
		final Channel<Integer> accessToCity2 = new Channel<Integer>(ID_PATH+2000); // canales para enviar a ciudad
		final Channel<Unidad> arenaPath = new Channel<Unidad>(ID_PATH+3000);
		final Channel<Integer> arenaPermiso = new Channel<Integer>(ID_PATH+4000);
		
		
		
		new Thread() {
			public void run() {
				
			
				
				
				//Channel<Integer> unidadNueva = new Channel<Integer>();
				
				//Thread permisoLado1
				permisoLado1.send(1);
				while(true) {
					Unidad viajero =entradaLado1.receive();  
					viajerosLado1.add(viajero); // Unidad entro en camino
					
					System.out.println("Se agrego nueva unidad " + viajero.getId() + " de bando "+ viajero.getBando()+ " \n esperando confirmacion de arena...");
					
					arenaPermiso.receive();
					System.out.println("Permiso obtenido");
					System.out.println("Enviando unidad por arena");
					arenaPath.send(viajero);
				
					permisoLado1.send(1);
				}
				
			}
		}.start();
		
		
		//Thread permisoLado2
    new Thread(){ 
    	public void run(){
		permisoLado2.send(1);
		while(true) {
			Unidad viajero =entradaLado1.receive();  
			viajerosLado2.add(viajero); //unidad entro en camino
			
			System.out.println("Se agrego nueva unidad " + viajero.getId() + " de bando "+ viajero.getBando()+ "esperando confirmacion de arena");
			
			arenaPermiso.receive();
			System.out.println("Permiso obtenido");
			System.out.println("Enviando unidad por arena");
			arenaPath.send(viajero);
		
			permisoLado2.send(1);
		}
		
	    }
     }.start();

		
		//thread arena
		new Thread(){
			public void run(){
				
				
				arenaPermiso.send(1);
				while(true){
					
						
						Unidad viajero = arenaPath.receive(); // signal para activar arena
						
						System.out.println(" Arena analizando..." );
							
						if (hayContrincante()){
							System.out.println("Dos unidades se encontraron en el camino y van a pelear!");
							pelear();
							viajerosLado1.pop();
							viajerosLado2.pop();
						}else{
							
						}
							
							System.out.println("Unidad " +  viajero.getId() + " de bando  "+viajero.getBando()+ "enviada a ciudad.");
				        arenaPermiso.send(1);
						
						
					
				}
				
			}
		}.start();
		
		//thread construccion unidad bando 1 por lado 1
		new Thread(){
			public void run(){
				int idB1 = 1;
				while(true){
					Unidad u1 = new Unidad(1);
					u1.setId(idB1);
					System.out.println("Nueva Unidad numero "+ u1.getId() +" del bando " + u1.getBando() );
					permisoLado1.receive(); // Cuando la unidad quiera viajar, solicitara el permiso del camino.
					entradaLado1.send(u1);
					
					idB1++;
				}
			}
			
		}.start();
		
		
		//thread construccion unidad bando 2 por el lado 2
				new Thread(){
					public void run(){
						int idB2 = 1;
						while(true){
							Unidad u2 = new Unidad(2);
							u2.setId(idB2);
							System.out.println("Nueva Unidad numero "+ u2.getId() +" del bando " + u2.getBando() );
							permisoLado2.receive();
							entradaLado2.send(u2);
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
							
							
							permisoLado1.receive();
							entradaLado1.send(u3);
							System.out.println(" Unidad numero "+ u3.getId() +" del bando " + u3.getBando() + "viajando");
							
						}
					}
					
				};	
				
	}
	
	public boolean hayContrincante(){
		if(  !(this.viajerosLado1.isEmpty()) &&  !(this.viajerosLado2.isEmpty() )) {
			return this.viajerosLado1.get(0).getBando() != this.viajerosLado2.get(0).getBando();
			
		}else{
			return false;
		}
	}
	
	public void pelear(){
		this.viajerosLado1.get(0).pelear(this.viajerosLado2.get(0));
	}
	
	public static void main(String[] args) {
		CaminoDobleEntrada p = new CaminoDobleEntrada(1, 2, 3);
	}
}
