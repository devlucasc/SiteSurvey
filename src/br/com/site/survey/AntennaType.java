package br.com.site.survey;

public enum AntennaType {
	BIDIRECTIONAL(1, "Bidirectional Antenna"),
	OMNIDIRECTIONAL(2, "Omnidirectional Antenna");

	private final int valor;
	private final String description;
	AntennaType(int valorOpcao, String descricao) {
		valor = valorOpcao;
		description = descricao;
	}

	public double getValor() {
		return valor;
	}

	public String getDescription() {
		return description;
	}
}
