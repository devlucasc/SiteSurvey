package br.com.site.survey;

/**
 * Wall Material Type Class
 * These attenuation values are obtained from internet
 * @author Lucas Corsi
 *
 */
public enum MaterialType {
	CONCRETE(12.5, "Concrete Wall"),
	BRICK(8, "Brick Wall"),
	GLASS_WITH_METAL_FRAME(6, "Glass Wall With Metal Frame"),
	MARBLE(5, "Marble Wall"),
	CINDERBLOCK(5, "Cinderblock Wall"),
	DRYWALL(4, "Dry Wall"),
	PLASTERBOARD(3, "Plasterboard Wall"),
	CUBICLE(4, "Cubicle Wall"),
	WOODEN_DOOR(3, "Wooden Door"),
	GLASS_WINDOW(2, "Glass Window");

	private final double valor;
	private final String description;
	MaterialType(double valorOpcao, String descricao) {
		valor = valorOpcao;
		description = descricao;
	}

	public double getWAF() {
		return valor;
	}

	public String getDescription() {
		return description;
	}
}
