package net.fidoandfido.initialiser;

import java.util.ArrayList;
import java.util.List;

public class CompanyNameBody {
	public final String value;
	public final boolean prefixable;
	public final boolean suffixable;
	public final String code;

	public final List<String> sectors = new ArrayList<String>();

	public CompanyNameBody(String value, boolean prefixable, boolean suffixable, String code) {
		this.value = value;
		this.prefixable = prefixable;
		this.suffixable = suffixable;
		this.code = code;
	}

}