package com.ilogs.component.container;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import org.primefaces.component.outputpanel.OutputPanelRenderer;

@FacesRenderer(componentFamily = Container.COMPONENT_FAMILY, rendererType = Container.RENDERER_TYPE)
public class ContainerRenderer extends OutputPanelRenderer {

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		Container container = (Container) component;
		String clientId = container.getClientId(context);

		writer.startElement(container.getTag(), null);
		writer.writeAttribute("id", clientId, null);

		if (container.getStyleClass() != null) {
			writer.writeAttribute("class", container.getStyleClass(), "styleClass");
		}
		if (container.getStyle() != null) {
			writer.writeAttribute("style", container.getStyle(), "style");
		}

		renderChildren(context, container);

		writer.endElement(container.getTag());
	}

	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		// Do nothing
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}
}
