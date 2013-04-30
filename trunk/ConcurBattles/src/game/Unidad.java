package game;

public class Unidad {
	
	private int bando;
	private int nivel = 1;
	private int batallasGanadas = 1;
	
	public Unidad(int bando) {
		this.setBando(bando);
	}
	
	public void pelear(Unidad contrincante){
		
		double misProbabilidadesDeGanar = (double) this.getNivel() / 
		(this.getNivel() + contrincante.getNivel());
		
		
		if (misProbabilidadesDeGanar >= Math.random()){
			this.ganarPelea();
			contrincante.morir();
		}else{
			contrincante.ganarPelea();
			this.morir();
		}
	}
	
	/**
	 * Computa una nueva batalla ganada,
	 * si el numero es fibonacci subis de nivel
	 */
	private void ganarPelea() {
		this.batallasGanadas += 1;
		
		if(this.esFibonacci(this.getBatallasGanadas())) {
			this.nivel += 1;
		}
	}
	
	private void morir() {
	}
	
	/**
	* Calcula si un numero pertenece a  la serie de fibonaci
	* @param numero
	* @return
	*/
	private boolean esFibonacci(int numero){
		return (  this.esCuadradoPerfecto ( (5 * (numero*numero))+4) ) ||
				(this.esCuadradoPerfecto ( 5 * (numero*numero)-4));
	}
	
	/**
	* calcula si un numero es cuadrado perfecto
	* @param num
	* @return true cuando la raiz cuadrada es entera
	*/
	private boolean esCuadradoPerfecto(int num){
		double raiz = Math.sqrt(num);
		return ( (raiz - Math.floor(raiz))  == 0);
	}

	public int getBando() {
		return bando;
	}

	public void setBando(int bando) {
		this.bando = bando;
	}

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	public int getBatallasGanadas() {
		return batallasGanadas;
	}

	public void setBatallasGanadas(int batallasGanadas) {
		this.batallasGanadas = batallasGanadas;
	}

}
