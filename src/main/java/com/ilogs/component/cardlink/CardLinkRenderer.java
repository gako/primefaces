package com.ilogs.component.cardlink;
import static com.ilogs.component.card.CardRenderer.CARD_ACTIONS_CLASS;
import static com.ilogs.component.card.CardRenderer.CARD_CLASS;
import static com.ilogs.component.card.CardRenderer.CARD_CONTENT_CLASS;
import static com.ilogs.component.card.CardRenderer.CARD_FOOTER_CLASS;
import static com.ilogs.component.card.CardRenderer.CARD_ICON_CONTAINER_CLASS;
import static com.ilogs.component.card.CardRenderer.CARD_LINKS_CLASS;
import static com.ilogs.component.card.CardRenderer.CARD_TEXT_CONTAINER_CLASS;
import static com.ilogs.component.card.CardRenderer.CARD_TITLEBAR_CLASS;
import static com.ilogs.component.card.CardRenderer.CARD_WRAPPER_CLASS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import javax.faces.render.FacesRenderer;

import org.primefaces.component.api.ClientBehaviorRenderingMode;
import org.primefaces.context.PrimeRequestContext;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.CSVBuilder;
import org.primefaces.util.ComponentTraversalUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.SharedStringBuilder;
@FacesRenderer(componentFamily = CardLink.COMPONENT_FAMILY, rendererType = CardLink.DEFAULT_RENDERER)
public class CardLinkRenderer extends CoreRenderer {

	public static String[] CLICK_STYLE_EVENT = { "onclick", "style" };

	private static final String SB_BUILD_ONCLICK = CardLink.class.getName() + "#buildOnclick";


	@Override
	public void decode(FacesContext context, UIComponent component) {
		CardLink link = (CardLink) component;
		if (link.isDisabled()) {
			return;
		}

		String param = component.getClientId(context);

		if (context.getExternalContext().getRequestParameterMap().containsKey(param)) {
			component.queueEvent(new ActionEvent(component));
		}

		decodeBehaviors(context, component);
	}

	private boolean isLink(CardLink link) {
		return link.getOnclick() != null || link.getActionExpression() != null || link.getActionListeners().length > 0;
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		CardLink card = (CardLink) component;
		String clientId = card.getClientId(context);

		// CARD DIV

		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		String styleClass = card.getStyleClass() == null ? CARD_CLASS : CARD_CLASS + " " + card.getStyleClass();

		if (!card.isDisabled() && isLink(card) && card.isHoverable()) {
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


		if (!card.isDisabled() && isLink(card)) {
			String request;
			boolean ajax = card.isAjax();
			PrimeRequestContext requestContext = PrimeRequestContext.getCurrentInstance();
			// boolean csvEnabled = requestContext.getApplicationContext().getConfig().isClientSideValidationEnabled() && card.isValidateClient();
			boolean csvEnabled = card.isValidateClient();

			StringBuilder onclick = SharedStringBuilder.get(context, SB_BUILD_ONCLICK);
			if (card.getOnclick() != null) {
				onclick.append(card.getOnclick()).append(";");
			}

			String onclickBehaviors = getEventBehaviors(context, card, "click", null);
			if (onclickBehaviors != null) {
				onclick.append(onclickBehaviors);
			}

			writer.startElement("a", card);
			writer.writeAttribute("id", clientId + "_link", "id");
			writer.writeAttribute("href", "#", null);
			writer.writeAttribute("class", CARD_WRAPPER_CLASS, "styleClass");
			if (card.getTitle() != null) {
				writer.writeAttribute("aria-label", card.getTitle(), null);
			}

			if (ajax) {
				request = buildAjaxRequest(context, card, null);
			} else {
				UIComponent form = ComponentTraversalUtils.closestForm(context, card);
				if (form == null) {
					throw new FacesException("Commandlink \"" + clientId + "\" must be inside a form component");
				}

				request = buildNonAjaxRequest(context, card, form, clientId, true);
			}

			if (csvEnabled) {
				CSVBuilder csvb = requestContext.getCSVBuilder();
				request = csvb.init().source("this").ajax(ajax).process(card, card.getProcess()).update(card, card.getUpdate()).command(request).build();
			}

			onclick.append(request);

			if (onclick.length() > 0) {
				if (card.requiresConfirmation()) {
					writer.writeAttribute("data-pfconfirmcommand", onclick.toString(), null);
					writer.writeAttribute("onclick", card.getConfirmationScript(), "onclick");
				} else {
					writer.writeAttribute("onclick", onclick.toString(), "onclick");
				}
			}

			List<ClientBehaviorContext.Parameter> behaviorParams = new ArrayList<>();
			behaviorParams.add(new ClientBehaviorContext.Parameter(Constants.CLIENT_BEHAVIOR_RENDERING_MODE, ClientBehaviorRenderingMode.UNOBSTRUSIVE));
			String dialogReturnBehavior = getEventBehaviors(context, card, "dialogReturn", behaviorParams);
			if (dialogReturnBehavior != null) {
				writer.writeAttribute("data-dialogreturn", dialogReturnBehavior, null);
			}

			renderPassThruAttributes(context, card, HTML.LINK_ATTRS, CLICK_STYLE_EVENT);

			encodeMarkup(context, card);

			writer.endElement("a");
		} else {
			writer.startElement("div", card);
			writer.writeAttribute("class", CARD_WRAPPER_CLASS, "styleClass");

			encodeMarkup(context, card);

			writer.endElement("div");
		}
		// CommandLink END

		encodeActions(context, card);

		writer.endElement("div");
	}

	protected void encodeMarkup(FacesContext context, CardLink panel) throws IOException {
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

	protected void encodeHeader(FacesContext context, CardLink panel) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		UIComponent header = panel.getFacet("header");

		if (header != null && header.isRendered()) {
			writer.startElement("h3", null);
			writer.writeAttribute("id", panel.getClientId(context) + "_header", null);
			writer.writeAttribute("class", CARD_TITLEBAR_CLASS, "styleClass");

			renderChild(context, header);

			writer.endElement("h3");
		}

	}

	protected void encodeLinks(FacesContext context, CardLink panel) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		UIComponent header = panel.getFacet("links");

		if (header != null && header.isRendered()) {
			writer.startElement("div", null);
			writer.writeAttribute("class", CARD_LINKS_CLASS, "styleClass");

			renderChild(context, header);

			writer.endElement("div");
		}

	}

	protected void encodeFooter(FacesContext context, CardLink panel) throws IOException {
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

	protected void encodeActions(FacesContext context, CardLink panel) throws IOException {
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
