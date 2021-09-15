package com.ilogs.component.field;

import java.io.IOException;

import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import org.primefaces.component.outputlabel.OutputLabel;
import org.primefaces.component.outputpanel.OutputPanelRenderer;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.util.CompositeUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.SharedStringBuilder;

@FacesRenderer(componentFamily = Field.COMPONENT_FAMILY, rendererType = Field.DEFAULT_RENDERER)
public class FieldRenderer extends OutputPanelRenderer {

	private static final String FACET_OUTPUT = "output";

	private static final String FACET_INPUT = "input";

	private static final String FACET_LABEL = "label";

	private static final String FACET_PREFIX = "prefix";

	private static final String FACET_POSTFIX = "postfix";

	private static final String FACET_CONTENT = "content";

	private static final String ICON_POS_RIGHT = "right";

	private static final String ICON_POS_LEFT = "left";

	private static final String SB_STYLE_CLASS = FieldRenderer.class.getName() + "#styleClass";

	private static final String FIELD_CLASS = "ipcp-field ui-widget ";

	private static final String LABEL_CLASS_INPUT ="label-floatlabel";
	private static final String LABEL_CLASS_OUTPUT ="label-floatlabel floatlabel-text";

	private static final String FIELD_CLASS_DISABLED = "ipcp-field ui-widget ui-state-disabled ";

	private static final String ICON_STYLE_CLASS_LEFT = "ui-inputgroup ui-inputgroup-icon-left";

	private static final String ICON_STYLE_CLASS_RIGHT = "ui-inputgroup ui-inputgroup-icon-right";

	private static final String NO_ICON_STYLE_CLASS = "ui-inputgroup";

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		Field field = (Field) component;
		String clientId = field.getClientId(context);

		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);

		String styleClass;
		if (field.isDisabled()) {
			styleClass = field.getStyleClass() == null ? FIELD_CLASS_DISABLED : FIELD_CLASS_DISABLED + field.getStyleClass();
		} else {
			styleClass = field.getStyleClass() == null ? FIELD_CLASS : FIELD_CLASS + field.getStyleClass();
		}

		writer.writeAttribute("class", styleClass, "styleClass");

		if (field.getStyle() != null) {
			writer.writeAttribute("style", field.getStyle(), "style");
		}
		encodeMarkup(context, field);
		writer.endElement("div");
	}

	protected void encodeIcon(FacesContext context, StringBuilder styleClass, String forClientId, Field field) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		writer.startElement("label", null);
		writer.writeAttribute("for", forClientId, "for");

		if (LangUtils.isValueBlank(field.getLabel())) {
			styleClass.append(" ui-inputgroup-addon floatlabel-spacer ");
		} else {
			styleClass.append(" ui-inputgroup-addon ");
		}

		styleClass.append(field.getIcon());
		writer.writeAttribute("class", styleClass.toString(), "styleClass");
		writer.endElement("label");

	}

	protected void encodeMarkup(FacesContext context, Field field) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		String forClientId = null;
		if (!LangUtils.isValueBlank(field.getFor())) {
			UIComponent forComponent = SearchExpressionFacade.resolveComponent(context, field, field.getFor());
			if (forComponent != null) {
				forClientId = forComponent.getClientId();
			} else {
				forClientId = field.getFor();
			}
		}

		boolean showInput = (!field.isEditableDefined() || field.isEditable()) && !field.isReadonly() && !field.isDisabled();


		if (!renderFacet(context, field, FACET_LABEL, null) && !LangUtils.isValueBlank(field.getLabel())) {
			OutputLabel outputLabel = (OutputLabel) context.getApplication().createComponent(OutputLabel.COMPONENT_TYPE);
			outputLabel.setValue(field.getLabel());
			outputLabel.setFor(field.getFor());
			outputLabel.setStyleClass(showInput ? LABEL_CLASS_INPUT : LABEL_CLASS_OUTPUT);
			outputLabel.setEscape(false);
			outputLabel.setParent(field);
			outputLabel.encodeAll(context);

			outputLabel.setParent(null);

			// writer.startElement("label", null);
			// writer.writeAttribute("for", field.getFor(), "for");
			// writer.writeAttribute("class", "label-floatlabel", "styleClass");
			// writer.writeText(field.getLabel(), field, "label");
			// writer.endElement("label");
		}

		boolean hasIcon = !LangUtils.isValueBlank(field.getIcon()) && showInput;

		boolean hasInputGroup = field.getFacet(FACET_POSTFIX)!=null || field.getFacet(FACET_PREFIX)!=null || hasIcon;

		final StringBuilder styleClass = SharedStringBuilder.get(context, SB_STYLE_CLASS);

		if (hasIcon) {
			String _for = field.getFor();
			if (!isValueBlank(_for)) {
				ContextCallback callback = new ContextCallback() {
					@Override
					public void invokeContextCallback(FacesContext context, UIComponent target) {
						if (target instanceof UIInput) {
							UIInput input = (UIInput) target;

							if (!input.isValid()) {
								styleClass.append(" ui-state-error ");
							}
						}
					}
				};

				UIComponent forComponent = SearchExpressionFacade.resolveComponent(context, field, _for);

				if (CompositeUtils.isComposite(forComponent)) {
					CompositeUtils.invokeOnDeepestEditableValueHolder(context, forComponent, callback);
				} else {
					callback.invokeContextCallback(context, forComponent);
				}
			}

		}

		if (hasInputGroup) {
		    writer.startElement("div", null);
            String containerStyleClass;

            if (hasIcon) {
                if (ICON_POS_RIGHT.equalsIgnoreCase(field.getIconPos())) {
                    containerStyleClass = ICON_STYLE_CLASS_RIGHT;
                } else {
                    containerStyleClass = ICON_STYLE_CLASS_LEFT;
                }
            } else {
                containerStyleClass = NO_ICON_STYLE_CLASS;
            }
            writer.writeAttribute("class", containerStyleClass, "styleClass");
		}

		if (hasIcon && ICON_POS_LEFT.equalsIgnoreCase(field.getIconPos())) {
			encodeIcon(context, styleClass, forClientId, field);
		}

		renderFacet(context, field, FACET_PREFIX, null);

		renderFacet(context, field, FACET_INPUT, showInput);
		renderFacet(context, field, FACET_OUTPUT, !showInput);

		renderChildren(context, field);

		renderFacet(context, field, FACET_POSTFIX, null);

		if (hasIcon && ICON_POS_RIGHT.equalsIgnoreCase(field.getIconPos())) {
			encodeIcon(context, styleClass, forClientId, field);
		}

		if (hasInputGroup) {
			writer.endElement("div");
		}

		renderFacet(context, field, FACET_CONTENT, null);
	}

	private boolean renderFacet(FacesContext context, Field field, String facet, Boolean rendered) throws IOException {
	    UIComponent inputFacet = field.getFacet(facet);
        if (inputFacet != null) {
            if (rendered!=null) {
                inputFacet.setRendered(rendered);
            }

            if (inputFacet.isRendered()) {
                inputFacet.encodeAll(context);
                return true;
            }
        }
        return false;
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
