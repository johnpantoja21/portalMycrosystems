package com.psicovirtual.liquidadorAdminTotal.vista.mb;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlForm;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import co.bmsoft.components.readfiles.ReadFilesComp;
import co.bmsoft.components.readfiles.parameters.ReadFilesParams;
import co.bmsoft.components.readfiles.parameters.ReadFilesParams.FileType;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.psicovirtual.estandar.modelo.utilidades.Parametros;
import com.psicovirtual.estandar.modelo.utilidades.UtilidadesModel;
import com.psicovirtual.estandar.vista.mb.MBMensajes;
import com.psicovirtual.estandar.vista.utilidades.AccesoPropiedadesVista;
import com.psicovirtual.liquidadorAdminTotal.vista.delegado.DNAfiliado;
import com.psicovirtual.liquidadorAdminTotal.vista.delegado.DNCargueArchivos;
import com.psicovirtual.procesos.modelo.ejb.entity.procesos.CargueArchivo;
import com.psicovirtual.procesos.modelo.ejb.entity.procesos.Inconsistencias;
import com.sun.faces.renderkit.html_basic.HtmlBasicRenderer.Param;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Long;
import java.lang.Double;
import java.lang.Integer;
import java.lang.Byte;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.naming.InitialContext;
import javax.sql.DataSource;

@ManagedBean(name = "MBCargueArchivos")
@SessionScoped
public class MBCargueArchivos {
	MBMensajes mensajes = new MBMensajes();
	private UploadedFile archivoClientes;

	private File fileClientes;
	DNAfiliado dnAfiliado;

	DNCargueArchivos dNCargueArchivos;
	List<Inconsistencias> listaInconsistencias;
	
	public boolean  subirArchivo;
	
	public MBCargueArchivos() {
		subirArchivo=true;
	}
	
	
	
	
	

	public void handleFileUploadCliente(FileUploadEvent event) throws Exception {
		String carpetaArchivos = null;
		OutputStream outputStream = null;
		InputStream inputStream = null;
		int bufferSize = 5048;
		AccesoPropiedadesVista prop = null;
//		try {
			prop = new AccesoPropiedadesVista();
			carpetaArchivos = prop.getParametro("RUTA_ARCHIVOS_CARGADOS");

			System.out.println("Carpeta " + carpetaArchivos);
			archivoClientes = event.getFile();
			// Leemos el archivo
			fileClientes = new File(carpetaArchivos + archivoClientes.getFileName());
			outputStream = new FileOutputStream(fileClientes);
			inputStream = archivoClientes.getInputstream();
			byte bufferData[] = new byte[bufferSize];
			int bytesWrite;
			while ((bytesWrite = inputStream.read(bufferData)) > 0)
				outputStream.write(bufferData, 0, bytesWrite);

			outputStream.close();
			inputStream.close();
			
		//	subirArchivo=false;

//		} catch (Exception exception) {
//			System.out.println(exception);
//		}
	}

	public void procesarArchivo() throws Exception {

//		try {
			if (archivoClientes != null) {
				guardarArchivoClientes();
				archivoClientes = null;

				fileClientes = null;

			}

//		} catch (Exception exception) {
//			System.out.println(exception);
//		}

	}

	
	public void limpiar() {
		System.out.println("limpia ");
		archivoClientes = null;
		//
		
						fileClientes = null;
						
						listaInconsistencias = new ArrayList<Inconsistencias>();
	}
	
	public void validarArchivo() throws Exception {
System.out.println("valid");
//		try {
			if (archivoClientes != null) {
				validarArchivoClientes();
//				archivoClientes = null;
//
//				fileClientes = null;

			}

//		} catch (Exception exception) {
//			System.out.println(exception);
//		}

	}

