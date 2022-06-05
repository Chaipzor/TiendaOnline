package TiendaOnline;


public class Usuario {
	private String nombre;
	private String clave;
	private int intentos; // Número de intentos fallados de login
	private int MAXINTENTOS = 3; // Maximo de intentos para bloquear al usuario tras contraseña incorrecta
	private boolean permiso; // Admins = true, usuarios normales = false;
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getClave() {
		return clave;
	}

	public int getMAXINTENTOS() {
		return MAXINTENTOS;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public int getIntentos() {
		return intentos;
	}

	public void setIntentos(int intentos) {
		this.intentos = intentos;
	}

	public Usuario(String nombre, String clave) {
		this.nombre = nombre;
		this.clave = clave;
	}

	public Usuario() {
	}

	public boolean getPermiso() {
		return permiso;
	}

	public void setPermiso(boolean permiso) {
		this.permiso = permiso;
	}
	
}
