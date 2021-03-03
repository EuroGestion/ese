package eu.eurogestion.ese.domain;

import java.util.Date;

import lombok.Data;

@Data
public class EseToken {

	private String usuarioDID;
	private String documentoId;
	private Date firmaFecha;
}
