package com.ilogs.component.card;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import org.primefaces.component.outputpanel.OutputPanelRenderer;
import org.primefaces.util.LangUtils;

@FacesRenderer(componentFamily =  Card.COMPONENT_FAMILY, rendererType = Card.DEFAULT_RENDERER)
public class CardRenderer extends OutputPanelRenderer {

	public static final String CARD_CLASS = "ui-widget ui-card";
	public static final String CARD_TITLEBAR_CLASS = "ui-card-header";
	public static final String CARD_ACTIONS_CLASS = "ui-card-actions";
	public static final String CARD_FOOTER_CLASS = "ui-card-footer";
	public static final String CARD_LINKS_CLASS = "ui-card-links";

	public static final String CARD_WRAPPER_CLASS = "ui-card-wrapper";

	public static final String CARD_ICON_CONTAINER_CLASS = "ui-card-icon-container accent";
	public static final String CARD_TEXT_CONTAINER_CLASS = "ui-card-text-container";

	public static final String CARD_CONTENT_CLASS = "ui-card-content";

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		Card card = (Card) component;
		String clientId = card.getClientId(context);

		// CARD DIV

		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		String styleClass = card.getStyleClass() == null ? CARD_CLASS : CARD_CLASS + " " + card.getStyleClass();

		if (!card.isDisabled() && card.isHoverable()) {
			styleClass += " HoverEffect";
		}
		if (card.isSelected()) {
			styleClass += " ui-state-highlight";
		}
		if (card.isDisabled()) {
			styleClass += " ui-state-disabled";
		}

		writer.writeAttribute("class", styleClass, "styleClass");

		if (card.getStyle() != null) {
			writer.writeAttribute("style", card.getStyle(), "style");
		}


		// <div class="ribbon"><span class="ribbon__content">exclusive</span></div>
		if (!LangUtils.isValueBlank(card.getRibbonText())) {
    		writer.startElement("div", card);
    		writer.writeAttribute("class", "ribbon", "styleClass");
    		writer.startElement("span", card);
    		writer.writeAttribute("class", "ribbon__content", "styleClass");
    		writer.write(card.getRibbonText());
    		writer.endElement("span");
    		writer.endElement("div");
		}
		// CommandLink Start

		writer.startElement("div", card);
		writer.writeAttribute("class", CARD_WRAPPER_CLASS, "styleClass");

		encodeMarkup(context, card);

		writer.endElement("div");

		// CommandLink END

		encodeActions(context, card);

		writer.endElement("div");
	}

	protected void encodeMarkup(FacesContext context, Card panel) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		UIComponent icon = panel.getFacet("icon");
		if (!LangUtils.isValueBlank(panel.getIcon()) || icon != null) {
			writer.startElement("div", null); // ui-card-icon-container
			writer.writeAttribute("class", CARD_ICON_CONTAINER_CLASS, "styleClass");

			if (icon != null) {
				renderChild(context, icon);
			} else if (!LangUtils.isValueBlank(panel.getIcon())) {
				writer.startElement("i", null);
				writer.writeAttribute("class", panel.getIcon(), "styleClass");
				writer.endElement("i");
			}
			writer.endElement("div"); // ui-card-icon-container
		}
		{
			writer.startElement("div", null); // ui-card-text-container
			writer.writeAttribute("class", CARD_TEXT_CONTAINER_CLASS, "styleClass");

			encodeHeader(context, panel);

			writer.startElement("div", null); // ui-card-content
			writer.writeAttribute("class", CARD_CONTENT_CLASS, "styleClass");
			renderChildren(context, panel);
			writer.endElement("div");// ui-card-content
			encodeLinks(context, panel);
			encodeFooter(context, panel);

			writer.endElement("div"); // ui-card-text-container
		}

	}

	protected void encodeHeader(FacesContext context, Card panel) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		UIComponent header = panel.getFacet("header");

		if (header != null && header.isRendered()) {
			writer.startElement("h3", null);
			writer.writeAttribute("class", CARD_TITLEBAR_CLASS, "styleClass");

			renderChild(context, header);

			writer.endElement("h3");
		}

	}

	protected void encodeLinks(FacesContext context, Card panel) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		UIComponent header = panel.getFacet("links");

		if (header != null && header.isRendered()) {
			writer.startElement("div", null);
			writer.writeAttribute("class", CARD_LINKS_CLASS, "styleClass");

			renderChild(context, header);

			writer.endElement("div");
		}

	}

	protected void encodeFooter(FacesContext context, Card panel) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		UIComponent footer = panel.getFacet("footer");

		if (footer != null && footer.isRendered()) {
			writer.startElement("div", null);
			writer.writeAttribute("id", panel.getClientId(context) + "_footer", null);
			writer.writeAttribute("class", CARD_FOOTER_CLASS, "styleClass");

			renderChild(context, footer);

			writer.endElement("div");
		}
	}

	protected void encodeActions(FacesContext context, Card panel) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		UIComponent actions = panel.getFacet("actions");

		if (actions != null && actions.isRendered()) {
			writer.startElement("div", null);
			writer.writeAttribute("id", panel.getClientId(context) + "_actions", null);
			writer.writeAttribute("class", CARD_ACTIONS_CLASS, "styleClass");

			renderChild(context, actions);

			writer.endElement("div");
		}
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
