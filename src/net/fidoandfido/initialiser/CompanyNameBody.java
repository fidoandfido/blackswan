package net.fidoandfido.initialiser;

public class CompanyNameBody {
	public final String value;
	public final boolean prefixable;
	public final boolean suffixable;
	public final String sector;
	public final String code;
	public final String strategy;

	public CompanyNameBody(String value, boolean prefixable, boolean suffixable, String sector, String code, String strategy) {
		this.value = value;
		this.prefixable = prefixable;
		this.suffixable = suffixable;
		this.sector = sector;
		this.code = code;
		this.strategy = strategy;
	}

}