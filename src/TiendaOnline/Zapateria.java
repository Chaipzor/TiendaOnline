package TiendaOnline;

import java.util.ArrayList;
import java.util.Scanner;
import java.sql.*;

/*
Un amigo acaba de abrir un nuevo negocio de venta de calzado, 
y nos pide ayuda para poder facilitarle las tareas cotidianas de la tienda con un TPV básico. 
Nos pide desarrollar un programa multiplataforma, ya que lo quiere instalar en un pequeño portátil que tiene en la tienda 
Por ahora nuestro amigo sólo vende tres tipos de zapatos: sandalias, calzado deportivo y zapatos de vestir.
Cada modelo de zapato lo adquiere a un precio distinto, pero para venderlos y que el negocio prospere, nuestro amigo supone que con una ganancia del 
55% sobre el precio del calzado de cada tipo sacará suficiente para hacer viable su negocio.
Cuando atiende un cliente, este puede comprar cualquier modelo de calzado de los tres tipos y la cantidad que desee de cada uno de ellos. 
Solo importa poder elegir zapatos de un tipo y otro, no el modelo en concreto. En la línea de caja, se introducen el precio de compra y 
la cantidad de zapatos comprado de cada tipo.
Al cliente, si es amigo, conocido o tiene un ticket de promoción, se le puede aplicar un 8% de descuento sobre la compra que realiza.
Ayuda a este amigo a crear un programa que, para un cliente dado, muestre su nombre, el numero de zapatos vendidos de cada tipo, 
el valor de la venta sin descuento, el descuento, valor de la venta con descuento si tuviera y valor de la venta incluyendo IVA. 
Todos los precios deben mostrar en € y los impuestos con el símbolo de %.
Para implementar el programa sólo se pueden utilizar los tipos de datos básicos vistos en clase, y bucles.
Para realizar los cálculos se puede utilizar la librería Math de Java

*/

public class Zapateria {
	public static void main(String[] args) {
		int opc; // Guarda 1 si es admin y 2 si tiene permisos normales.
		String opcAdmin;
		String opcArt;
		String opcUser;
		String opcNormal = null;
		double dto = 0;
		ArrayList<Zapato> cesta = new ArrayList<Zapato>();
		ArrayList<Usuario> listaUsuarios = new ArrayList<Usuario>();
		// Repetiremos el menú hasta que el usuario quiera terminar.
		do {

			// Devuelve 0 si el usuario no existe o está bloqueado, 1 si es admin y 2 si
			// tiene permisos normales.
			opc = login();
		} while (opc != 1 && opc != 2);
		do {
			// Entramos en el menú administrador.
			if (opc == 1) {
				do {
					opcAdmin = panelAdmin();
					// Entramos en el panel de administración de artículos
					if (opcAdmin.equals("1")) {
						do {
							opcArt = panelArticulos();
							if (opcArt.equals("1")) {
								altaProducto(); // Alta de artículos
							} else if (opcArt.equals("2")) {
								listarProductosAdmin(); // Listado de artículos (nombre, stock, pvp)
							}
						} while (!opcArt.equals("0"));
					} else if (opcAdmin.equals("2")) { // Entramos en el panel de administración de usuarios
						do {
							opcUser = panelUsuarios();
							if (opcUser.equals("1")) {
								altaUsuario(); // Alta de usuarios
							} else if (opcUser.equals("2")) {
								bajaUsuario(); // Baja de usuarios
							} else if (opcUser.equals("3")) {
								listarUsuarios();
							} else if (opcUser.equals("4")) {
								editarUsuario();
							} else if (opcUser.equals("5")) {
								desbloquear(); // Desbloqueo de un usuario
							} else if (opcUser.equals("6")) {
								desbloquearTodos();// Desbloqueo de todos los usuarios de alta
							}

						} while (!opcUser.equals("0"));
					} else if (opcAdmin.equals("3")) {
						do {
							opcNormal = menuUsuarios(opc); // Menú de usuarios normales (desde administrador).
							if (opcNormal.equals("1")) {
								// meterCesta(zapatos, cesta); // Introducir artículos en la cesta
							} else if (opcNormal.equals("2")) {
								verCesta(cesta); // Ver elementos y cantidades de la cesta
							} else if (opcNormal.equals("3")) {
								// editarCesta(zapatos, cesta); // Modificar artículos de la cesta (eliminar
								// cantidades)
							} else if (opcNormal.equals("4")) {
								dto = descuento(); // Activar descuento con un código promocional
							}
						} while (!opcNormal.equals("0") && !opcNormal.equals("5"));
						if (opcNormal.equals("0")) {
							ticket(cesta, dto); // Presentación del ticket final
						}
					}
				} while (!opcAdmin.equals("0"));
			} else if (opc == 2) {
				do {
					opcNormal = menuUsuarios(opc); // Menú de usuarios sin permisos (general)
					if (opcNormal.equals("1")) {
						// meterCesta(zapatos, cesta); // Introducir artículos en la cesta
					} else if (opcNormal.equals("2")) {
						verCesta(cesta); // Ver elementos y cantidades de la cesta
					} else if (opcNormal.equals("3")) {
						// editarCesta(zapatos, cesta); // Modificar artículos de la cesta (eliminar
						// cantidades)
					} else if (opcNormal.equals("4")) {
						dto = descuento(); // Activar descuento con un código promocional
					}
				} while (!opcNormal.equals("0"));
				if (opcNormal.equals("0")) {
					ticket(cesta, dto); // Presentación del ticket final
				}
			}
		} while (opcNormal.equals("5"));
		System.out.println("Hasta pronto!!");
	}

