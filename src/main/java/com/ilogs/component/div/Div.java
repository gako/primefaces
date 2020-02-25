package com.ilogs.component.div;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;

@FacesComponent(value = Div.COMPONENT_TYPE)
public class Div extends UIComponentBase {

	public static final String COMPONENT_TYPE = "com.ilogs.component.container.Div";
	public static final String COMPONENT_FAMILY = "com.ilogs.component";
	public static final String RENDERER_TYPE = "com.ilogs.component.container.DivRenderer";

	protected enum PropertyKeys {
		tag, style, styleClass
	}

	public Div() {
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
