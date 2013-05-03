package game;

public class GeneradorDeCanal {
	
	private static int permisoEspecial = 4999;
	private static int permiso = 5999;
	private static int numeroDeCanal = 6999;
	private static int otroNumeroDeCanal = 7999;
	
	public static int generarPermisoEspecial(){
		
		permisoEspecial = permisoEspecial + 1;
		
		return permisoEspecial;
	}
	
	public static int generarPermiso(){
		
		permiso= permiso + 1;
		
		return permiso;
	}

	public static int generarNumeroDeCanal(){
	
		numeroDeCanal = numeroDeCanal + 1;
	
		return numeroDeCanal;
	}

	public static int generarOtroNumeroDeCanal(){
		
		otroNumeroDeCanal = otroNumeroDeCanal + 1;
	
		return otroNumeroDeCanal;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
	}

}
