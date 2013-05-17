package game;

import java.util.ArrayList;
import java.util.List;

import channel.Channel;

public class Castle extends Entidad {
	public Castle(List<ArrayList<Integer>> ciudadesA, int elId, int bando  ){
		this.setBando(bando);
		this.setIdEntidad(elId);
		this.setCiudadesAdyacentes(ciudadesA);
		final Channel<String> arenaControl = new Channel<String>(getIdEntidad()+1100);
		final Channel<Unidad> arena = new Channel<Unidad>(getIdEntidad()+1200);
		final Channel<Integer> permisoPuerta = new Channel<Integer>(getIdEntidad()+1300);
		final Channel<Unidad> puerta = new Channel<Unidad>(getIdEntidad()+1400);
		final Channel<Unidad> crearUnidad = new Channel<Unidad>(this.getBando());
		
		//threadCreacionUnidad
		new Thread(){
			public void run(){
				System.out.println("creando primera unidad ");
				crearUnidad.send(new Unidad(getBando()));
				while(! Game.gameOver()){
					Unidad unidadNueva = crearUnidad.receive();
					System.out.println("Entrando unidad a castillo " + getBando());
					permisoPuerta.receive();
					puerta.send(unidadNueva);
				}
			}
		}.start();
		
		//thread puerta
		new Thread(){
			public void run(){
				permisoPuerta.send(1);
				while (! Game.gameOver()){
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
						
						while (! Game.gameOver()){
							String orden = arenaControl.receive();
							if(orden.equals("add")){
								Unidad unidad = arena.receive();
								
								//verificar si hay unidades de getBando() contrario
								if (hayUnidadesDeBandoContrarioA(unidad)){
									iniciarPelea(unidad);
								}
								if(unidad.isEstoyVivo()){
									getUnidades().add(unidad);
									//verificar getBando(), si es distinto, conquisto
									if(getBando()!= unidad.getBando()){
										setBando(unidad.getBando());
										System.out.println(" El castillo" + getIdEntidad()+ " ha sido conquistada por la unidad "+ unidad.getId()+" del bando " + unidad.getBando() + " !!");
										//crear unidad en castillo de dicho getBando()
										Integer myId =  (int) getId();
										// avisar a castillo de getBando(), que cree otra unidad.
										(new Channel<Unidad>(getBando())).send(new Unidad(unidad.getBando()));
										Game.setGameOver(true);
										
										System.out.println("GANO EL BANDO "+ ((getBando() == 1) ? "GOLD" : "SILVER"));
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