	public void validarArchivoClientes()  {

		HashMap respuestaClientes = null;
		AccesoPropiedadesVista prop = null;
		String carpetaArchivos = null;
		String mensajeCargados = "";
		String mensajeInconsistencias = "";
		Integer clientesCargados = null;

	try {
			if (dNCargueArchivos == null) {
				dNCargueArchivos = new DNCargueArchivos();
			}

			if (dnAfiliado == null) {
				dnAfiliado = new DNAfiliado();
			}
			listaInconsistencias = new ArrayList<Inconsistencias>();

			prop = new AccesoPropiedadesVista();

			String camposNumericos = prop.getParametro("CAMPOS_NUMERICOS");
			boolean[] posicionesNumericos = UtilidadesModel.convertirPropiedades(camposNumericos.split(","));

			String longitudCampos = prop.getParametro("LONGITUD_CAMPOS");

			String[] longitudCamposDatos = longitudCampos.split(",");

			String camposObligatorios = prop.getParametro("CAMPOS_OBLIGATORIOS");

			boolean[] posicionesRequeridos = UtilidadesModel.convertirPropiedades(camposObligatorios.split(","));

			String posicionEmail = prop.getParametro("POSICION_EMAIL");

			int posEmail = Integer.parseInt(posicionEmail);

			
			
			
			String camposFechas = prop.getParametro("CAMPOS_FECHA");
			
			

			boolean[] camposFechasRequeridos = UtilidadesModel.convertirPropiedades(camposFechas.split(","));

			String formatoFecha = prop.getParametro("FORMATO_FECHA");

			System.out.println("formato fehca " + camposFechas);

			List<Vector> registros = null;

			carpetaArchivos = prop.getParametro("RUTA_ARCHIVOS_CARGADOS");

		
			
			String nombreTitulos = prop.getParametro("NOMBRE_TITULOS");

			String[] nombreTitulosDatos = nombreTitulos.split(",");
			

			// Leemos el archivo
			FileType tipo = getFileType(archivoClientes.getFileName());

			int filaInicial = Integer.parseInt(prop.getParametro("FILA_INICIAL_CLIENTES"));
			int cantidadLote = Integer.parseInt(prop.getParametro("NUMERO_ARCHIVOS_CARGUE")) + 1;

			// while (true) {

			HashMap parametros = new HashMap();
			parametros.put(ReadFilesParams.COLUMNA_INICIO,
					Integer.parseInt(prop.getParametro("COLUMNA_INICIO_CLIENTES")));
			parametros.put(ReadFilesParams.COLUMNA_FINAL,
					Integer.parseInt(prop.getParametro("COLUMNA_FINAL_CLIENTES")));
			parametros.put(ReadFilesParams.FILA_INICIAL, filaInicial);
			parametros.put(ReadFilesParams.FORMATO_FECHA, prop.getParametro("FORMATO_FECHA"));

			System.out.println("casa " + prop.getParametro("NUMERO_ARCHIVOS_CARGUE"));

			// parametros.put(ReadFilesParams.FILA_FINAL, cantidadLote);

			registros = ReadFilesComp.readFile(carpetaArchivos + archivoClientes.getFileName(), tipo, parametros);

			// for (int i = 0; i < registros.size(); i++) {
			// registros.get(i);
			// System.out.println("dato2 "+registros.get(i).get(0));
			// //String[] longitudCamposDatos =registros.get(i).toString().split(",");
			// //System.out.println("dato " +longitudCamposDatos[0]);
			//
			// }

			// System.out.println("ini " + filaInicial);
			// System.out.println("fin " + cantidadLote);
			// if (registros.size() > 0) {
			// filaInicial += Integer.parseInt(prop
			// .getParametro("NUMERO_ARCHIVOS_CARGUE"));
			// cantidadLote += Integer.parseInt(prop
			// .getParametro("NUMERO_ARCHIVOS_CARGUE"));
			//
			// } else {
			//
			// break;
			//
			// }

			// }

			// for (int i = 0; i < posicionesNumericos.length; i++) {
			//
			// System.out.println("campo "+i +" es "+posicionesNumericos[i]);
			// }
			//
			int fila = 0;
			int columna = 0;

			for (int i = 0; i < registros.size(); i++) {
				fila++;
				Vector reg = registros.get(i);
				columna = 0;
				for (int j = 0; j < reg.size(); j++) {

					columna++;
					if (posicionesNumericos[j] == true) {

						try {
							if (reg.get(j) != null && !reg.get(j).equals("")) {
								Long.parseLong(reg.get(j).toString().trim()	);
							}
						} catch (Exception e) {

							System.out.println("Error Se Esperaba Un Numero " + e.getMessage() + " fila " + (fila + 1)
									+ " columna " + (columna));

							Inconsistencias inc = new Inconsistencias();

							inc.setTipo("Se espera campo numérico");
							inc.setValor(e.getMessage());
							inc.setFila((fila + 1) + "");
							inc.setColumna((nombreTitulosDatos[columna-1]) + "");
							listaInconsistencias.add(inc);

						}

					}

				}
			}

			fila = 0;
			columna = 0;

			for (int i = 0; i < registros.size(); i++) {
				fila++;
				Vector reg = registros.get(i);
				columna = 0;
				for (int j = 0; j < reg.size(); j++) {

					columna++;

					if (reg.get(j) != null
							&& reg.get(j).toString().length() > Integer.parseInt(longitudCamposDatos[j])) {

						System.out.println("Error la longitud del campo " + reg.get(j).toString() + " fila "
								+ (fila + 1) + " columna " + (columna) + " excede la permitida de "
								+ longitudCamposDatos[j]);

						Inconsistencias inc = new Inconsistencias();

						inc.setTipo("La longitud del campo es mayor a la permitida (" + longitudCamposDatos[j] + ")");
						inc.setValor(reg.get(j).toString());
						inc.setFila((fila + 1) + "");
						inc.setColumna((nombreTitulosDatos[columna-1]) + "");
						listaInconsistencias.add(inc);
					}

				}
			}

			fila = 0;
			columna = 0;

			for (int i = 0; i < registros.size(); i++) {
				fila++;
				Vector reg = registros.get(i);
				columna = 0;
				for (int j = 0; j < reg.size(); j++) {

					columna++;

					if (posicionesRequeridos[j]

							&& (reg.get(j) == null || reg.get(j).toString().trim().equals(""))

					) {

						System.out.println("Error el campo es requerido en la posicion:  fila " + (fila + 1)
								+ " columna " + (columna));
						
						

						Inconsistencias inc = new Inconsistencias();

						inc.setTipo("El campos es obligatorio");
						inc.setValor("");
						inc.setFila((fila + 1) + "");
						inc.setColumna((nombreTitulosDatos[columna-1]) + "");
						listaInconsistencias.add(inc);

					}

				}
			}

			fila = 0;
			columna = 0;

			for (int i = 0; i < registros.size(); i++) {
				fila++;
				Vector reg = registros.get(i);
				columna = 0;
				for (int j = 0; j < reg.size(); j++) {

					columna++;

					if (camposFechasRequeridos[j] == true) {

						try {
							if (reg.get(j) != null && !reg.get(j).equals("")) {

								SimpleDateFormat formateador = new SimpleDateFormat(formatoFecha);

								formateador.parse(reg.get(j).toString().trim()	);

							}
						} catch (Exception e) {

							System.out.println("Error Se Esperaba Una fecha en la posicion:  fila " + (fila + 1)
									+ " columna " + (columna) + " en el formato " + formatoFecha);
							
							

							Inconsistencias inc = new Inconsistencias();

							inc.setTipo("Se espera una fecha en formato "+ formatoFecha);
							inc.setValor("");
							inc.setFila((fila + 1) + "");
							inc.setColumna((nombreTitulosDatos[columna-1]) + "");
							listaInconsistencias.add(inc);
							

						}

					}

				}
			}
			
			fila = 0;
			columna = 0;
			
			for (int i = 0; i < registros.size(); i++) {
				fila++;
				Vector reg = registros.get(i);
				columna = 0;
				for (int j = 0; j < reg.size(); j++) {

					columna++;

					if (j == posEmail) {
						if (reg.get(j) != null && !reg.get(j).equals("")) {
							
					
						if (!validateEmail(reg.get(j).toString().trim()						)) {

							System.out.println("Error el campo es requerido en la posicion:  fila " + reg.get(j).toString()
									+ " columna " + (columna));
							
							

							Inconsistencias inc = new Inconsistencias();

							inc.setTipo("El Correo no es valido");
							inc.setValor(reg.get(j).toString());
							inc.setFila((fila + 1) + "");
							inc.setColumna((nombreTitulosDatos[columna-1]) + "");
							listaInconsistencias.add(inc);

						}	}

					}

				}
			}
			
			
			

			String carpetaArchivosDestino = prop.getParametro("RUTA_ARCHIVOS_CARGADOS_CLIENTES");

		} catch (Exception exception) {

			mensajes.mostrarMensajeError("Error De tipo de campo, buscar en el excel y corrige este dato:", exception.getMessage());

			System.out.println(exception);
		}
		return;
	}

	
	
	
	

