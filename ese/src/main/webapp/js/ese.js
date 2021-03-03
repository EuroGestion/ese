function cargarProvinciasRegister() {
	var formulario = document.getElementById("reg");
	formulario.action = getContextPath() + "/cargarProvinciasRegister";
	formulario.submit();
}
function cargarLocalidadesRegister() {
	var formulario = document.getElementById("reg");
	formulario.action = getContextPath() + "/cargarLocalidadesRegister";
	formulario.submit();
}
function cargarProvinciasModificarRegister() {
	var formulario = document.getElementById("reg");
	formulario.action = getContextPath() + "/cargarProvinciasModificarRegister";
	formulario.submit();
}
function cargarLocalidadesModificarRegister() {
	var formulario = document.getElementById("reg");
	formulario.action = getContextPath()
			+ "/cargarLocalidadesModificarRegister";
	formulario.submit();
}
function cargarProvinciasRegisterCompania() {
	var formulario = document.getElementById("reg");
	formulario.action = getContextPath() + "/cargarProvinciasRegisterCompania";
	formulario.submit();
}
function cargarLocalidadesRegisterCompania() {
	var formulario = document.getElementById("reg");
	formulario.action = getContextPath() + "/cargarLocalidadesRegisterCompania";
	formulario.submit();
}

function cargarProvinciasModificarRegisterCompania() {
	var formulario = document.getElementById("reg");
	formulario.action = getContextPath()
			+ "/cargarProvinciasModificarRegisterCompania";
	formulario.submit();
}
function cargarLocalidadesModificarRegisterCompania() {
	var formulario = document.getElementById("reg");
	formulario.action = getContextPath()
			+ "/cargarLocalidadesModificarRegisterCompania";
	formulario.submit();
}

function seleccionarCurso() {
	var formulario = document.getElementById("programarFormacion");
	formulario.action = getContextPath() + "/seleccionarCurso";
	formulario.submit();
}

function cambiarTipoTitulo() {
	var formulario = document.getElementById("habilitar");
	formulario.action = getContextPath() + "/seleccionarTipoTitulo";
	formulario.submit();
}

function seleccionarTren() {
	var formulario = document.getElementById("programarInspeccion");
	formulario.action = getContextPath() + "/seleccionarTren";
	formulario.submit();
}

function recargarProgramarInspeccion() {
	var formulario = document.getElementById("programarInspeccion");
	formulario.action = getContextPath() + "/recargarProgramarInspeccion";
	formulario.submit();
}

function seleccionarTrenModInspeccion() {
	var formulario = document.getElementById("modificarInspeccion");
	formulario.action = getContextPath() + "/seleccionarTrenModInspeccion";
	formulario.submit();
}



function seleccionarEspecialTren() {
	var formulario = document.getElementById("detalleTren");
	formulario.action = getContextPath()
			+ "/seleccionarEspecialTren";
	formulario.submit();
}
function getContextPath() {
	return window.location.pathname.substring(0, window.location.pathname
			.indexOf("/", 2));
}

function seleccionarResolucionRevisionPsico() {

	var validoDesde = document.getElementById("validoDesde");
	var fechaCaducidad = document.getElementById("fechaCaducidad");

	if (document.getElementById("resolucion1").checked) {
		validoDesde.setAttribute("required", "");
		fechaCaducidad.setAttribute("required", "");

		validoDesde.removeAttribute("disabled", "");
		fechaCaducidad.removeAttribute("disabled", "");
	} else {
		validoDesde.removeAttribute("required", "");
		fechaCaducidad.removeAttribute("required", "");

		validoDesde.setAttribute("disabled", "");
		fechaCaducidad.setAttribute("disabled", "");
	}
}

