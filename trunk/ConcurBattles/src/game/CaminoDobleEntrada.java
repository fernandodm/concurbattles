package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

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
		
		final Channel<Integer> permisoLado1 = new Channel<Integer>(idcity1+idcity2+1000);
		final Channel<Integer> permisoLado2 = new Channel<Integer>(idcity1+idcity2+2000);
		final Channel<Unidad> entradaLado1 = new Channel<Unidad>(idcity1+idcity2+3000);
		final Channel<Unidad> entradaLado2 = new Channel<Unidad>(idcity1+idcity2+4000);
		final Channel<Unidad> accessToCity1 = new Channel<Unidad>(ID_CITY1); // canales para enviar a ciudad , faltaria un canala de permiso de acceso?
		final Channel<Unidad> accessToCity2 = new Channel<Unidad>(ID_CITY2); // canales para enviar a ciudad
		final Channel<Unidad> arenaPath = new Channel<Unidad>(ID_PATH+500);
		final Channel<Integer> arenaPermiso = new Channel<Integer>(ID_PATH+600);
		
		System.out.println("Camino "+id+ " conectando ciudades " + idcity1 + " y " +idcity2  + " generado");
		
		new Thread() {
			public void run() {
				
			
				
				
				//Channel<Integer> unidadNueva = new Channel<Integer>();
				
				//Thread permisoLado1
				permisoLado1.send(1);
				while(true) {
					Unidad viajero =entradaLado1.receive();  
					viajerosLado1.add(viajero); // Unidad entro en camino
					
					System.out.println("Se agrego por lado 1nueva unidad " + viajero.getId() + " de bando "+ viajero.getBando()+ " \n esperando confirmacion de arena...");
					
					arenaPermiso.receive();
					System.out.println("Permiso obtenido en lado1");
					System.out.println("Enviando unidad "+ viajero.getId()+" de bando "+viajero.getBando()+" por arena por lado 1");
					try {
						Thread.currentThread().sleep(0);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
			Unidad viajero =entradaLado2.receive();  
			viajerosLado2.add(viajero); //unidad entro en camino
			
			System.out.println("Se agrego por lado2 nueva unidad " + viajero.getId() + " de bando "+ viajero.getBando()+ "esperando confirmacion de arena");
			
			arenaPermiso.receive();
			System.out.println("Permiso obtenido por lado 2");
			System.out.println("Enviando unidad "+ viajero.getId()+" de bando "+viajero.getBando()+" por arena por lado 2");
			try {
				Thread.currentThread().sleep(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
					
					if(viajero.isEstoyVivo()){
						
						if (hayContrincante()){
							System.out.println("Dos unidades se encontraron en el camino y van a pelear!");
							
							Unidad viajero1 =  viajerosLado1.pop();
							Unidad viajero2 = viajerosLado2.pop();
							viajero1.pelear(viajero2);
							//pelear(viajero1, viajero2);
							System.out.println("Unidad " +viajero1.getId() +" del bando "+ viajero1.getBando()+" peleando con \n  Unidac " +viajero2.getId() +" del bando "+ viajero2.getBando());
							if(viajero1.isEstoyVivo()){
								System.out.println("La unidad " +viajero1.getId() +" del bando "+ viajero1.getBando()+" ha Vencido!");
								System.out.println("La unidad " +viajero2.getId() +" del bando "+ viajero2.getBando()+" ha Muerto!");
								accessToCity2.send(viajero1);
								System.out.println("Unidad " +  viajero1.getId() + " de bando  "+viajero1.getBando()+ "enviada a ciudad " + ID_CITY2);
								
							}else{
								System.out.println("La unidad " +viajero2.getId() +" del bando "+ viajero2.getBando()+" ha ganado");
								System.out.println("La unidad " +viajero1.getId() +" del bando "+ viajero1.getBando()+" ha Muerto!");
								accessToCity1.send(viajero2);
								System.out.println("Unidad " +  viajero2.getId() + " de bando  "+viajero2.getBando()+ "enviada a ciudad " + ID_CITY1);
							}
							//
						}else{
							System.out.println("No se cruzó con enemigo");
							if(viajerosLado1.contains(viajero)){
								System.out.println("Unidad " +  viajero.getId() + " de bando  "+viajero.getBando()+ "enviada a ciudad " + ID_CITY2);
							}else{
								if(viajerosLado2.contains(viajero)){
									System.out.println("Unidad " +  viajero.getId() + " de bando  "+viajero.getBando()+ "enviada a ciudad " + ID_CITY1);
								}
							}
							quitarAViajero(viajero);
							//ENVIAR A CIUDAD EFECTIVAMENTE
							//System.out.println("Unidad " +  viajero.getId() + " de bando  "+viajero.getBando()+ "enviada a ciudad.");
						}
						
						
				        
					}else{
						System.out.println("Unidad " +  viajero.getId() + " de bando  "+viajero.getBando()+ " ya estaba muerta!!!");
						//quitarAViajero(viajero);
					}
						
					arenaPermiso.send(1);
				}
				
			}
		}.start();
		
		//thread construccion unidad bando 1 por lado 1
		new Thread(){
			public void run(){
				int idB1 = 1;
				while(true){
					try {
						Thread.currentThread().sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Unidad u1 = new Unidad(1);
					u1.setId(idB1);
					System.out.println("Nueva Unidad numero "+ u1.getId() +" del bando " + u1.getBando() );
					permisoLado1.receive(); // Cuando la unidad quiera viajar, solicitara el permiso del camino.
					entradaLado1.send(u1);
					
					idB1++;
				}
			}
			
		};//.start();
		
		
		//thread construccion unidad bando 2 por el lado 2
				new Thread(){
					public void run(){
						int idB2 = 1;
						while(true){
							try {
								Thread.currentThread().sleep(1500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Unidad u2 = new Unidad(2);
							u2.setId(idB2);
							System.out.println("Nueva Unidad numero "+ u2.getId() +" del bando " + u2.getBando() );
							permisoLado2.receive();
							entradaLado2.send(u2);
							idB2++;
						}
					}
					
				};//.start();
				
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
			return this.viajerosLado1.peekFirst().getBando() != this.viajerosLado2.peekFirst().getBando();
			
		}else{
			return false;
		}
	}
	
	public boolean lado1Vacio(){
		return this.viajerosLado1.isEmpty();
	}
	
	public boolean lado2Vacio(){
		return this.viajerosLado2.isEmpty();
	}
	
	public void quitarAViajero(Unidad u){
		
		if(this.lado1Vacio() && !(this.lado2Vacio()) ){
			this.viajerosLado2.removeFirst();
			System.out.println(" unidad "+u.getId()+" entrando por lado 2  del bando " + u.getBando()+ " ha pasado por el camino");
		}
		else{
			
			if(this.lado2Vacio() && !(this.lado1Vacio())){
				this.viajerosLado1.removeFirst();
				System.out.println(" unidad" + u.getId()+ "entrando por lado 1   del bando " + u.getBando()+ " ha pasado por el camino");
				
			
		     }else{
		    	 
		    	 if( !this.lado1Vacio()  !=  !this.lado2Vacio()){
		    		 if(this.viajerosLado1.getFirst().getId() == u.getId()){
							this.viajerosLado1.removeFirst();
							System.out.println(" unidad" + u.getId()+ "entrando por lado 1   del bando " + u.getBando()+ " ha pasado por el camino");
						}else{
							if(this.viajerosLado2.getFirst().getId() == u.getId() ){
								this.viajerosLado2.removeFirst();
								System.out.println(" unidad" + u.getId()+ "entrando por lado 2   del bando " + u.getBando()+ " ha pasado por el camino");
							}
					
						}
		    	 }
					
			}
		}
				
			
			
	}			
	
	
	public void pelear(Unidad u1, Unidad u2){
		u1.pelear(u2);
	}
	/*
	public static void main(String[] args) {
	
		
		
		
		
			
			
		
		CaminoDobleEntrada p = new CaminoDobleEntrada(1, 1, 3);
	}*/
}