	public  boolean validateEmail(String hex) {
		Pattern pattern;
		Matcher matcher;
		pattern = Pattern.compile(Parametros.EMAIL_PATTERN);
		matcher = pattern.matcher(hex);
		return matcher.matches();
	}
	
	
	public void validarArchivoClientes2() {

		HashMap respuestaClientes = null;
		AccesoPropiedadesVista prop = null;
		String carpetaArchivos = null;
		String mensajeCargados = "";
		String mensajeInconsistencias = "";
		Integer clientesCargados = null;

		try {
			if (dNCargueArchivos == null) {
				dNCargueArchivos = new DNCargueArchivos();
			}

			if (dnAfiliado == null) {
				dnAfiliado = new DNAfiliado();
			}

			prop = new AccesoPropiedadesVista();

			List<Vector> registros = null;

			carpetaArchivos = prop.getParametro("RUTA_ARCHIVOS_CARGADOS");

			// Leemos el archivo
			FileType tipo = getFileType(archivoClientes.getFileName());

			int filaInicial = Integer.parseInt(prop.getParametro("FILA_INICIAL_CLIENTES"));
			int cantidadLote = Integer.parseInt(prop.getParametro("NUMERO_ARCHIVOS_CARGUE")) + 1;

			// while (true) {

			HashMap parametros = new HashMap();
			parametros.put(ReadFilesParams.COLUMNA_INICIO,
					Integer.parseInt(prop.getParametro("COLUMNA_INICIO_CLIENTES")));
			parametros.put(ReadFilesParams.COLUMNA_FINAL,
					Integer.parseInt(prop.getParametro("COLUMNA_FINAL_CLIENTES")));
			parametros.put(ReadFilesParams.FILA_INICIAL, filaInicial);
			parametros.put(ReadFilesParams.FORMATO_FECHA, prop.getParametro("FORMATO_FECHA"));
			System.out.println("casa " + prop.getParametro("NUMERO_ARCHIVOS_CARGUE"));

			// parametros.put(ReadFilesParams.FILA_FINAL, cantidadLote);

			registros = ReadFilesComp.readFile(carpetaArchivos + archivoClientes.getFileName(), tipo, parametros);

			// for (int i = 0; i < registros.size(); i++) {
			// registros.get(i);
			// System.out.println("dato2 "+registros.get(i).get(0));
			// //String[] longitudCamposDatos =registros.get(i).toString().split(",");
			// //System.out.println("dato " +longitudCamposDatos[0]);
			//
			// }

			// System.out.println("ini " + filaInicial);
			// System.out.println("fin " + cantidadLote);
			// if (registros.size() > 0) {
			// filaInicial += Integer.parseInt(prop
			// .getParametro("NUMERO_ARCHIVOS_CARGUE"));
			// cantidadLote += Integer.parseInt(prop
			// .getParametro("NUMERO_ARCHIVOS_CARGUE"));
			//
			// } else {
			//
			// break;
			//
			// }

			// }

			for (int i = 0; i < registros.size(); i++) {

				System.out.println("valida  " + registros.get(i));

			}

			String carpetaArchivosDestino = prop.getParametro("RUTA_ARCHIVOS_CARGADOS_CLIENTES");

		} catch (Exception exception) {
			System.out.println(exception);
		}
		return;
	}

