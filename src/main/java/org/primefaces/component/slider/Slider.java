/**
 *  Copyright 2009-2022 PrimeTek.
 *
 *  Licensed under PrimeFaces Commercial License, Version 1.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  Licensed under PrimeFaces Commercial License, Version 1.0 (the "License");
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.primefaces.component.slider;

import java.util.Collection;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;

import org.primefaces.event.SlideEndEvent;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MapBuilder;
import org.primefaces.util.MessageFactory;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js")
})
public class Slider extends SliderBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Slider";
    public static final String VALUE_OUT_OF_RANGE = "primefaces.slider.OUT_OF_RANGE";

    private static final String DEFAULT_EVENT = "slideEnd";
    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("slideEnd", SlideEndEvent.class)
            .build();
    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return DEFAULT_EVENT;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if (ComponentUtils.isRequestSource(this, context)) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = getClientId(context);

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if (eventName.equals("slideEnd")) {
                double sliderValue = Double.parseDouble(params.get(clientId + "_slideValue"));
                SlideEndEvent slideEndEvent = new SlideEndEvent(this, behaviorEvent.getBehavior(), sliderValue);
                slideEndEvent.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(slideEndEvent);
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    @Override
    public void validate(FacesContext context) {
        super.validate(context);

        if (!isValid()) {
            return;
        }

        if (isRange()) {
            String[] inputIds = getFor().split(",");
            UIInput inputFrom = (UIInput) SearchExpressionFacade.resolveComponent(context, this, inputIds[0]);
            UIInput inputTo = (UIInput) SearchExpressionFacade.resolveComponent(context, this, inputIds[1]);
            Object submittedValueFrom = getSubmittedValue(inputFrom);
            Object submittedValueTo = getSubmittedValue(inputTo);
            if (submittedValueFrom == null || submittedValueTo == null) {
                return;
            }
            String valueFromStr = submittedValueFrom.toString();
            String valueToStr = submittedValueTo.toString();
            double valueFrom = Double.valueOf(valueFromStr);
            double valueTo = Double.valueOf(valueToStr);
            if (valueTo < valueFrom) {
                setValid(false);
                inputFrom.setValid(false);
                inputTo.setValid(false);
            }
            else {
                if (valueFrom < getMinValue() || valueFrom > getMaxValue()) {
                    setValid(false);
                    inputFrom.setValid(false);
                }
                if (valueTo > getMaxValue() || valueTo < getMinValue()) {
                    setValid(false);
                    inputTo.setValid(false);
                }
            }
        }
        else {
            UIInput input = (UIInput) SearchExpressionFacade.resolveComponent(context, this, getFor());
            Object submittedValue = getSubmittedValue(input);
            if (submittedValue == null) {
                return;
            }

            String submittedValueString;
            Converter converter = input.getConverter();
            if (converter != null) {
                submittedValueString = converter.getAsString(context, this, submittedValue);
            }
            else {
                submittedValueString = submittedValue.toString();
            }

            if (LangUtils.isValueBlank(submittedValueString)) {
                return;
            }

            double submittedValueDouble = Double.valueOf(submittedValueString);
            if (submittedValueDouble < getMinValue() || submittedValueDouble > getMaxValue()) {
                setValid(false);
                input.setValid(false);
            }
        }

        if (!isValid()) {
            String validatorMessage = getValidatorMessage();
            FacesMessage msg = null;
            if (validatorMessage != null) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, validatorMessage, validatorMessage);
            }
            else {
                msg = MessageFactory.getMessage(VALUE_OUT_OF_RANGE, FacesMessage.SEVERITY_ERROR, null);
            }
            context.addMessage(getClientId(context), msg);
        }
    }

    private Object getSubmittedValue(UIInput input) {
        return input.getSubmittedValue() == null && input.isLocalValueSet() ? input.getValue() : input.getSubmittedValue();
    }
}