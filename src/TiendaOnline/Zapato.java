package TiendaOnline;

public class Zapato {
	private String nombreZapato;
	private double precioCompra;
	private double precioVenta;
	private int unidades;

	public Zapato() {
	}

	public Zapato(String nombreZapato, int unidades, double precioVenta) {
		this.nombreZapato = nombreZapato;
		this.unidades = unidades;
		this.precioVenta = precioVenta;
	}

	public Zapato(String nombreZapato, double precioCompra, int unidades) {
		final double MARGEN = 1.55;
		this.nombreZapato = nombreZapato;
		this.precioCompra = precioCompra;
		this.precioVenta = precioCompra * MARGEN;
		this.unidades = unidades;
	}

	public String getNombreZapato() {
		return nombreZapato;
	}

	public void setNombreZapato(String nombreZapato) {
		this.nombreZapato = nombreZapato;
	}

	public int getUnidades() {
		return unidades;
	}

	public void setUnidades(int unidades) {
		this.unidades = unidades;
	}

	public double getPrecioCompra() {
		return precioCompra;
	}

	public void setPrecioCompra(double precioCompra) {
		this.precioCompra = precioCompra;
	}

	public double getPrecioVenta() {
		return precioVenta;
	}

	public void setPrecioVenta(double precioVenta) {
		this.precioVenta = precioVenta;
	}

	public double getSubtotal() {
		double subtotal = precioVenta * unidades;
		return subtotal;
	}
	
}