function seleccionarResolucionModificarCursoAlumno() {

	var evidencia = document.getElementById("evidencia");
	var fechaDocumento = document.getElementById("fechaDocumento");
	var fechaCaducidad = document.getElementById("fechaCaducidad");
	var validoDesde = document.getElementById("validoDesde");

	if (document.getElementById("resolucion1").checked) {
		evidencia.setAttribute("required", "");
		fechaDocumento.setAttribute("required", "");
		fechaCaducidad.setAttribute("required", "");
		validoDesde.setAttribute("required", "");
	} else {
		evidencia.removeAttribute("required", "");
		fechaDocumento.removeAttribute("required", "");
		fechaCaducidad.removeAttribute("required", "");
		validoDesde.removeAttribute("required", "");
	}
}

function openNav() {
	document.getElementById("mySidenav").style.width = "auto";
}

/* Set the width of the side navigation to 0 */
function closeNav() {
	document.getElementById("mySidenav").style.width = "0";
}

function abrirCerrarSubmenuFormacion() {
	var elemento = document.getElementById("submenuFormacion");
	if (elemento.hidden) {
		elemento.removeAttribute("hidden", "");
	} else {
		elemento.setAttribute("hidden", "");
	}

}

function abrirCerrarSubmenuRevision() {
	var elemento = document.getElementById("submenuRevision");
	if (elemento.hidden) {
		elemento.removeAttribute("hidden", "");
	} else {
		elemento.setAttribute("hidden", "");
	}
}

function abrirCerrarMenuFormacionYTitulos() {

	var elemento = document.getElementById("menuFormacion");
	var elemento2 = document.getElementById("menuRevision");
	if (elemento.hidden) {
		elemento.removeAttribute("hidden", "");
		elemento2.removeAttribute("hidden", "");
	} else {
		elemento.setAttribute("hidden", "");
		elemento2.setAttribute("hidden", "");
	}

}

function abrirCerrarMenuAdministracion() {

	var elemento = document.getElementById("menuAdministracion");
	if (elemento.hidden) {
		elemento.removeAttribute("hidden", "");
	} else {
		elemento.setAttribute("hidden", "");
	}

}

function abrirCerrarMenuGestionProcedimientos() {

	var elemento = document.getElementById("menuGestionProcedimientos");
	if (elemento.hidden) {
		elemento.removeAttribute("hidden", "");
	} else {
		elemento.setAttribute("hidden", "");
	}

}

function abrirCerrarMenuInspeccionesYControlesDeSeguridad() {

	var elemento = document
			.getElementById("menuInspeccionesYControlesDeSeguridad");
	if (elemento.hidden) {
		elemento.removeAttribute("hidden", "");
	} else {
		elemento.setAttribute("hidden", "");
	}

}

function abrirCerrarMenuSucesoFerroviario() {

	var elemento = document.getElementById("menuSucesoFerroviario");
	if (elemento.hidden) {
		elemento.removeAttribute("hidden", "");
	} else {
		elemento.setAttribute("hidden", "");
	}

}

function ocultarYMostrarOpcionMenu(opcionOcultar, opcionMostrar) {

	var elementoOcultar = document.getElementById(opcionOcultar);
	var elementoMostrar = document.getElementById(opcionMostrar);

	elementoMostrar.removeAttribute("hidden", "");
	elementoOcultar.setAttribute("hidden", "");

}

function mostrarOcultar(opcion, required) {

	if (document.getElementById(opcion + "1").checked) {
		document.getElementById(opcion + "Seleccion").removeAttribute("hidden",
				"");
		document.getElementById(required).setAttribute("required", "")

	} else {
		document.getElementById(opcion + "Seleccion")
				.setAttribute("hidden", "");
		document.getElementById(required).removeAttribute("required", "");

	}

}

