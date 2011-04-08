package net.fidoandfido.initialiser;

public class CompanyNamePrefix {
	public final String value;
	public final boolean spaceAllowed;
	public final boolean spaceOptional;
	public final String code;

	public CompanyNamePrefix(String value, boolean spaceAllowed, boolean spaceOptional, String code) {
		this.value = value;
		this.spaceAllowed = spaceAllowed;
		this.spaceOptional = spaceOptional;
		this.code = code;
	}
}