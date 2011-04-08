package net.fidoandfido.initialiser;

public class CompanyNameSuffix {
	public final String value;
	public final boolean spaceAllowed;
	public final boolean spaceOptional;
	public final String code;

	public CompanyNameSuffix(String value, boolean spaceAllowed, boolean spaceOptional, String code) {
		super();
		this.value = value;
		this.spaceAllowed = spaceAllowed;
		this.spaceOptional = spaceOptional;
		this.code = code;
	}
}