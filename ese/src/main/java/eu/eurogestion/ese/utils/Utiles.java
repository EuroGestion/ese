package eu.eurogestion.ese.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.util.DigestUtils;

/**
 * @author Rmerino
 *
 */

public class Utiles {

	/**
	 * Metodo que convierte una fecha en String dado un patron
	 * 
	 * @param fecha
	 * @param pattern
	 * @return
	 */
	public static String convertDateToString(Date fecha, String pattern) {
		return new SimpleDateFormat(pattern).format(fecha);
	}

	public static Date convertStringToDate(String fecha, String pattern) {

		try {
			return new SimpleDateFormat(pattern).parse(fecha);
		} catch (ParseException e) {
			return null;
		}

	}

	/**
	 * Metodo que comprueba si el string introducido es un numero
	 * 
	 * @param numero
	 * @return
	 */
	public static boolean esNumero(String numero) {
		try {
			Integer.parseInt(numero);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * Metodo para comprobar que un DNI es correcto
	 * 
	 * @param dniEntrada
	 * @return
	 */
	public static boolean dniValido(String dniEntrada) {

		String dni = dniEntrada.toUpperCase();
		Pattern pattern = Pattern.compile("(\\d{8})([A-Z]{1})");
		Matcher matcher = pattern.matcher(dni);
		if (matcher.matches()) {
			String letra = matcher.group(2);
			String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
			int index = Integer.parseInt(matcher.group(1));
			index = index % 23;
			String reference = letras.substring(index, index + 1);
			if (reference.equalsIgnoreCase(letra)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Metodo que devuelve una fecha si es correcta o un null si la fecha esta mal
	 * 
	 * @param fecha
	 * @return
	 */
	public static Date parseDatePantalla(String fecha) {

		try {
			return new SimpleDateFormat(Constantes.FORMATO_FECHA_PANTALLA).parse(fecha);
		} catch (ParseException e) {
			return null;
		}

	}

	/**
	 * Metodo que devuelve una fecha si es correcta o un null si la fecha esta mal
	 * 
	 * @param fecha
	 * @return
	 */
	public static Date parseTimeHorasPantalla(String fecha) {

		try {
			return new SimpleDateFormat(Constantes.FORMATO_FECHA_HORAS_PANTALLA).parse(fecha);
		} catch (ParseException e) {
			return null;
		}

	}

	/**
	 * Metodo que devuelve una fecha si es correcta o un null si la fecha esta mal
	 * 
	 * @param fecha
	 * @return
	 */
	public static String formatStringHorasPantalla(Date fecha) {

		return new SimpleDateFormat(Constantes.FORMATO_FECHA_HORAS_PANTALLA).format(fecha);

	}

	public static String getYear(Date fecha) {

		return new SimpleDateFormat(Constantes.FORMATO_FECHA_ANNO).format(fecha);

	}

	public static String calculateHashMD5(byte[] file) {
		return DigestUtils.md5DigestAsHex(file);
	}

	public static String cifrarPassword(String password) {

		Keccak.Digest256 digest256 = new Keccak.Digest256();
		byte[] hashbytes = digest256.digest(password.getBytes(StandardCharsets.UTF_8));
		return new String(Hex.encode(hashbytes));
	}

	public static String crearPassword() {
		return RandomStringUtils.randomAlphanumeric(8);

	}

	public static Date sysdate() {

		return parseDatePantalla(convertDateToString(new Date(), Constantes.FORMATO_FECHA_PANTALLA));
	}

	public static Map<String, List<String>> obtenerMapOpciones() {
		Map<String, List<String>> mapaOpciones = new HashMap<>();
		// PERMISOS BASICOS
		mapaOpciones.put(Constantes.OPCION_PANTALLA_TAREAS_PENDIENTES,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_TAREAS_PENDIENTES }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_PERMISOS,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_PERMISOS }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_PERSONAL,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_PERSONAL }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_COMPANIAS,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_COMPANIAS }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_TRENES,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_TRENES }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_CURSOS,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_CURSOS }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_REVISIONES,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_REVISIONES }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_TITULOS,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_TITULOS }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_INSPECCIONES,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_INSPECCIONES }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_ACCIDENTES,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_ACCIDENTES }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_HOMOLOGACION,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_HOMOLOGACION }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_PASF,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_PASF }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_GESTION_DE_TURNOS,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_GESTION_DE_TURNOS }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_TOMA_DE_SERVICIO,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_TOMA_DE_SERVICIO }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_COMPOSICION,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_COMPOSICION }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_PERMISOS,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_PERMISOS }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_MATERIALES,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_MATERIALES }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_MODELO_MATERIAL,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_MODELO_MATERIAL }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_LIBROS,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_LIBROS }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_TRAMOS,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_TRAMOS }));

		// MENUS
		mapaOpciones.put(Constantes.OPCION_PANTALLA_ADMINISTRACION,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_PERSONAL, Constantes.OPCION_PANTALLA_COMPANIAS,
						Constantes.OPCION_PANTALLA_TRENES, Constantes.OPCION_PANTALLA_COMPOSICION,
						Constantes.OPCION_PANTALLA_PERMISOS, Constantes.OPCION_PANTALLA_MATERIALES,
						Constantes.OPCION_PANTALLA_MODELO_MATERIAL, Constantes.OPCION_PANTALLA_TRAMOS }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_FORMACION_Y_TITULO_HABILITANTE, Arrays
				.asList(new String[] { Constantes.OPCION_PANTALLA_CURSOS, Constantes.OPCION_PANTALLA_REVISIONES }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_GESTION_PROCEDIMIENTOS,
				Arrays.asList(new String[] { Constantes.OPCION_PANTALLA_GESTION_DE_TURNOS,
						Constantes.OPCION_PANTALLA_TOMA_DE_SERVICIO, Constantes.OPCION_PANTALLA_LIBROS }));
		mapaOpciones.put(Constantes.OPCION_PANTALLA_GESTION_MATERIALES, Arrays.asList(
				new String[] { Constantes.OPCION_PANTALLA_MATERIALES, Constantes.OPCION_PANTALLA_MODELO_MATERIAL }));

		return mapaOpciones;
	}

	public static byte[] convertBlobTobyteArray(Blob blob) {
		try {
			int blobLength = (int) blob.length();
			return blob.getBytes(1, blobLength);
		} catch (SQLException e) {
			return new byte[0];
		}
	}

	public static Long obtenerPaginasTotales(Long totalElementos, int sizeTabla) {
		Long totalPaginas = totalElementos / sizeTabla;
		Long restoTotalPaginas = totalElementos % sizeTabla;
		if (restoTotalPaginas > 0) {
			totalPaginas++;
		}

		return totalPaginas;
	}

	public static String obtenerErrorBlockChain(String errorCompleto) {

		String error = errorCompleto.split("Revert reason:")[1].split("0x")[1].replace("'", "").replace(".", "");
		String causaError = error.substring(error.length() - 64, error.length());
		byte[] errorByte = Hex.decode(causaError);
		String causaErrorTexto = "";
		try {
			causaErrorTexto = new String(errorByte, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return causaErrorTexto;
	}
}
