package entities;

import java.io.Serializable;
import java.util.Objects;

import org.openqa.selenium.WebElement;

public class Acordo implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	
	private String tipoPessoa;
	private String nome;
	private Integer idProduto;
	private Double valorProduto;
	private String status;

	
	public Acordo() {
		
	}


	public Acordo(String tipoPessoa, String nome, Integer idProduto, Double valorProduto, String status) {
		super();
		this.tipoPessoa = tipoPessoa;
		this.nome = nome;
		this.idProduto = idProduto;
		this.valorProduto = valorProduto;
		this.status = status;
	}


	public String getTipoPessoa() {
		return tipoPessoa;
	}


	public void setTipoPessoa(String tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public Integer getIdProduto() {
		return idProduto;
	}


	public void setIdProduto(Integer idProduto) {
		this.idProduto = idProduto;
	}


	public Double getValorProduto() {
		return valorProduto;
	}


	public void setValorProduto(Double valorProduto) {
		this.valorProduto = valorProduto;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	@Override
	public int hashCode() {
		return Objects.hash(idProduto, nome, status, tipoPessoa, valorProduto);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Acordo other = (Acordo) obj;
		return Objects.equals(idProduto, other.idProduto) && Objects.equals(nome, other.nome)
				&& Objects.equals(status, other.status) && Objects.equals(tipoPessoa, other.tipoPessoa)
				&& Objects.equals(valorProduto, other.valorProduto);
	}

	

	}