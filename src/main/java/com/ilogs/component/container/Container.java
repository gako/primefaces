package com.ilogs.component.container;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

@FacesComponent(value = Container.COMPONENT_TYPE)
public class Container extends UINamingContainer {

	public static final String COMPONENT_TYPE = "com.ilogs.component.container.Container";
	public static final String COMPONENT_FAMILY = "com.ilogs.component";
	public static final String RENDERER_TYPE = "com.ilogs.component.container.ContainerRenderer";

	protected enum PropertyKeys {
		tag, style, styleClass
	}

	public Container() {
		setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getTag() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.tag, "div");

	}

	public void setTag(String value) {
		getStateHelper().put(PropertyKeys.tag, value);
	}

	public String getStyle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);

	}

	public void setStyle(String value) {
		getStateHelper().put(PropertyKeys.style, value);
	}

	public String getStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);

	}

	public void setStyleClass(String value) {
		getStateHelper().put(PropertyKeys.styleClass, value);
	}

}
