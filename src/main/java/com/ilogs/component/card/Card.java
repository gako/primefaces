package com.ilogs.component.card;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;

import org.primefaces.component.outputpanel.OutputPanel;

@FacesComponent(value = Card.COMPONENT_TYPE)
@ResourceDependencies({
	@ResourceDependency(library = "primefaces", name = "components.css")
})
public class Card extends OutputPanel {

	public static final String COMPONENT_TYPE = "com.ilogs.component.card.Card";
	public static final String COMPONENT_FAMILY = "com.ilogs.component";
	public static final String DEFAULT_RENDERER = "com.ilogs.component.card.CardRenderer";

	protected enum PropertyKeys {
		icon, hoverable, selected, disabled, ribbonText;
	}

	public Card() {
		setRendererType(DEFAULT_RENDERER);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getIcon() {
		return (String) getStateHelper().eval(PropertyKeys.icon, null);
	}

	public void setIcon(String edit) {
		getStateHelper().put(PropertyKeys.icon, edit);
	}

	public String getRibbonText() {
        return (String) getStateHelper().eval(PropertyKeys.ribbonText, null);
    }

    public void setRibbonText(String edit) {
        getStateHelper().put(PropertyKeys.ribbonText, edit);
    }

	public boolean isHoverable() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.hoverable, false);
	}

	public void setHoverable(boolean _hover) {
		getStateHelper().put(PropertyKeys.hoverable, _hover);
	}

	public boolean isSelected() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.selected, false);
	}

	public void setSelected(boolean _hover) {
		getStateHelper().put(PropertyKeys.selected, _hover);
	}

	/**
	 * <p>
	 * Return the value of the <code>disabled</code> property.
	 * </p>
	 * <p>
	 * Contents: Flag indicating that this element must never receive focus or be included in a subsequent submit.
	 */
	public boolean isDisabled() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.disabled, false);

	}

	/**
	 * <p>
	 * Set the value of the <code>disabled</code> property.
	 * </p>
	 */
	public void setDisabled(boolean disabled) {
		getStateHelper().put(PropertyKeys.disabled, disabled);
	}

}
