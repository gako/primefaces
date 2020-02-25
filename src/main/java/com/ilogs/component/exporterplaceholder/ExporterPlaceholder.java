package com.ilogs.component.exporterplaceholder;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

import org.primefaces.util.ComponentUtils;

@FacesComponent(value = ExporterPlaceholder.COMPONENT_TYPE)
public class ExporterPlaceholder extends UIOutput {

	public static final String COMPONENT_TYPE = "com.ilogs.component.exporterplaceholder.ExporterPlaceholder";
	public static final String COMPONENT_FAMILY = "com.ilogs.component";
	public static final String DEFAULT_RENDERER = "com.ilogs.component.exporterplaceholder.ExporterPlaceholderRenderer";

	public ExporterPlaceholder() {
		setRendererType(DEFAULT_RENDERER);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	@Override
	public String toString() {
		String value = ComponentUtils.getValueToRender(FacesContext.getCurrentInstance(), this);
		if (value != null) {
		    return value;
		} else {
		    return "";
		}
	}

}
