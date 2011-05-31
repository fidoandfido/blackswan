function createRequest() {
	try {
		request = new XMLHttpRequest();
	} catch (tryMs) {
		try {
			request = new ActiveXObject("Msxml12.XMLHTTP");
		} catch (otherMs) {
			try {
				request = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (failed) {
				request = null;
			}
		}
	}
	return request;
}

