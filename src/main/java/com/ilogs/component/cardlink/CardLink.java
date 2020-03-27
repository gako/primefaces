package com.ilogs.component.cardlink;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;

import org.primefaces.component.commandlink.CommandLink;

@FacesComponent(value = CardLink.COMPONENT_TYPE)
@ResourceDependencies({
	@ResourceDependency(library = "primefaces", name = "components.css")
})
public class CardLink extends CommandLink {

	public static final String COMPONENT_TYPE = "com.ilogs.component.cardlink.CardLink";
	public static final String COMPONENT_FAMILY = "com.ilogs.component";
	public static final String DEFAULT_RENDERER = "com.ilogs.component.cardlink.CardLinkRenderer";

	protected enum PropertyKeys {
		icon, hoverable, selected
	}

	public CardLink() {
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

	public boolean isHoverable() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.hoverable, true);
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

}
