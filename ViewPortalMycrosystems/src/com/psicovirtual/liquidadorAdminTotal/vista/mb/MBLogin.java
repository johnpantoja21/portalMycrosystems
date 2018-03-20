package com.psicovirtual.liquidadorAdminTotal.vista.mb;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.psicovirtual.estandar.vista.mb.MBMensajes;

@ManagedBean(name = "MBLogin")
@SessionScoped
public class MBLogin {
	MBMensajes mensajes = new MBMensajes();

	public MBLogin(){

	}

	public void navegarControl() {

		try {
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext extContext = context.getExternalContext();
			String url2 = extContext.encodeActionURL(
					context.getApplication().getViewHandler().getActionURL(context, "/view/gestion/bienvenido.xhtml"));
			extContext.redirect(url2);
		} catch (Exception exception) {
			// TODO: Add catch code
			exception.printStackTrace();
		}

	}
	
	
	
	public void cerrarSesion() {

		try {
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext extContext = context.getExternalContext();
			String url2 = extContext.encodeActionURL(
					context.getApplication().getViewHandler().getActionURL(context, "/view/index.xhtml"));
			extContext.redirect(url2);
		} catch (Exception exception) {
			// TODO: Add catch code
			exception.printStackTrace();
		}

	}
	
	
	
	
	
	
	
	

	public MBMensajes getMensajes() {
		return mensajes;
	}

	public void setMensajes(MBMensajes mensajes) {
		this.mensajes = mensajes;
	}

}
