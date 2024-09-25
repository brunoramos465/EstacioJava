package entities;

import java.io.Serializable;
import java.util.Objects;

public class Pessoa implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Long cpfCnpj;
	private Long contrato;
			
	public Pessoa() {
		
	}

	public Pessoa(Long cpfCnpj, Long contrato) {
		super();
		this.cpfCnpj = cpfCnpj;
		this.contrato = contrato;
	}



	public Long getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(Long cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

	public Long getContrato() {
		return contrato;
	}

	public void setContrato(Long contrato) {
		this.contrato = contrato;
	}
	
	
	

	@Override
	public int hashCode() {
		return Objects.hash(contrato, cpfCnpj);
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pessoa other = (Pessoa) obj;
		return Objects.equals(contrato, other.contrato) && Objects.equals(cpfCnpj, other.cpfCnpj);
	}



	



	
	
	

}