function validarSubidaArchivo(nombreArchivo, required, nombreDescripcion) {

	var archivo = document.getElementById(nombreArchivo);
	archivo.setCustomValidity("");
	if (!required && archivo.value.trim() == "") {
		return true;
	}

	if (required && archivo.value.trim() == "") {
		archivo.setCustomValidity("Selecciona un archivo");
		archivo.reportValidity();
		return false;
	}

	var ext = "." + archivo.value.split(".").pop();
	var extensiones = archivo.accept.replace(/ /g, "").split(",");

	var extensionCorrecta = false;
	for (x = 0; x < extensiones.length; x++) {
		if (ext == extensiones[x]) {
			extensionCorrecta = true;
		}
	}
	if (!extensionCorrecta) {

		archivo.setCustomValidity("Tipo de archivo no permitido");
		archivo.reportValidity();
	}

	if (nombreDescripcion != "") {
		var descripcion = document.getElementById(nombreDescripcion);
		descripcion.setCustomValidity("");
		if (descripcion.value.trim() == "") {
			descripcion
					.setCustomValidity("La descripción no puede estar vacia");
			descripcion.reportValidity();
			return false;
		}

	}

	return extensionCorrecta;
}

function validarCamposObligatorios(campos) {

	for (x = 0; x < campos.length; x++) {
		var archivo = document.getElementById(campos[x]);
		archivo.setCustomValidity("");

		if (archivo.value.trim() == "") {
			if ("select" == archivo.tagName.toLowerCase()) {
				archivo.setCustomValidity("Selecciona un elemento de la lista");
			} else {
				archivo.setCustomValidity("Completa este campo");
			}
			archivo.reportValidity();
			return false;

		}
	}
	return true;

}

function validarGuardarCamposObligatoriosPASF() {
	if (document.getElementById('descarrilamiento') == null) {
		return true;
	}
	limpiarCamposValidacionJSPASF();
	var campos = [ 'descarrilamiento', 'colision', 'accidentePasoNivel',
			'incendio', 'arrollamientoVia', 'arrollamientoInterseccion',
			'caidaPersonas', 'suicidio', 'descomposicion', 'detencion',
			'incidentesTransportesExcepcionales', 'rebaseSennal',
			'conatoColision', 'enganchePantografo', 'otros',
			'cursosFormativos', 'revisionesAptitudPsicofisica', 'iso', 'issc',
			'ismp', 'iseer', 'iseet', 'isrc', 'cad', 'auditorias' ];
	return validarCamposObligatorios(campos);
}

function validarAprovarCamposObligatoriosPASF() {

	limpiarCamposValidacionJSPASF();
	var campos = [ 'descarrilamiento', 'colision', 'accidentePasoNivel',
			'incendio', 'arrollamientoVia', 'arrollamientoInterseccion',
			'caidaPersonas', 'suicidio', 'descomposicion', 'detencion',
			'incidentesTransportesExcepcionales', 'rebaseSennal',
			'conatoColision', 'enganchePantografo', 'otros',
			'cursosFormativos', 'revisionesAptitudPsicofisica', 'iso', 'issc',
			'ismp', 'iseer', 'iseet', 'isrc', 'cad', 'auditorias' ];
	return validarCamposObligatorios(campos);
}

function validarAddCursoCamposObligatoriosPASF() {
	limpiarCamposValidacionJSPASF();
	var campos = [ 'cursoCategoria', 'cursoCargo', 'cursoDescripcion',
			'cursoAsistentes', 'cursoDuracion' ];
	return validarCamposObligatorios(campos);
}

function validarAddInspeccionCamposObligatoriosPASF() {
	limpiarCamposValidacionJSPASF();
	var campos = [ 'inspeccionTipo', 'inspeccionDescripcion',
			'inspeccionNumero', 'inspeccionDuracion' ];
	return validarCamposObligatorios(campos);
}

function validarAddAuditoriaCamposObligatoriosPASF() {
	limpiarCamposValidacionJSPASF();
	var campos = [ 'auditoriaDescripcion', 'auditoriaDuracion' ];
	return validarCamposObligatorios(campos);
}