	public boolean validarRegistroCliente(Vector registro, int numRegistro, boolean[] posicionesRequeridas,
			boolean[] posicionesCamposNumericos, int posicionEmail,

			String[] longitudCampos) throws Exception {
		boolean hayInconsistencia = false;

		// VALIDAMOS INCONSISTENCIAS POR FORMATO DE CAMPOS
		// if (validarInconsistenciaFormato(registro, listInconsistencias,
		// posicionesCamposNumericos, posicionEmail, numRegistro, cargue)) {
		// hayInconsistencia = true;
		// }
		//

		// // VALIDAMOS LA INCONSISTENCIA POR CAMPOS REQUERIDOS
		// if (validarCampoRequeridos(registro, posicionesRequeridas,
		// listInconsistencias, numRegistro, cargue)) {
		// hayInconsistencia = true;
		// }

		// // VALIDAMOS LA INCONSISTENCIA POR LONGITUD DE CAMPOS
		// if (validarLongitudCampos(registro, longitudCampos,
		// listInconsistencias, numRegistro, cargue)) {
		// hayInconsistencia = true;
		// }
		//
		// // VALIDAMOS INCONSISTENCIAS POR FORMATO DE CAMPOS
		// if (validarInconsistenciaFormato(registro, listInconsistencias,
		// posicionesCamposNumericos, posicionEmail, numRegistro, cargue)) {
		// hayInconsistencia = true;
		// }

		// SI HAY INCONSISTENCIA NO SE CREA EL REGISTRO
		if (hayInconsistencia) {
			return false;
		}

		return hayInconsistencia;

	}