	// Función para dar de alta los nuevos productos, calcula su precio de venta.
	public static void altaProducto() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Código de producto: ");
		String codigo = sc.nextLine();
		Statement instruccionSQL = conectarBBDD();
		boolean comprobacion = comprobarProducto(instruccionSQL, codigo); // 1 = Ya existe, 0 = no existe.
		if (comprobacion) {
			actualizarProducto(instruccionSQL, codigo);
		} else {
			nuevoProducto(instruccionSQL, codigo);
		}
	}

	public static Statement conectarBBDD() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			java.sql.Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/tiendaonline",
					"root", "");
			Statement instruccionSQL = conexion.createStatement();
			return instruccionSQL;
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Error de conexión");
		}
		return null;
	}

	public static boolean comprobarProducto(Statement instruccionSQL, String codigo) {
		try {
			ResultSet resultadosConsulta = instruccionSQL
					.executeQuery("SELECT codigo_producto FROM productos WHERE codigo_producto= '" + codigo + "'");
			if (resultadosConsulta.next()) {
				if (resultadosConsulta.getString("codigo_producto").equals(codigo)) {
					System.out.println("Existe");
					return true;
				}
			} else {
				System.out.println("NO Existe");
				return false;
			}
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Error de conexión comprobarProducto");
		}
		return false;
	}

	public static void actualizarProducto(Statement instruccionSQL, String codigo) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Cantidad: ");
		int nuevasUnidades = Integer.parseInt(sc.nextLine());
		System.out.println("Precio de compra: ");
		double precioCompra = Double.parseDouble(sc.nextLine());
		try {
			String query = "UPDATE productos SET stock = stock + '" + nuevasUnidades + "' WHERE codigo_producto= '"
					+ codigo + "'";
			instruccionSQL.executeUpdate(query);
			query = "UPDATE productos SET precio_compra = '" + precioCompra + "' WHERE codigo_producto= '" + codigo
					+ "'";
			instruccionSQL.executeUpdate(query);
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Error de conexión comprobarProducto");
		}
	}

	public static void nuevoProducto(Statement instruccionSQL, String codigo) {
		Scanner sc = new Scanner(System.in);
		final double MARGEN = 1.55;
		System.out.println("Nombre del producto: ");
		String nombre = sc.nextLine();
		System.out.println("Precio de coste: ");
		double precio = Double.parseDouble(sc.nextLine());
		System.out.println("Introduce la cantidad en almacen: ");
		int unidades = Integer.parseInt(sc.nextLine());
		double precioVenta = precio * MARGEN;
		try {
			String query = "INSERT INTO productos (codigo_producto, nombre, precio_compra, precio_venta,stock) values('"
					+ codigo + "','" + nombre + "','" + precio + "','" + precioVenta + "','" + unidades + "')";
			instruccionSQL.executeUpdate(query);
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Error de conexión nuevoProducto");
		}
	}

	public static void listarProductosAdmin() {
		try {
			Statement instruccionSQL = conectarBBDD();
			String query = "SELECT * FROM productos";
			ResultSet resultadosConsulta = instruccionSQL.executeQuery(query);
			while (resultadosConsulta.next()) {
				System.out.println(resultadosConsulta.getString("nombre"));
				System.out.println(resultadosConsulta.getString("precio_venta"));
				System.out.println(resultadosConsulta.getString("stock"));
			}
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Error de conexión nuevoProducto");
		}
	}

	// Mostramos las opciones del menú principal.
	public static String menuUsuarios(int admin) {
		Scanner sc = new Scanner(System.in);

		System.out.println("\n\n1. Añadir a la cesta");
		System.out.println("2. Ver cesta");
		System.out.println("3. Editar cesta");
		System.out.println("4. Cupón descuento");
		if (admin == 1) {
			System.out.println("5. Volver al menú de administración");
			System.out.println("0. Ver ticket y volver al menú de administración");
		} else {
			System.out.println("0. Ver ticket y finalizar");
		}
		return sc.nextLine();
	}

	// Comprobamos el login, si existe usuario, la clave, si está bloqueado o no y
	// mostramos
	// también los intentos que le quedan disponibles en formato "X/MAXINTENTOS".
	public static int login() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Nombre de usuario: ");
		String user = sc.nextLine();
		String pass = "";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			java.sql.Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/tiendaonline",
					"root", "");
			if (conexion != null) {
				Statement instruccionSQL = conexion.createStatement();
				ResultSet resultadosConsulta = instruccionSQL
						.executeQuery("SELECT * FROM usuarios WHERE usuario= '" + user + "'");
				if (resultadosConsulta.next()) {
					if (Integer.parseInt(resultadosConsulta.getString("intentos")) == 3) {
						System.out.println("Usuario bloqueado, intento máximos superados.");
						return -1;
					}
					System.out.println("Contraseña: ");
					pass = sc.nextLine();
					if (resultadosConsulta.getString("clave").equals(pass)) {
						System.out.println("Bienvenido.");
						if (resultadosConsulta.getString("permiso").equals("1")) {
							System.out.print("Administrador!");
							return 1;
						}
					} else if (Integer.parseInt(resultadosConsulta.getString("intentos")) < 3) {
						System.out.println("Usuario y contraseña incorrectos.");
						String query = "UPDATE usuarios SET intentos = intentos + 1 where usuario =('" + user + "')";
						instruccionSQL.executeUpdate(query);
						return 0;
					}
				} else {
					System.out.println("usuario inexistente.");
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Error de conexión");
		}
		return -1;
	}

	// Se registra un nuevo usuario siempre y cuando no esté utilizado ya ese
	// nombre.
	public static void altaUsuario() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Nombre de usuario: ");
		String usuario = sc.nextLine();
		Statement instruccionSQL = conectarBBDD();
		boolean comprobacion = comprobarUsuario(instruccionSQL, usuario); // 1 = Ya existe, 0 = no existe.
		if (!comprobacion) {
			nuevoUsuario(instruccionSQL, usuario);
		} else {
			System.out.println("El nombre de usuario ya está siendo utilizado.");
		}
	}

	public static boolean comprobarUsuario(Statement instruccionSQL, String usuario) {
		try {
			ResultSet resultadosConsulta = instruccionSQL
					.executeQuery("SELECT usuario FROM usuarios WHERE usuario= '" + usuario + "'");
			if (resultadosConsulta.next()) {
				if (resultadosConsulta.getString("usuario").equals(usuario)) {
					return true;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Error de conexión usuarios Usuario");
		}
		return false;
	}

	public static void nuevoUsuario(Statement instruccionSQL, String usuario) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Clave: ");
		String clave = sc.nextLine();
		System.out.println("email: ");
		String email = sc.nextLine();
		System.out.println("Permisos (1: Administrador, 0: Alta usuario normal): ");
		int permisos = Integer.parseInt(sc.nextLine());
		try {
			String query = "INSERT INTO usuarios (usuario, clave, email, permiso) values('"
					+ usuario + "','" + clave + "','" + email + "','" + permisos + "')";
			instruccionSQL.executeUpdate(query);
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Error de conexión nuevoProducto");
		}
	}

	// Se da de baja el usuario introducido siempre que exista preaviemente
	public static void bajaUsuario() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Introduce el nombre de usuario: ");
		String usuario = sc.nextLine();
		Statement instruccionSQL = conectarBBDD();
		boolean comprobacion = comprobarUsuario(instruccionSQL, usuario); // 1 = Ya existe, 0 = no existe.
		if (comprobacion) {
			try {
				String query = "DELETE FROM usuarios where usuario='" + usuario + "'";
				instruccionSQL.executeUpdate(query);
				System.out.println("Usuario eliminado correctamente.");
			} catch (Exception e) {
				System.out.println(e);
				System.out.println("Error de conexión Borrar usuario");
			}
		}
	}

	public static void listarUsuarios() {
		try {
			Statement instruccionSQL = conectarBBDD();
			String query = "SELECT * FROM usuarios";
			ResultSet resultadosConsulta = instruccionSQL.executeQuery(query);
			while (resultadosConsulta.next()) {
				System.out.println(resultadosConsulta.getString("usuario"));
				System.out.println(resultadosConsulta.getString("email"));
			}
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Error de conexión nuevoProducto");
		}
	}

	public static void editarUsuario() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Usuario: ");
		String usuario = sc.nextLine();
		Statement instruccionSQL = conectarBBDD();
		boolean comprobacion = comprobarUsuario(instruccionSQL, usuario); // 1 = Ya existe, 0 = no existe.
		if (comprobacion) {
			System.out.println("Nuevo email: ");
			String email = sc.nextLine();
			try {
				String query = "UPDATE usuarios SET email = '" + email + "' WHERE usuario= '"
						+ usuario + "'";
				instruccionSQL.executeUpdate(query);
			} catch (Exception e) {
				System.out.println(e);
				System.out.println("Error de conexión editarUsuario");
			}
		}
	}

	// Menú para el administrador, con la posibilidad de desbloquear (o resetear a 0
	// los intentos)
	public static String panelAdmin() {
		Scanner sc = new Scanner(System.in);
		System.out.println("\n1) Control de artículos");
		System.out.println("2) Control de usuarios");
		System.out.println("3) Panel de clientes");
		System.out.println("0) Salir");
		return sc.nextLine();
	}

	public static String panelArticulos() {
		Scanner sc = new Scanner(System.in);
		System.out.println("\n1) Alta");
		System.out.println("2) Listado");
		System.out.println("0) Salir");
		return sc.nextLine();
	}

	public static String panelUsuarios() {
		Scanner sc = new Scanner(System.in);
		System.out.println("\n1) Alta");
		System.out.println("2) Baja");
		System.out.println("3) Listar");
		System.out.println("4) Editar");
		System.out.println("5) Desbloquear");
		System.out.println("6) Desbloquear todos");
		System.out.println("0) Salir");
		return sc.nextLine();
	}

	// Desbloquea un usuario existente (o resetea a 0 los intentos)

	public static void desbloquear() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Introduce el nombre de usuario a desbloquear: ");
		String usuario = sc.nextLine();
		Statement instruccionSQL = conectarBBDD();
		boolean comprobacion = comprobarUsuario(instruccionSQL, usuario); // 1 = Ya existe, 0 = no existe.
		if (comprobacion) {
			try {
				String query = "UPDATE usuarios SET intentos = '0' WHERE usuario= '" + usuario + "'";
				instruccionSQL.executeUpdate(query);
			} catch (Exception e) {
				System.out.println(e);
				System.out.println("Error de conexión editarUsuario");
			}
		}
	}

	// Desbloquea a todos los usuarios existentes (o resetea a 0 los intentos).
	public static void desbloquearTodos() {
		Statement instruccionSQL = conectarBBDD();
		try {
			String query = "UPDATE usuarios SET intentos = '0'";
			instruccionSQL.executeUpdate(query);
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Error de conexión editarUsuario");
		}
	}

	// Lista todos los artículos disponibles en stock y su PVP

	// Introduce nuevos artículos y cantidades en la cesta

	public static void meterCesta(ArrayList<Zapato> zapatos, ArrayList<Zapato> cesta) {
		Scanner sc = new Scanner(System.in);
		int control = 0;
		System.out.println("Listado de productos y stocks: ");
		System.out.println("Artículo \t\t Stock");
		for (int i = 0; i < zapatos.size(); i++) {
			System.out.println(i + ")" + zapatos.get(i).getNombreZapato() + " \t\t " + zapatos.get(i).getUnidades());
			control = i;
		}
		String art;
		int existe = -1; // Si es -1, no existía, si es 0 o superior, ya existía y guarda la posición.
		do {
			System.out.println("Selecciona un artículo (nº de artículo): ");
			art = sc.nextLine();
		} while (Integer.parseInt(art) > control);
		int cantidad;
		int articulo = Integer.parseInt(art);
		do {
			System.out.println("Stock disponible: " + zapatos.get(articulo).getUnidades());
			System.out.println("Cantidad de " + zapatos.get(articulo).getNombreZapato() + " a comprar: ");
			cantidad = sc.nextInt();
			if (cantidad > zapatos.get(articulo).getUnidades()) {
				System.out.println("Stock máximo superado.");
			}
		} while (cantidad > zapatos.get(articulo).getUnidades());
		if (!(cantidad == 0)) {
			Zapato articuloACesta = new Zapato(zapatos.get(articulo).getNombreZapato(), cantidad,
					zapatos.get(articulo).getPrecioVenta());
			for (int i = 0; i < cesta.size(); i++) {
				if (zapatos.get(articulo).getNombreZapato().equals(cesta.get(i).getNombreZapato())) {
					existe = i;
				}
			}
			if (existe < 0) {
				cesta.add(articuloACesta);
			} else {
				// Acumulamos en cantidad, y se mostrará en una única línea en el ticket

				cesta.get(existe).setUnidades(cesta.get(existe).getUnidades() + cantidad);
			}
			// Minora la cantidad de unidades compradas del stock.

			zapatos.get(articulo).setUnidades(zapatos.get(articulo).getUnidades() - cantidad);
		}
	}

	// Muestra la cesta

	public static void verCesta(ArrayList<Zapato> cesta) {
		System.out.println("\tCESTA:");
		if (cesta.size() == 0) {
			System.out.println("Tu cesta está vacía.");
		} else {
			System.out.println("Listado de productos y cantidades: ");
			for (int i = 0; i < cesta.size(); i++) {
				System.out.println(cesta.get(i).getNombreZapato() + "\t" + cesta.get(i).getUnidades());
			}
		}
	}

	// Posibilidad de eliminar artículos por cantidades de la cesta.

	public static void editarCesta(ArrayList<Zapato> zapatos, ArrayList<Zapato> cesta) {
		String art = null;
		Scanner sc = new Scanner(System.in);
		int control = 0;
		System.out.println("\tCESTA:");
		if (cesta.size() == 0) {
			System.out.println("Tu cesta está vacía.");
		} else {
			System.out.println("Listado de productos y cantidades: ");
			for (int i = 0; i < cesta.size(); i++) {
				System.out.println(i + ") " + cesta.get(i).getNombreZapato() + "\t" + cesta.get(i).getUnidades());
				control = i;
			}
			do {
				System.out.println("Selecciona un artículo (nº de artículo): ");
				art = sc.nextLine();
			} while (Integer.parseInt(art) > control);
		}
		int cantidad;
		int articulo = Integer.parseInt(art);
		do {
			System.out.println("Stock disponible: " + cesta.get(articulo).getUnidades());
			System.out.println("Cantidad de " + cesta.get(articulo).getNombreZapato() + " a quitar: ");
			cantidad = sc.nextInt();
			if (cantidad > cesta.get(articulo).getUnidades()) {
				System.out.println("No había tantas unidades en tu cesta.");
				System.out.println("Las unidades en la cesta son: " + cesta.get(articulo).getUnidades());
			}
		} while (cantidad > cesta.get(articulo).getUnidades());
		if (!(cantidad == 0)) {
			Zapato sacarDeCesta = new Zapato(cesta.get(articulo).getNombreZapato(), cantidad,
					cesta.get(articulo).getPrecioVenta());
			cesta.get(articulo).setUnidades(cesta.get(articulo).getUnidades() - cantidad); // Devolvemos la cantidad a
																							// stock.
		}
		zapatos.get(articulo).setUnidades(zapatos.get(articulo).getUnidades() + cantidad); // Incrementa la cantidad de
		// unidades del stock.
	}

	// Comprobación de que el cliente tiene código de descuento verificado

	public static double descuento() {
		final String CODIGO = "PROG22";
		final double DESCUENTO = 0.08;
		Scanner sc = new Scanner(System.in);
		System.out.println("¿Tienes un cupón de descuento? (S/N)");
		if (sc.nextLine().equalsIgnoreCase("s")) {
			System.out.println("Introduce el código del cupón: ");
			if (sc.nextLine().equalsIgnoreCase(CODIGO)) {
				System.out.println("Descuento del 8% aplicado correctamente.");
				return DESCUENTO;
			}
		}
		System.out.println("El cupón introducido no es válido.");
		return DESCUENTO;
	}

	// Impresión del ticket en formato adecuado

	public static void ticket(ArrayList<Zapato> cesta, double dto) {
		final double IVA = 0.21;
		System.out.println("------------------------------------------------------------------------------------");
		System.out.printf("|%-20s|%-20s|%-20s|%-20s|\n", "Descripción", "Precio", "Cantidad", "Subtotal");
		System.out.println("------------------------------------------------------------------------------------");
		double total = 0;
		for (int i = 0; i < cesta.size(); i++) {
			Zapato x = cesta.get(i);
			System.out.printf("|%-20s|%-10.2f%-10s|%-20d|%-10.2f%-10s|\n", x.getNombreZapato(), x.getPrecioVenta(), "€",
					x.getUnidades(), x.getSubtotal(), "€");
			total += x.getSubtotal();
		}
		System.out.println("------------------------------------------------------------------------------------");

		if (dto != 0) {
			System.out.printf("|%70s %10.2f %1s|\n", "Total sin cupón: ", total, "€");
			System.out.printf("|%62s%1.2f%1s%1s %10.2f %1s|\n", "Descuento(", dto * 100, "%)", ": ", (-total * dto),
					"€");
			System.out.printf("|%70s %10.2f %1s|\n", "Total: ", total * (1 - dto), "€");
		} else {
			System.out.printf("|%70s %10.2f %1s|\n", "Total: ", total, "€");
		}
		System.out.printf("|%70s %10.2f %1s|\n", "IVA(21%): ", total * IVA, "€");
		System.out.printf("|%70s %10.2f %1s|\n", "Total IVA Incl.: ", total * (1 - dto) * (1 + IVA), "€");
		System.out.println("                            **Gracias por su compra**");
	}

}