function limpiarCamposValidacionJSPASF() {
	var campos = [ 'descarrilamiento', 'colision', 'accidentePasoNivel',
			'incendio', 'arrollamientoVia', 'arrollamientoInterseccion',
			'caidaPersonas', 'suicidio', 'descomposicion', 'detencion',
			'incidentesTransportesExcepcionales', 'rebaseSennal',
			'conatoColision', 'enganchePantografo', 'otros',
			'cursosFormativos', 'revisionesAptitudPsicofisica',
			'cursoCategoria', 'cursoCargo', 'cursoDescripcion',
			'cursoAsistentes', 'cursoDuracion', 'iso', 'issc', 'ismp', 'iseer',
			'iseet', 'isrc', 'cad', 'inspeccionTipo', 'inspeccionDescripcion',
			'inspeccionNumero', 'inspeccionDuracion', 'auditorias',
			'auditoriaDescripcion', 'auditoriaDuracion' ];
	limpiarCamposValidacionJS(campos);
}

function limpiarCamposValidacionJS(nombreArchivos) {

	for (x = 0; x < nombreArchivos.length; x++) {
		var archivo = document.getElementById(nombreArchivos[x]);
		archivo.setCustomValidity("");
	}

}

function nobackbutton() {

	window.location.hash = "no-back-button";

	window.location.hash = "Again-No-back-button" // chrome

	window.onhashchange = function() {
		window.location.hash = "no-back-button";
	}

}

function comprobarPrimeraPrueba(limiteAlcohol) {
	var prueba = document.getElementById("primeraPrueba").value;
	var resultado = document.getElementById("resultadoPrimeraPrueba");
	comprobarPrueba(prueba, resultado, limiteAlcohol);

}

function comprobarSegundaPrueba(limiteAlcohol) {
	var prueba = document.getElementById("segundaPrueba").value;
	var resultado = document.getElementById("resultadoSegundaPrueba");
	comprobarPrueba(prueba, resultado, limiteAlcohol);
	comprobarNumero("segundaPrueba");
}

function comprobarPrueba(valorPrueba, resultado, limiteAlcohol) {
	if (valorPrueba.trim() == "" || valorPrueba.trim() == '') {
		resultado.value = "";
	} else if (parseFloat(valorPrueba) > parseFloat(limiteAlcohol)) {
		resultado.value = "Positivo";
	} else {
		resultado.value = "Negativo";
	}

}

function comprobarNumero(nombreCampo) {
	var campo = document.getElementById(nombreCampo);
	var valorCampo = campo.value;
	if (valorCampo.trim() != "" && valorCampo.trim() != '') {
		campo.setAttribute("required", "");
	} else {
		campo.removeAttribute("required", "");

	}

}

function sumaCartaPortes(nombreCampo) {
	var campoCorrecto = document.getElementById(nombreCampo + "Correcta");
	var campoIncorrecto = document.getElementById(nombreCampo + "Incorrecta");
	var campoTotal = document.getElementById(nombreCampo + "Total");
	var valorCampoCorrecto = campoCorrecto.value;
	var valorCampoIncorrecto = campoIncorrecto.value;
	var numeroCampoCorrecto;
	var numeroCampoIncorrecto;
	if (valorCampoCorrecto.trim() == "" || valorCampoCorrecto.trim() == '') {
		numeroCampoCorrecto = 0;
	} else {
		numeroCampoCorrecto = parseInt(valorCampoCorrecto);

	}
	if (valorCampoIncorrecto.trim() == "" || valorCampoIncorrecto.trim() == '') {
		numeroCampoIncorrecto = 0;
	} else {
		numeroCampoIncorrecto = parseInt(valorCampoIncorrecto);

	}
	numeroTotal = numeroCampoCorrecto + numeroCampoIncorrecto;
	campoTotal.value = numeroTotal.toString();

}
$(document).ready(
		function() {
			$("form").keypress(
					function(e) {
						if (e.which == 13 && e.target.nodeName != "TEXTAREA"
								&& e.target.form.id != "formularioLogin") {
							return false;
						}
					});
		});


