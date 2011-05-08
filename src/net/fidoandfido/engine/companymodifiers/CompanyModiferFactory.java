package net.fidoandfido.engine.companymodifiers;

public class CompanyModiferFactory {

	public static CompanyModifier getCompanyModifier(String name) {
		if (GentleCompanyModifier.NAME.equals(name)) {
			return new GentleCompanyModifier();
		}
		return new DefaultCompanyModifier();
	}

}
