package eu.eurogestion.ese.services;

import java.math.BigInteger;

import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import eu.eurogestion.ese.contracts.BlockChainGasProvider;
import eu.eurogestion.ese.contracts.DocumentosBlockChain;
import eu.eurogestion.ese.domain.Documento;
import eu.eurogestion.ese.exception.EseException;
import eu.eurogestion.ese.utils.Constantes;
import eu.eurogestion.ese.utils.Utiles;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BlockChainServiceImpl implements BlockChainService {

	/** Repositories & Services **/

	/** Functions **/
	@Override
	public void uploadDocumento(Documento documento) throws EseException {

		Web3j w3 = Web3j.build(new HttpService(Constantes.BC_URL_SERVER));
		String privateKey = Constantes.BC_CREDENTIALS;
		Credentials credentials = Credentials.create(privateKey);
		String contractAddress = Constantes.BC_CONTRACT_ADRESS;

		DocumentosBlockChain contrato = DocumentosBlockChain.load(contractAddress, w3, credentials,
				new BlockChainGasProvider());

		try {
			BigInteger idDocumento = new BigInteger(documento.getIdDocumento().toString());
			contrato.addDocumento(idDocumento, documento.getHashDocumento()).send();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new EseException(Utiles.obtenerErrorBlockChain(e.getMessage()));
		}

		// logs de tiempos en el metodo
// 		TransactionReceipt prueba;
//		log.info("inicio metodo: " + new Date().toString());
//		
//		for (LogsEventResponse logBC : contrato.getLogsEvents(prueba)) {
//			String fecha = new Date(logBC.tiempo.longValue() * 1000).toString();
//			log.info(logBC.lugar + " : " + fecha);
//		}
//
//		log.info("fin metodo: " + new Date().toString());
	}

	@Override
	public String downloadDocumento(String idDocumento) throws EseException {

		Web3j w3 = Web3j.build(new HttpService(Constantes.BC_URL_SERVER));
		String privateKey = Constantes.BC_CREDENTIALS;
		Credentials credentials = Credentials.create(privateKey);
		String contractAddress = Constantes.BC_CONTRACT_ADRESS;

		DocumentosBlockChain contrato = DocumentosBlockChain.load(contractAddress, w3, credentials,
				new DefaultGasProvider());

		String hash;
		try {
			hash = contrato.getHashDocumento(new BigInteger(idDocumento)).send();
		} catch (Exception e) {
			throw new EseException(e.getMessage());
		}

		return hash;
	}
}
