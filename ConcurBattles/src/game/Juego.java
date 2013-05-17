package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;

import ar.edu.unq.tpi.concurbattles.ConcurBattles;
import channel.Channel;

public class Juego {
	public static final Integer inputChannel = 9990;
	private static final Integer outputChannel = 9991;
	private static boolean gameOver = false;
	
	public static void main(String[] args) {
		
		//final Channel<String> c1 = new Channel<String>(inputChannel);
		
		final Channel<String> c2 = new Channel<String>(outputChannel);
		
		new Thread() {
			public void run() {
				while (true) {
					String[] mapa = c2.receive().split("\n");
					
					final List<List<Integer>> ciudades = new ArrayList<List<Integer>>();
					
					for(String camino : mapa) {
						// Separarlo por espacios y sacar el primer espacio en blanco
						List<String> caminos = new ArrayList<String>(Arrays.asList(camino.split(" ")));
						caminos.remove(0);
						
						//////////////////////////////////////////////
						/// ACA HAY QUE ARMAR LOS List de ArrayList ??
						//////////////////////////////////////////////
						List<Integer> caminosInt = new ArrayList<Integer>();
						for(String s : caminos) {
							caminosInt.add(parseInt(s));
						}
						//////////////////////////////////////////////
						
						// Agregar los caminos a la lista
						ciudades.add(caminosInt);
					}
					
					// Mostrar ciudades con caminos parseados
					System.out.println(ciudades);
					
					// Crear los dos castillos
					//this.crearCastillo(1, ciudades.get(0));
					//this.crearCastillo(2, ciudades.get(ciudades.size()-1));
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