	public void guardarArchivoClientes() throws Exception {

		HashMap respuestaClientes = null;
		AccesoPropiedadesVista prop = null;
		String carpetaArchivos = null;
		String mensajeCargados = "";
		String mensajeInconsistencias = "";
		Integer clientesCargados = null;

//		try {
			if (dNCargueArchivos == null) {
				dNCargueArchivos = new DNCargueArchivos();
			}

			if (dnAfiliado == null) {
				dnAfiliado = new DNAfiliado();
			}

			prop = new AccesoPropiedadesVista();

			List<Vector> registros = null;

			carpetaArchivos = prop.getParametro("RUTA_ARCHIVOS_CARGADOS");

			// Leemos el archivo
			FileType tipo = getFileType(archivoClientes.getFileName());

			int filaInicial = Integer.parseInt(prop.getParametro("FILA_INICIAL_CLIENTES"));
			int cantidadLote = Integer.parseInt(prop.getParametro("NUMERO_ARCHIVOS_CARGUE")) + 1;

			CargueArchivo cargue = new CargueArchivo();
			cargue.setAfiliado(dnAfiliado.buscarAfiliado(1144167624));
			cargue.setNombreArchivo(archivoClientes.getFileName());
			cargue.setFechaCargue(new Date());

			CargueArchivo cargueCreado = dNCargueArchivos.crearCargueArchivo(cargue);

			int idCargue = dNCargueArchivos.maximoIdCargue();

			System.out.println("CARGUE " + idCargue);

			// while (true) {

			HashMap parametros = new HashMap();
			parametros.put(ReadFilesParams.COLUMNA_INICIO,
					Integer.parseInt(prop.getParametro("COLUMNA_INICIO_CLIENTES")));
			parametros.put(ReadFilesParams.COLUMNA_FINAL,
					Integer.parseInt(prop.getParametro("COLUMNA_FINAL_CLIENTES")));
			parametros.put(ReadFilesParams.FILA_INICIAL, filaInicial);

			System.out.println("casa " + prop.getParametro("NUMERO_ARCHIVOS_CARGUE"));

			// parametros.put(ReadFilesParams.FILA_FINAL, cantidadLote);

			registros = ReadFilesComp.readFile(carpetaArchivos + archivoClientes.getFileName(), tipo, parametros);

			// for (int i = 0; i < registros.size(); i++) {
			// registros.get(i);
			// System.out.println("dato2 "+registros.get(i).get(0));
			// //String[] longitudCamposDatos =registros.get(i).toString().split(",");
			// //System.out.println("dato " +longitudCamposDatos[0]);
			//
			// }

			// System.out.println("ini " + filaInicial);
			// System.out.println("fin " + cantidadLote);
			// if (registros.size() > 0) {
			// filaInicial += Integer.parseInt(prop
			// .getParametro("NUMERO_ARCHIVOS_CARGUE"));
			// cantidadLote += Integer.parseInt(prop
			// .getParametro("NUMERO_ARCHIVOS_CARGUE"));
			//
			// } else {
			//
			// break;
			//
			// }

			// }

			for (int i = 0; i < registros.size(); i++) {
				registros.get(i);
				System.out.println("dato  " + registros.get(i));

			}

			dNCargueArchivos.guardarInformacionCargue(registros, idCargue);

			String carpetaArchivosDestino = prop.getParametro("RUTA_ARCHIVOS_CARGADOS_CLIENTES");

			File fileDestino = new File(
					carpetaArchivosDestino + "CLIENTES-" + idCargue + "." + getExtension(fileClientes.getName()));
			guardarArchivoCargue(fileClientes, fileDestino);

//		} catch (Exception exception) {
//			System.out.println(exception);
//		}
		return;
	}

