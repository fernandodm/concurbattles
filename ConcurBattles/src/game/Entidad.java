package game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
  
import channel.Channel;

public class Entidad {
	private int bando = 0;
    private Integer idEntidad;
	private  List<ArrayList<Integer>> ciudadesAdyacentes;
	
	private ArrayList<Unidad> unidades = new ArrayList <Unidad>();
	/**getId
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
		
		
		
		ArrayList<Unidad> muertos = new ArrayList<Unidad>();
		for (Unidad enemigo : unidades) {
			
			
			
			System.out.println(" unidad " + unaUnidad.getId()+ " de bando  " + unaUnidad.getBando()+ " va a pelear con unidad de bando contrario " + enemigo.getId());
			unaUnidad.pelear(enemigo);
			if(enemigo.isEstoyVivo()){
				System.out.println("La unidad atacante ha muerto!");
			   break;
			}else{
				System.out.println("Unidad" +unaUnidad.getId()+  "mato a un defensor!!");
				Unidad muerto = enemigo;
				muertos.add(enemigo);
			 //unidades.remove(enemigo);
			}
			
			
			
		}
		if(!muertos.isEmpty()){
			
			unidades.removeAll(muertos);
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
		ArrayList<Unidad> viajeros = new ArrayList<Unidad>();
       for (Unidad unidad : this.getUnidades()) {
			boolean decision = unidad.decidirViajar(); 
			if(decision){
				viajeros.add(unidad);
				//this.getUnidades().remove(unidad); <--- remove inside list iteration == Java explotion
				unidad.setCiudadAnterior(this.getIdEntidad());
				unidad.viajar(this.getIdEntidad(), this.getCiudadesAdyacentes());
			}else{
				System.out.println(" unanidad " + unidad.getId()+ " de bando  " + unidad.getBando()+ " decidio quedarse");
			}
			
			
		}
       if(!viajeros.isEmpty()){
    	   this.getUnidades().removeAll(viajeros);
       }
       
	}
	
	
	public int getBando() {
		return bando;
	}
	
	public void setBando(int bando) {
		this.bando = bando;
	}
	public int getIdEntidad() {
		return idEntidad;
	}
	public void setIdEntidad(int id) {
		this.idEntidad = id;
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
