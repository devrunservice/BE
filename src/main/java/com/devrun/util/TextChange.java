package com.devrun.util;

import org.springframework.stereotype.Component;

@Component
public class TextChange {

	public String changeText(String txt) {
	    txt = txt.replaceAll("&", "&amp;");
	    txt = txt.replaceAll("<", "&lt;");
	    txt = txt.replaceAll(">", "&gt;");
	    txt = txt.replaceAll("\"", "&quot;");
	    txt = txt.replaceAll("'", "&#x27;");
	    return txt;
	}
}
