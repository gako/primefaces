package com.ilogs.component.status;

import java.io.IOException;
import java.util.Arrays;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

@FacesRenderer(componentFamily = Status.COMPONENT_FAMILY,rendererType = Status.DEFAULT_RENDERER)
public class StatusRenderer extends CoreRenderer {

	public static final String STATUS_CLASS = "ipcp-status";
	public static final Object STATUS_LABEL_CLASS = "ipcp-status-label";
	public static final Object STATUS_ICON_CLASS = "ipcp-status-icon";

	public static final String[] STATUS_OK = new String[] { "TRUE", "INFO", "OK", "ACTIVE", "PENDING", "COMPLETED", "DELIVERED", "APPLICABLE", "FULLY_USED" };
	public static final String[] STATUS_WARN = new String[] { "WARNING", "WARN", "INACTIVE", "SUSPENDED", "TRANSFER", "TRANSFER2", "UNSURE", "LOCKED", "ISSUED", "PARTIALLY_USED" };
	public static final String[] STATUS_ERROR = new String[] { "FALSE", "SEVERE", "FATAL", "ERROR", "DELETED", "FAILED", "NOT_APPLICABLE", "NOT_USED", "OFF" };

	static {
		Arrays.sort(STATUS_OK);
		Arrays.sort(STATUS_WARN);
		Arrays.sort(STATUS_ERROR);
	}

	public static String statusClass(Object o) {
		if (o != null) {
			String s = o.toString().toUpperCase();

			if (Arrays.binarySearch(STATUS_OK, s) >= 0) {
				return "info";
			} else if (Arrays.binarySearch(STATUS_WARN, s) >= 0) {
				return "warning";
			} else if (Arrays.binarySearch(STATUS_ERROR, s) >= 0) {
				return "error";
			} else {
				return "default";
			}
		} else {
			return "default";
		}

	}

	public static String statusIcon(Object o) {
		return "fa fas fa-circle " + statusClass(o);
	}

	@Override
	public void decode(FacesContext context, UIComponent component) {
		// Status status = (Status) component;
		decodeBehaviors(context, component);
	}

	@Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		Status status = (Status) component;

		encodeMarkup(facesContext, status);
	}

	protected void encodeMarkup(FacesContext context, Status status) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = status.getClientId(context);

		writer.startElement("span", null);
		writer.writeAttribute("id", clientId, null);
		String styleClass = status.getStyleClass() == null ? STATUS_CLASS : STATUS_CLASS + " " + status.getStyleClass();

		writer.writeAttribute("class", statusClass(status.getValue()) + (status.isLabel() ? " with-label " : " ") + styleClass, "styleClass");

		if (status.getStyle() != null) {
			writer.writeAttribute("style", status.getStyle(), "style");
		}

		renderDynamicPassThruAttributes(context, status);
		{
			writer.startElement("i", null);
			writer.writeAttribute("id", clientId + "_icon", null);
			writer.writeAttribute("title", status.getValue(), null);
			writer.writeAttribute("class", STATUS_ICON_CLASS + " " + status.getIcon() + " " + statusClass(status.getValue()), "styleClass");
			writer.endElement("i");
			if (status.isLabel()) {
				writer.startElement("span", null);
				writer.writeAttribute("id", clientId + "_label", null);
				writer.writeAttribute("title", status.getValue(), null);
				writer.writeAttribute("class", STATUS_LABEL_CLASS, "styleClass");

				if (status.getValue() != null) {
					String label = ComponentUtils.getValueToRender(context, status);
					if (status.isEscape()) {
						writer.writeText(label, "value");
					} else {
						writer.write(label);
					}
				}

				writer.endElement("span");
			}

		}
		writer.endElement("span");
	}
}
