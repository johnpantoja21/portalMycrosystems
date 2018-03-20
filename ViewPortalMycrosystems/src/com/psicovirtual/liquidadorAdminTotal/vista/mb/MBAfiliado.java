package com.psicovirtual.liquidadorAdminTotal.vista.mb;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.psicovirtual.estandar.vista.mb.MBMensajes;
import com.psicovirtual.liquidadorAdminTotal.vista.delegado.DNAfiliado;
import com.psicovirtual.procesos.modelo.ejb.entity.procesos.Afiliado;

@ManagedBean(name = "MBAfiliado")
@SessionScoped
public class MBAfiliado {
	MBMensajes mensajes = new MBMensajes();

	DNAfiliado dnAfiliado;
	Afiliado afiliado;
	List<Afiliado> listAfiliados;
	Afiliado afiliadoSeleccionado;	
	
	
	public MBAfiliado(){
		
		try {
			afiliado = new Afiliado();
			afiliadoSeleccionado= new Afiliado();
			listarAfiliados();
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	public void guardarAfiliado() throws Exception {		
		if (dnAfiliado==null) {
			dnAfiliado= new DNAfiliado();
		}
		dnAfiliado.crearAfiliado(afiliado);		
		
		
		mensajes.mostrarMensajeOk("Registro Exitoso.");
		
		
		listAfiliados=dnAfiliado.listaAfiliado();
	}
	
	
	
	
	public void modificarAfiliado() throws Exception {		
		if (dnAfiliado==null) {
			dnAfiliado= new DNAfiliado();
		}
		
		
//		Afiliado afiliado = dnAfiliado.buscarAfiliado(afiliadoSeleccionado.getNit());
//		System.out.println("nom "+ afiliadoSeleccionado.getNombre());
//		
//		afiliado.setNombre(afiliadoSeleccionado.getNombre());
//		
		
		
		
		dnAfiliado.modificarAfiliado(afiliadoSeleccionado);		
		
		
		mensajes.mostrarMensajeOk("Modificacion Exitosa.");
		
		
		listAfiliados=dnAfiliado.listaAfiliado();
	}
	
	
	
	
	
	
	
	
	
	public void listarAfiliados() throws Exception {
		
		if (dnAfiliado==null) {
			dnAfiliado= new DNAfiliado();
		}
		
		
		listAfiliados=dnAfiliado.listaAfiliado();
	}

	
	public void cargarModificar() {
		
		System.out.println("dato "+afiliadoSeleccionado.getNombre());
		
		
	}
	
	
	
	

	public void limpiar()   {
		
		afiliado = new Afiliado();
		
		afiliadoSeleccionado= new Afiliado();
	}


	
	
	
	
	
	
	
	
	

	public MBMensajes getMensajes() {
		return mensajes;
	}



	public void setMensajes(MBMensajes mensajes) {
		this.mensajes = mensajes;
	}



	public DNAfiliado getDnAfiliado() {
		return dnAfiliado;
	}



	public void setDnAfiliado(DNAfiliado dnAfiliado) {
		this.dnAfiliado = dnAfiliado;
	}



	public Afiliado getAfiliado() {
		return afiliado;
	}



	public void setAfiliado(Afiliado afiliado) {
		this.afiliado = afiliado;
	}



	public List<Afiliado> getListAfiliados() {
		return listAfiliados;
	}



	public void setListAfiliados(List<Afiliado> listAfiliados) {
		this.listAfiliados = listAfiliados;
	}



	public Afiliado getAfiliadoSeleccionado() {
		return afiliadoSeleccionado;
	}



	public void setAfiliadoSeleccionado(Afiliado afiliadoSeleccionado) {
		this.afiliadoSeleccionado = afiliadoSeleccionado;
	}
	
	
	
	
	
	
	
	

}
