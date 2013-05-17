package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Integer.parseInt;

import ar.edu.unq.tpi.concurbattles.ConcurBattles;
import channel.Channel;

public class Game{
	public static final Integer inputChannel = 9990;
	private static final Integer outputChannel = 9991;
	private static boolean gameOver = false;
	private static List<City> cities = new ArrayList<City>();
	
	public static void main(String[] args) {
		
		//final Channel<String> c1 = new Channel<String>(inputChannel);
		
		final Channel<String> c2 = new Channel<String>(outputChannel);
		
		new Thread() {
			public void run() {
				while (true) {
					String[] mapa = c2.receive().split("\n");
					System.out.println(mapa);
					final List<List<Integer>> ciudades = new LinkedList<List<Integer>>();
					//Transformo lo que genera el mapa en linked de linked
					LinkedList<LinkedList<Integer>> ciudadesMapeadas = new LinkedList<LinkedList<Integer>>();
					for(String camino : mapa) {
						// Separarlo por espacios y sacar el primer espacio en blanco
						List<String> caminos = new ArrayList<String>(Arrays.asList(camino.split(" ")));
						
						caminos.remove(0); //saco la coma chota
						LinkedList<Integer> ciudad= new LinkedList<Integer>();
						for (String s : caminos) {
							int n = Integer.parseInt(s); //parse a int
							ciudad.add(n); 
						}
						ciudadesMapeadas.add(ciudad); //agrego un mapeo de ciudad
						System.out.println(" Esto da la transformacion final de las ciudades mapeadas " + ciudadesMapeadas);
					}
					
					int idCastillo2 = ciudadesMapeadas.size();
					
					int idPath = 1; //contador
					LinkedList<Integer>castillo1 = ciudadesMapeadas.pollFirst();//obtengo y quito el castillo1
					LinkedList<Integer>castillo2 = ciudadesMapeadas.pollLast(); //obtengo y quito el castillo2
					
					//Los castillos los creo a lo ultimo
					/*
					 * Objetivo, por cada ciudad mapeada, debo crear los caminos adyacentes
					 * guardar los caminos y las ciudades a las que apunta
					 * y luego generar la ciudad conj la list de los caminosadyacentes
					 * 
					 */
					for(LinkedList<Integer> ciudadMapeo : ciudadesMapeadas) {
						
						
					int idNewCity = ciudadMapeo.peekFirst(); //Identifico el id de la nueva ciudad
					
					List<Integer> idsCiudadesVecinas = new ArrayList<Integer>(ciudadMapeo); // Identifico los ids de las ciudades vecinas
					System.out.println("generando ciudad " + idNewCity );
					List<ArrayList<Integer>>caminosAdyacentes = new ArrayList<ArrayList<Integer>>(); //Genero la lista de listas que representa en ese caso cada camino adyacente ls correspondiente ciudad a la que apunta
					
					//Comienzo a crear cada camino
					for (int idCiudadvecina : idsCiudadesVecinas) {
						if(idCiudadvecina>idNewCity || idCiudadvecina<2){
							System.out.println("comenzando a generar Path");
							List<ArrayList<Integer>> adyacentesAlCamino = new ArrayList<ArrayList<Integer>>(); // genero las ciudades adyacentes al camino
							
							
							// Genero la primera ciudad adyacente al camino,  representa la ciudad que estoy generando
							ArrayList<Integer> laEntidadCiudadNueva = new ArrayList<Integer>();
							laEntidadCiudadNueva.add(2); // 2 representa una ciudad
							laEntidadCiudadNueva.add(idNewCity); //
							
							// Genero la otra ciudad adyacente
							ArrayList<Integer> laOtraCiudadAdyacente = new ArrayList<Integer>();
							
							//considerarCasoConexionACastillo
							if(idCiudadvecina == 1 || idCiudadvecina == idCastillo2){
								laOtraCiudadAdyacente.add(1); // el 1 representa un castillo
								System.out.println("Ciudad "+idNewCity+" conectada con castillo !");
							}else{
								laOtraCiudadAdyacente.add(2);
							}
							
							laOtraCiudadAdyacente.add(idCiudadvecina);
							
							
							//Las agrego efectivamente
							adyacentesAlCamino.add(laEntidadCiudadNueva);
							adyacentesAlCamino.add(laOtraCiudadAdyacente);
							
							//Genero el camino
							new Path(adyacentesAlCamino, idPath);
							System.out.println(" Nuevo camino " +idPath + "Conectando ciudad " +idNewCity + " con " + idCiudadvecina + "con adyacentes " + adyacentesAlCamino);
							//genero la representacion del camino que sera una de las entidades adyacentes de la nueva ciudad
							ArrayList<Integer> caminoAdyacente= new ArrayList<Integer>();
							caminoAdyacente.add(3); // el "3" representa un camino
							caminoAdyacente.add(idPath); // el id del path que estamos creando
							caminoAdyacente.add(idCiudadvecina); // IMPORTANTE, aquie agrego el id de la ciudad a la que el camino con el id dado Llega,
	 						
							caminosAdyacentes.add(caminoAdyacente);
							System.out.println("Agregado camino adyacente"+caminoAdyacente);
							idPath++;
						}else{
							//Seteo la variable en 0 del id del path que busco, los paths siempre empeizan desde 1, un valor distinto de cero implica que se encontro el valor
							System.out.println("Camino ya existente, tomando id delcamino de ciudad ya creada");
							int idDelPathQueNecesito = 0;
							//recorro las ciudades
							for (City c : cities) {
								//recorro las entidades adyacentes de esta ciudad, que representan los caminos
								//Yo necesito obtener a la ciudad vecina
								if(idCiudadvecina == (c.getIdEntidad())){
									System.out.println("Entida con el path repetido encontrada");
									for (ArrayList<Integer> entidad : c.getCiudadesAdyacentes()) {
										//si la posicion 2 de dicha entidad, coincido con el id dela ciudad vecina, encontre el path y tomo el valor del id en la posicion1
										if(entidad.get(2) == idNewCity){
											
											idDelPathQueNecesito = entidad.get(1);
											System.out.println("Se encontro el id del path: " + idDelPathQueNecesito);
											//No creo ningun path puesto que ya existe, solo le asigno el camino correspondiente como adyacente
											ArrayList<Integer> caminoAdyacente= new ArrayList<Integer>();
											caminoAdyacente.add(3); // el "3" representa un camino
											caminoAdyacente.add(idDelPathQueNecesito); // el id del path que estamos creando
											caminoAdyacente.add(idCiudadvecina); // IMPORTANTE, aquie agrego el id de la ciudad a la que el camino con el id dado Llega,
					 						
											caminosAdyacentes.add(caminoAdyacente);
											break;
										}
									}
								}
								
								if (idDelPathQueNecesito != 0){
									break;
								}
							}
							
							
							
						}
						
					}
					
					//Finalmente, genero la ciudad
					System.out.println("La nueva ciudad tiene como entidades adyacentes "+ caminosAdyacentes);
					cities.add(new City(caminosAdyacentes, idNewCity));
					System.out.println("Ciudad " + idNewCity + " creada");	
					
					
					}//aqui termia la creacion de las ciudades
					
					
					///
					/////
					///
					//
					//
					//Ahora debo crear los castillos
					//
					//
                    int idNewCastle = castillo1.peekFirst(); //Identifico el id de la nueva ciudad
					
					List<Integer> idsCiudadesVecinas = new ArrayList<Integer>(castillo1); // Identifico los ids de las ciudades vecinas
					System.out.println("generando castillo " + idNewCastle );
					List<ArrayList<Integer>>caminosAdyacentesCastillo1 = new ArrayList<ArrayList<Integer>>();
					for (Integer  ciudadVecina: idsCiudadesVecinas) {
						int idDelPathQueNecesito = 0;
						//recorro las ciudades
						
						//caso border, conexion castillo - castillo, crear path nuevo, utilizar id path
						if(ciudadVecina== idCastillo2){
							List<ArrayList<Integer>> adyacentesAlCamino = new ArrayList<ArrayList<Integer>>();
							// Genero la primera ciudad adyacente al camino,  representa la ciudad que estoy generando
							ArrayList<Integer> laEntidadCiudadNueva = new ArrayList<Integer>();
							laEntidadCiudadNueva.add(2); // 2 representa una ciudad
							laEntidadCiudadNueva.add(idNewCastle); //
							
							// Genero la otra ciudad adyacente
							ArrayList<Integer> laOtraCiudadAdyacente = new ArrayList<Integer>();
							
							
							laOtraCiudadAdyacente.add(1);
							laOtraCiudadAdyacente.add(ciudadVecina);
							
							
							
							//Las agrego efectivamente
							adyacentesAlCamino.add(laEntidadCiudadNueva);
							adyacentesAlCamino.add(laOtraCiudadAdyacente);
							
							//Genero el camino
							new Path(adyacentesAlCamino, idPath);
							System.out.println(" Nuevo camino " +idPath + "Conectando castillo " +idNewCastle + " con " + ciudadVecina);
							//genero la representacion del camino que sera una de las entidades adyacentes de la nueva ciudad
							ArrayList<Integer> caminoAdyacente= new ArrayList<Integer>();
							caminoAdyacente.add(3); // el "3" representa un camino
							caminoAdyacente.add(idPath); // el id del path que estamos creando
							caminoAdyacente.add(ciudadVecina); // IMPORTANTE, aquie agrego el id de la ciudad a la que el camino con el id dado Llega,
	 						
							caminosAdyacentesCastillo1.add(caminoAdyacente);
							
						}else{
							for (City ciudad : cities) {
								//recorro las entidades adyacentes de esta ciudad, que representan los caminos
								//Yo necesito obtener a la ciudad vecina
								if(ciudad.getIdEntidad()==ciudadVecina){
									for (ArrayList<Integer> entidad : ciudad.getCiudadesAdyacentes()) {
										//si la posicion 2 de dicha entidad, coincido con el id dela ciudad vecina, encontre el path y tomo el valor del id en la posicion1
										if(entidad.get(2) ==idNewCastle){
											
											idDelPathQueNecesito = entidad.get(1);
											System.out.println("Se encontro el id del path: " + idDelPathQueNecesito);
											
											//No creo ningun path puesto que ya existe, solo le asigno el camino correspondiente como adyacente
											ArrayList<Integer> caminoAdyacente= new ArrayList<Integer>();
											caminoAdyacente.add(3); // el "3" representa un camino
											caminoAdyacente.add(idDelPathQueNecesito); // el id del path que estamos creando
											caminoAdyacente.add(ciudadVecina); // IMPORTANTE, aquie agrego el id de la ciudad a la que el camino con el id dado Llega,
					 						
											caminosAdyacentesCastillo1.add(caminoAdyacente);
											break;
										}
									}
								}
								
								if (idDelPathQueNecesito != 0){
									break;
								}
						   }
							
						
						}
						
						
						
					}
					
					//
					//Genero el segundo castillo
					//
					//
					//
					//
					
					
					
					
                   int idNewCastle2 = castillo2.peekFirst(); //Identifico el id de la nueva ciudad
					
					List<Integer> idsCiudadesVecinasCastillo2 = new ArrayList<Integer>(castillo2); // Identifico los ids de las ciudades vecinas
					System.out.println("generando castillo " + idsCiudadesVecinasCastillo2 );
					List<ArrayList<Integer>>caminosAdyacentesCastillo2 = new ArrayList<ArrayList<Integer>>();
					for (Integer  ciudadVecinaCastillo2: idsCiudadesVecinasCastillo2) {
						int idDelPathQueNecesito = 0;
						//recorro las ciudades
						
						//caso border, conexion castillo - castillo, path ya existe, tomar valor del path que esta en memoria,  es el ultimo valor del contador
						if(ciudadVecinaCastillo2.equals(1)){
							ArrayList<Integer> caminoAdyacente= new ArrayList<Integer>();
							caminoAdyacente.add(3); // el "3" representa un camino
							caminoAdyacente.add(idPath); // el id del path que estamos creando, representa el path de castillo a castillo antes generado
							caminoAdyacente.add(ciudadVecinaCastillo2); // IMPORTANTE, aquie agrego el id de la ciudad a la que el camino con el id dado Llega,
	 						
							caminosAdyacentesCastillo2.add(caminoAdyacente);
							
			
							
						}else{
							for (City ciudad : cities) {
								//recorro las entidades adyacentes de este castillo, que representan los caminos
								//Yo necesito obtener a la ciudad vecina
								if(ciudad.getIdEntidad()==ciudadVecinaCastillo2){
									for (ArrayList<Integer> entidad : ciudad.getCiudadesAdyacentes()) {
										//si la posicion 2 de dicha entidad, coincido con el id dela ciudad vecina, encontre el path y tomo el valor del id en la posicion1
										if(entidad.get(2) == idNewCastle2){
											
											idDelPathQueNecesito = entidad.get(1);
											System.out.println("Se encontro el id del path: " + idDelPathQueNecesito);
											//No creo ningun path puesto que ya existe, solo le asigno el camino correspondiente como adyacente
											ArrayList<Integer> caminoAdyacente= new ArrayList<Integer>();
											caminoAdyacente.add(3); // el "3" representa un camino
											caminoAdyacente.add(idDelPathQueNecesito); // el id del path que estamos creando
											caminoAdyacente.add(ciudadVecinaCastillo2); // IMPORTANTE, aquie agrego el id de la ciudad a la que el camino con el id dado Llega,
					 						
											caminosAdyacentesCastillo2.add(caminoAdyacente);
											break;
										}
									}
								}
								
								if (idDelPathQueNecesito != 0){
									break;
								}
						   }
							
							
							
						}
						
						
						
					}
					new Castle(caminosAdyacentesCastillo2, idCastillo2, idCastillo2 );
					new Castle(caminosAdyacentesCastillo1, 1 , 1);			
					
					System.out.println("Castillo 1 construido con adyacentes " + caminosAdyacentesCastillo1);
					System.out.println("Castillo 2 construido  con adyancentes" + caminosAdyacentesCastillo2);
					
					
					
					
					/////////////////////////777
					///////////////////////////7
					/////////////////////////////
					////////////////////////////
					
					//Creacion de Entidades finalizada
					
					
					
					
					/*
					
					new Thread() {
						public void run() {
							new Castillo(1, ciudades.get(0).remove(0), ciudades.get(0));
						}
					}.start();
					new Thread() {
						public void run() {
							new Castillo(2, ciudades.get(ciudades.size()-1).remove(0), ciudades.get(ciudades.size()-1));
						}
					}.start();
					
					// Crear las ciudades con sus respectivos caminos
					for(int i=1; i < ciudades.size()-2; i++) {
						//this.crearCiudad(ciudades.get(i));
						final int j = i;
						new Thread() {
							public void run() {
								new Ciudad(ciudades.get(j).remove(0), ciudades.get(j));								
							}
						}.start();

					}
					*/
				}
			}
			/*
			private void crearCastillo(int bando, List<Integer> caminos) {
				new Castillo(bando, caminos.remove(0), caminos);
			}
			
			private void crearCiudad(List<Integer> caminos) {
				new Ciudad(caminos.remove(0), caminos);
			}*/
		}.start();
		/*	
		while (true) {
			String line = readLine(System.in);
			if (!line.contains("*")) {
				c1.send(line);
			} else {
				for (float i = 0.1f; i <= 1.0f; i += 0.1) {
					c1.send(line.replaceAll("[*]", "") + " " + i);
					Thread.sleep(25);
				}
			}
		}*/
		
		// Correr la UI
		new Thread() {
			public void run() {
				String[] arg = {inputChannel.toString(), outputChannel.toString()};
				
				try {
					ConcurBattles.main(arg);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public static boolean gameOver(){
		return gameOver;
	}
	
	public static void setGameOver(boolean b) {
		gameOver = b;
	}
}

