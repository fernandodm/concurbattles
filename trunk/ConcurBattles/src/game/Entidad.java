package game;

import java.util.ArrayList;
import java.util.List;
  
import channel.Channel;

public class Entidad {
	private int bando = 0;
    private Integer id;
	private  List<ArrayList<Integer>> ciudadesAdyacentes;
	private ArrayList<Unidad> unidades = new ArrayList <Unidad>();
	/**
	 * Dada una Unidad se verifica si en la ciudad existan unidades del bando contrario
	 * @param unaUnidad
	 * @return
	 */
	protected  boolean hayUnidadesDeBandoContrarioA(Unidad unaUnidad){
		for (Unidad unidad : unidades) {
			if(! (unidad.getBando() == unaUnidad.getBando())){
				return true;
			}
		}
		return false;
	}
	/**
	 * Dada una unidad, esta peleara contra todas las unidades del bando contrario en la ciudad
	 * hasta matar a todas o morir en el intento
	 * @param unaUnidad
	 */
	protected void iniciarPelea(Unidad unaUnidad){
		for (Unidad enemigo : unidades) {
			
			
			unaUnidad.pelear(enemigo);
			
			if(enemigo.isEstoyVivo()){
			   break;
			}else{
			  unidades.remove(enemigo);
			}
			
			
			
		}
	}
	
	/**
	 * <EL SANTO GRIAL>
	 * 
	 * Metodo que representa el corazon del funcionamiento.
	 * 
	 * Se pide a todas las unidades que decidan que hacer, cada unidad respondera por true o false
	 * si deciden viajar o no. Caso afirmativo, son quitadas de la ciudad y las unidades reciben por parametro
	 * las ciudades adyacentes entre la que elegiran una para viajar; caso contrario, se queda en la ciudad.
	 */
	protected void decidan(){
       for (Unidad unidad : this.getUnidades()) {
			boolean decision = unidad.decidirViajar();
			if(decision){
				this.getUnidades().remove(unidad);
				unidad.setCiudadAnterior(this.getId());
				unidad.viajar(this.getId(), this.getCiudadesAdyacentes());
			}
			
			
		}
	}
	
	
	public int getBando() {
		return bando;
	}
	public void setBando(int bando) {
		this.bando = bando;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<ArrayList<Integer>> getCiudadesAdyacentes() {
		return ciudadesAdyacentes;
	}
	public void setCiudadesAdyacentes(List<ArrayList<Integer>> ciudadesAdyacentes) {
		this.ciudadesAdyacentes = ciudadesAdyacentes;
	}
	public List<Unidad> getUnidades() {
		return unidades;
	}
	public void setUnidades(ArrayList<Unidad> unidades) {
		this.unidades = unidades;
	}
	public static void main(String[] args) {
		
		
	}
}