	public void guardarArchivoCargue(File sourceFile, File destFile) throws Exception {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel origen = null;
		FileChannel destino = null;
		try {
			origen = new FileInputStream(sourceFile).getChannel();
			destino = new FileOutputStream(destFile).getChannel();

			long count = 0;
			long size = origen.size();
			while ((count += destino.transferFrom(origen, count, size - count)) < size)
				;

		} finally {
			if (origen != null) {
				origen.close();

			}
			if (destino != null) {
				destino.close();
			}
		}
	}

	public String getExtension(String filename) {
		int index = filename.lastIndexOf('.');
		if (index == -1) {
			return "";
		} else {
			return filename.substring(index + 1);
		}
	}

	private FileType getFileType(String fileName) throws Exception {
		String[] name = fileName.split("\\.");
		if (name[name.length - 1].equalsIgnoreCase("xlsx")) {
			return FileType.XLSX;
		} else if (name[name.length - 1].equalsIgnoreCase("xls")) {
			return FileType.XLS;
		} else if (name[name.length - 1].equalsIgnoreCase("csv")) {
			return FileType.CSV;
		} else if (name[name.length - 1].equalsIgnoreCase("txt")) {
			return FileType.TEXT;
		}

		return null;
	}

	public MBMensajes getMensajes() {
		return mensajes;
	}

	public void setMensajes(MBMensajes mensajes) {
		this.mensajes = mensajes;
	}

	public UploadedFile getArchivoClientes() {
		return archivoClientes;
	}

	public void setArchivoClientes(UploadedFile archivoClientes) {
		this.archivoClientes = archivoClientes;
	}

	public File getFileClientes() {
		return fileClientes;
	}

	public void setFileClientes(File fileClientes) {
		this.fileClientes = fileClientes;
	}

	public DNAfiliado getDnAfiliado() {
		return dnAfiliado;
	}

	public void setDnAfiliado(DNAfiliado dnAfiliado) {
		this.dnAfiliado = dnAfiliado;
	}

	public DNCargueArchivos getdNCargueArchivos() {
		return dNCargueArchivos;
	}

	public void setdNCargueArchivos(DNCargueArchivos dNCargueArchivos) {
		this.dNCargueArchivos = dNCargueArchivos;
	}

	public List<Inconsistencias> getListaInconsistencias() {
		return listaInconsistencias;
	}

	public void setListaInconsistencias(List<Inconsistencias> listaInconsistencias) {
		this.listaInconsistencias = listaInconsistencias;
	}






	public boolean isSubirArchivo() {
		return subirArchivo;
	}






	public void setSubirArchivo(boolean subirArchivo) {
		this.subirArchivo = subirArchivo;
	}
	
	
	
	

}
