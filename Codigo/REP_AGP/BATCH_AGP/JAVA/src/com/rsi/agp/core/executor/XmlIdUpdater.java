package com.rsi.agp.core.executor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Clase para actualizar el campo <ID> autonumérico en un fichero XML.
 * Recibe como parámetros el fichero xml a actualizar y el primer identificador. Por cada ocurrencia
 * del atributo ID se va incrementando el identificador de uno en uno.
 * @author U028783
 *
 */
public class XmlIdUpdater {
	
	static String fileName;
	
	private static final String IDTAG_STR = "<ID></ID>";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: java "
					+ XmlIdUpdater.class.getName()
					+ " <XML that needs to be updated>" + " <First identifier>");
			System.exit(1);
		}
		try {
			update(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}
	
	private static void update(String[] args) throws IOException {
		File fichero = new File(args[0]);
		try (FileInputStream xmlIn = new FileInputStream(fichero)) {
			int contador = Integer.parseInt(args[1]);
			if (args.length == 2) {
				byte[] bytes = new byte[Integer.parseInt(fichero.length() + "")];
				@SuppressWarnings("unused")
				int numbytes = xmlIn.read(bytes);
				String cad = new String(bytes);
				// Para la tabla de características grupo tasa: genero un id autonumerico
				if (cad.indexOf(IDTAG_STR) >= 0) {
					while (cad.indexOf(IDTAG_STR) >= 0) {
						contador++;
						String replacement = "<ID>" + contador + "</ID>";
						cad = cad.replaceFirst(IDTAG_STR, replacement);
					}
					try (FileOutputStream fos = new FileOutputStream(fichero)) {
						fos.write(cad.getBytes());
					}
				} else {
					System.out.println("No hay ids para actualizar en el fichero " + args[0]);
				}
			}
		}
	}
}