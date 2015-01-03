package org.plugination.gpgs.core;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.MetaElement;

public class GwtUtils {
	public static native boolean hasGoogleAPI() /*-{
	    if ($wnd.googleapis) {
	        return true;
	    }
	    return false;
	}-*/;

	public static void addMetaTag(String name, String content) {
		Element head = Document.get().getElementsByTagName("head").getItem(0);
	    MetaElement metaElement = Document.get().createMetaElement();
	    metaElement.setName(name);
	    metaElement.setContent(content);
	    head.appendChild(metaElement);
	}
}
