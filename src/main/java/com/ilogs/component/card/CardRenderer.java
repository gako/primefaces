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

	private static final String CARD_CLASS = "Card ui-widget";
	private static final Object CARD_TITLEBAR_CLASS = "CardTopic";
	private static final Object CARD_ACTIONS_CLASS = "CardFooter";
	private static final Object CARD_FOOTER_CLASS = "CardFooter";

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
		// CommandLink Start

		UIComponent facetActions = card.getFacet("actions");
		String wrapperStyleClass = "CardWrapper";
		if (facetActions != null && facetActions.isRendered()) {
			wrapperStyleClass = "CardWrapper CardWrapperWithFooter";
		} else {
			wrapperStyleClass = "CardWrapper";
		}

		writer.startElement("div", card);
		writer.writeAttribute("class", wrapperStyleClass, "styleClass");

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
			writer.startElement("div", null); // CardIconContainer
			writer.writeAttribute("class", "CardIconContainer accent", "styleClass");

			if (icon != null) {
				renderChild(context, icon);
			} else if (!LangUtils.isValueBlank(panel.getIcon())) {
				writer.startElement("i", null);
				writer.writeAttribute("class", panel.getIcon(), "styleClass");
				writer.endElement("i");
			}
			writer.endElement("div"); // CardIconContainer
		}
		{
			writer.startElement("div", null); // CardTextContainer
			writer.writeAttribute("class", "CardTextContainer", "styleClass");

			encodeHeader(context, panel);

			writer.startElement("div", null); // CardContent
			writer.writeAttribute("class", "CardContent", "styleClass");
			renderChildren(context, panel);
			writer.endElement("div");// CardContent
			encodeLinks(context, panel);
			encodeFooter(context, panel);

			writer.endElement("div"); // CardTextContainer
		}

	}

	protected void encodeHeader(FacesContext context, Card panel) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		UIComponent header = panel.getFacet("header");

		if (header != null && header.isRendered()) {
			writer.startElement("div", null);
			writer.writeAttribute("class", CARD_TITLEBAR_CLASS, "styleClass");

			renderChild(context, header);

			writer.endElement("div");
		}

	}

	protected void encodeLinks(FacesContext context, Card panel) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		UIComponent header = panel.getFacet("links");

		if (header != null && header.isRendered()) {
			writer.startElement("div", null);
			writer.writeAttribute("class", "CardLinkContainer", "styleClass");

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
