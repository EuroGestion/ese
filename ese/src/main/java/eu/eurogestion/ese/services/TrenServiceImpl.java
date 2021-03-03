package eu.eurogestion.ese.services;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.domain.Tramo;
import eu.eurogestion.ese.domain.Tren;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.pojo.BuscadorTrenJSP;
import eu.eurogestion.ese.pojo.DetalleTrenJSP;
import eu.eurogestion.ese.repository.DocumentoDAO;
import eu.eurogestion.ese.repository.PuntoInfraestructuraDAO;
import eu.eurogestion.ese.repository.TramoDAO;
import eu.eurogestion.ese.repository.TrenDAO;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;

@Service
public class TrenServiceImpl implements TrenService {

	/** Repositories & Services **/

	@Autowired
	public TrenDAO trenDAO;

	@Autowired
	public TramoDAO tramoDAO;
	@Autowired
	public PuntoInfraestructuraDAO puntoInfraestructuraDAO;

	@Autowired
	public BlockChainService blockChainService;

	@Autowired
	public DocumentoDAO documentoDAO;

	/** Functions **/

	@Override
	public void bajaTren(BuscadorTrenJSP buscadorTrenJSP) {

		Tren tren = trenDAO.getOne(Integer.parseInt(buscadorTrenJSP.getIdTren()));
		tren.setFechaBaja(new Date());
		trenDAO.save(tren);
	}

	@Override
	public Tren crearTren(DetalleTrenJSP detalleTrenJSP) throws EseException {

		Tren tren = new Tren();
		tren.setNumero(detalleTrenJSP.getNumeroTren());
		if (detalleTrenJSP.isEsEspecial()) {
			Tramo tramo = new Tramo();
			tramo.setEsEspecial(Boolean.TRUE);
			tramo.setNombre(detalleTrenJSP.getNombre());
			tramo.setPuntoOrigen(puntoInfraestructuraDAO.getOne(Integer.parseInt(detalleTrenJSP.getIdOrigen())));
			tramo.setPuntoDestino(puntoInfraestructuraDAO.getOne(Integer.parseInt(detalleTrenJSP.getIdDestino())));
			tramo = tramoDAO.save(tramo);
			tren.setTramo(tramo);

		} else {
			tren.setTramo(tramoDAO.getOne(Integer.parseInt(detalleTrenJSP.getIdTramo())));
		}
		tren.setHoraInicio(Utiles.parseTimeHorasPantalla(detalleTrenJSP.getHoraInicio()));
		tren.setHoraFin(Utiles.parseTimeHorasPantalla(detalleTrenJSP.getHoraFin()));

		Tren trenDDBB = trenDAO.save(tren);

		return trenDDBB;
	}

	@Override
	public void guardarTren(DetalleTrenJSP detalleTrenJSP) {

		Tren tren = trenDAO.getOne(Integer.parseInt(detalleTrenJSP.getIdTren()));
		tren.setNumero(detalleTrenJSP.getNumeroTren());
		if (detalleTrenJSP.isEsEspecial()) {
			Tramo tramo = tren.getTramo();
			tramo.setNombre(detalleTrenJSP.getNombre());
			tramo.setPuntoOrigen(puntoInfraestructuraDAO.getOne(Integer.parseInt(detalleTrenJSP.getIdOrigen())));
			tramo.setPuntoDestino(puntoInfraestructuraDAO.getOne(Integer.parseInt(detalleTrenJSP.getIdDestino())));
			tramo = tramoDAO.save(tramo);
		} else {
			tren.setTramo(tramoDAO.getOne(Integer.parseInt(detalleTrenJSP.getIdTramo())));
		}
		tren.setHoraInicio(Utiles.parseTimeHorasPantalla(detalleTrenJSP.getHoraInicio()));
		tren.setHoraFin(Utiles.parseTimeHorasPantalla(detalleTrenJSP.getHoraFin()));

		trenDAO.save(tren);
	}

	@Override
	public void anadirDocumentoTren(DetalleTrenJSP detalleTrenJSP) throws EseException {
		Tren tren = trenDAO.getOne(Integer.parseInt(detalleTrenJSP.getIdTren()));
		byte[] fichero;
		try {
			fichero = detalleTrenJSP.getDocumento().getBytes();
		} catch (IOException e) {
			throw new EseException(e.getMessage());
		}

		String md5 = Utiles.calculateHashMD5(fichero);
		Documento documento = new Documento();
		documento.setFechaDocumento(Utiles.sysdate());
		documento.setFechaSubida(Utiles.sysdate());
		documento.setHashDocumento(md5);

		Blob aux;
		try {
			aux = new SerialBlob(fichero);
		} catch (SQLException e) {
			throw new EseException(e.getMessage());
		}

		documento.setFichero(aux);
		documento.setTipoFichero(detalleTrenJSP.getDocumento().getContentType());
		documento.setTitulo(Constantes.TITULO_DOCUMENTO_TREN);
		documentoDAO.save(documento);

		tren.setDocumento(documento);

		trenDAO.save(tren);

		blockChainService.uploadDocumento(documento);
	}
}
