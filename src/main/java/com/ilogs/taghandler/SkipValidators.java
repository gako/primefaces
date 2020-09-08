/*
 * Copyright 2016 OmniFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.ilogs.taghandler;

import static java.lang.Boolean.TRUE;
import static javax.faces.event.PhaseId.RESTORE_VIEW;
import static org.omnifaces.util.Components.hasInvokedSubmit;
import static org.omnifaces.util.Events.subscribeToRequestAfterPhase;
import static org.omnifaces.util.Events.subscribeToViewEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.el.ValueExpression;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostValidateEvent;
import javax.faces.event.PreValidateEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import javax.faces.validator.Validator;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

import org.omnifaces.util.Callback;
import org.primefaces.component.calendar.Calendar;
import org.primefaces.util.LangUtils;

/**
 * <p>
 * The <code>&lt;p:skipValidators&gt;</code> taghandler allows the developer to entirely skip validation when executing an {@link UICommand} or {@link ClientBehaviorHolder} action. This taghandler must be placed inside an {@link UICommand} or {@link ClientBehaviorHolder} component (client behavior holder components are
 * those components supporting <code>&lt;f:ajax&gt;</code>).
 *
 * <h3>Usage</h3>
 * <p>
 * For example, when adding a new row to the data table, you'd like to not immediately validate all empty rows.
 *
 * <pre>
 * &lt;h:form&gt;
 *     &lt;h:dataTable value="#{bean.items}" var="item"&gt;
 *         &lt;h:column&gt;
 *             &lt;h:inputText value="#{item.value}" required="true" /&gt;
 *         &lt;/h:column&gt;
 *     &lt;/h:dataTable&gt;
 *     &lt;h:commandButton value="add new row" action="#{bean.add}"&gt;
 *         &lt;p:skipValidators /&gt;
 *     &lt;/h:commandButton&gt;
 *     &lt;h:commandButton value="save all data" action="#{bean.save}" /&gt;
 *     &lt;h:messages /&gt;
 * &lt;/h:form&gt;
 * </pre>
 * <p>
 * Note that converters will still run and that model values will still be updated. This behavior is by design.
 *
 * @author Michele Mariotti
 * @author Bauke Scholtz
 * @since 2.3
 */
public class SkipValidators extends TagHandler {

	// Constants ------------------------------------------------------------------------------------------------------

	private static final String ERROR_INVALID_PARENT = "Parent component of o:skipValidators must be an instance of UICommand or ClientBehaviorHolder.";

	public static final String VALIDATION_GROUP_ATTRIBUTE = "validationGroup";

	/**
	 * Group that should still be validated even if skipvalidators was added to button
	 */
	public static final String INCLUDE_GROUP_ATTRIBUTE = "includeGroup";
	/**
	 * Group that should not be validated, anything else will be validated
	 */
	public static final String EXCLUDE_GROUP_ATTRIBUTE = "excludeGroup";

	public static final String VALIDATION_REQUIRED_BY_GROUP_ATTRIBUTE = "requiredByGroup";

	// Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * The tag constructor.
	 *
	 * @param config
	 *            The tag config.
	 */
	public SkipValidators(TagConfig config) {
		super(config);
	}

	// Actions --------------------------------------------------------------------------------------------------------

	/**
	 * If the parent component is an instance of {@link UICommand} or {@link ClientBehaviorHolder}, and is new, and we're in the restore view phase of a postback, then delegate to {@link #processSkipValidators(UIComponent)}.
	 *
	 * @throws IllegalArgumentException
	 *             When the parent component is not an instance of {@link UICommand} or {@link ClientBehaviorHolder}.
	 */
	@Override
	public void apply(final FaceletContext context, final UIComponent parent) throws IOException {
		if (!(parent instanceof UICommand || parent instanceof ClientBehaviorHolder)) {
			throw new IllegalArgumentException(ERROR_INVALID_PARENT);
		}

		FacesContext facesContext = context.getFacesContext();

		if (!(ComponentHandler.isNew(parent) && facesContext.isPostback() && facesContext.getCurrentPhaseId() == RESTORE_VIEW)) {
			return;
		}

		// We can't use hasInvokedSubmit() before the component is added to view, because the client ID isn't available.
		// Hence, we subscribe this check to after phase of restore view.
		subscribeToRequestAfterPhase(RESTORE_VIEW, new Callback.Void() {
			@Override
			public void invoke() {
				processSkipValidators(context, parent);
			}
		});
	}

	/**
	 * Check if the given component has been invoked during the current request and if so, then register the skip validators event listener which removes the validators during {@link PreValidateEvent} and restores them during {@link PostValidateEvent}.
	 *
	 * @param parent
	 *            The parent component of this tag.
	 */
	protected void processSkipValidators(FaceletContext context, UIComponent parent) {
		if (!hasInvokedSubmit(parent)) {
			return;
		}

		TagAttribute includeGroupTag = getAttribute(INCLUDE_GROUP_ATTRIBUTE);
		String includeGroup = includeGroupTag != null ? includeGroupTag.getValue(context) : null;

		TagAttribute excludeGroupTag = getAttribute(EXCLUDE_GROUP_ATTRIBUTE);
		String excludeGroup = excludeGroupTag != null ? excludeGroupTag.getValue(context) : null;

		SkipValidatorsEventListener listener = new SkipValidatorsEventListener(includeGroup, excludeGroup);
		subscribeToViewEvent(PreValidateEvent.class, listener);
		subscribeToViewEvent(PostValidateEvent.class, listener);
	}

	/**
	 * Remove validators during prevalidate and restore them during postvalidate.
	 */
	static class SkipValidatorsEventListener implements SystemEventListener {

		/**
		 * Group that should still be validated even if skipvalidators was added to button
		 */
		private String includeGroup = null;
		/**
		 * Group that should not be validated, anything else will be validated
		 */
		private String excludeGroup = null;

		private Map<String, ValueExpression> requiredExpressions = new HashMap<String, ValueExpression>();
		private Map<String, Boolean> required = new HashMap<String, Boolean>();
		private Map<String, Validator[]> allValidators = new HashMap<String, Validator[]>();
		private Map<String, Object> validationValues = new HashMap<String, Object>();
		private Map<String, ValueExpression> validationExpressions = new HashMap<String, ValueExpression>();

		public SkipValidatorsEventListener(String includeGroup, String excludeGroup) {
			this.includeGroup = includeGroup;
			this.excludeGroup = excludeGroup;
		}

		@Override
		public boolean isListenerForSource(Object source) {
			return source instanceof UIInput;
		}

		@Override
		public void processEvent(SystemEvent event) throws AbortProcessingException {
			UIInput input = (UIInput) event.getSource();
			String clientId = input != null ? input.getClientId() : null;

			String inputValidationGroup = null;
			if (input != null && input.getAttributes().get(VALIDATION_GROUP_ATTRIBUTE) != null) {
				inputValidationGroup = input.getAttributes().get(VALIDATION_GROUP_ATTRIBUTE).toString();

				// if we have validationGroup defined, keep the validators for the given group
				if (!LangUtils.isValueBlank(inputValidationGroup) && Objects.equals(inputValidationGroup, includeGroup)) {

					// handle requiredbygroup attribute and set required to true if the validation groups match
					ValueExpression requiredByGroupExpression = input.getValueExpression(VALIDATION_REQUIRED_BY_GROUP_ATTRIBUTE);
					Object requiredByGroupAttribte = input.getAttributes().get(VALIDATION_REQUIRED_BY_GROUP_ATTRIBUTE);
					if (requiredByGroupExpression != null || requiredByGroupAttribte != null) {

						if (event instanceof PreValidateEvent) {
							storeRequired(input);

							if (requiredByGroupExpression != null) {
								input.setValueExpression("required", requiredByGroupExpression);
							} else if (requiredByGroupAttribte != null) {
								overwriteRequired(input, Boolean.parseBoolean(requiredByGroupAttribte.toString()));
							}

						} else if (event instanceof PostValidateEvent) {
							restoreRequired(input);
						}

					}
					return;
				}
			}

			if (LangUtils.isValueBlank(excludeGroup) || Objects.equals(inputValidationGroup, excludeGroup)) {

				if (event instanceof PreValidateEvent) {
					storeRequired(input);

					overwriteRequired(input, false);

					Validator[] validators = input.getValidators();
					allValidators.put(clientId, validators);

					for (Validator validator : validators) {
						input.removeValidator(validator);
					}
				} else if (event instanceof PostValidateEvent) {
					restoreRequired(input);

					for (Validator validator : allValidators.remove(clientId)) {
						input.addValidator(validator);
					}
				}
			}
		}

		protected void storeRequired(UIInput input) {
			String clientId = input.getClientId();

			required.put(clientId, input.isRequired());
			requiredExpressions.put(clientId, input.getValueExpression("required"));

			if (input instanceof Calendar) {
				Calendar calendar = (Calendar) input;

				if (calendar.getMindate()!=null) {
					validationValues.put(clientId+"-mindate", calendar.getMindate());

				}
				validationExpressions.put(clientId+"-mindate", calendar.getValueExpression("mindate"));

				calendar.setMindate(null);
				calendar.setValueExpression("mindate", null);

				if (calendar.getMaxdate()!=null) {
                    validationValues.put(clientId+"-maxdate", calendar.getMaxdate());
				}
				validationExpressions.put(clientId+"-maxdate", calendar.getValueExpression("maxdate"));

				calendar.setMaxdate(null);
                calendar.setValueExpression("maxdate", null);
			}
		}

		protected void restoreRequired(UIInput input) {
			String clientId = input.getClientId();

			Boolean requiredValue = required.remove(clientId);
			Object requiredValueExpression = requiredExpressions.remove(clientId);

			if (requiredValueExpression instanceof ValueExpression) {
				input.setValueExpression("required", (ValueExpression) requiredValueExpression);
			} else {
				input.setRequired(TRUE.equals(requiredValue));
			}

			if (input instanceof Calendar) {
                Calendar calendar = (Calendar) input;

                ValueExpression mindateExpression = validationExpressions.remove(clientId+"-mindate");
                Object mindate = validationValues.remove(clientId+"-mindate");
                if (mindateExpression instanceof ValueExpression) {
                	calendar.setValueExpression("mindate", mindateExpression);
                } else if (mindate!=null) {
                	calendar.setMindate(mindate);
                }

                ValueExpression maxdateExpression = validationExpressions.remove(clientId+"-maxdate");
                Object maxdate = validationValues.remove(clientId+"-maxdate");
                if (maxdateExpression instanceof ValueExpression) {
                    calendar.setValueExpression("maxdate", maxdateExpression);
                } else if (maxdate!=null) {
                    calendar.setMaxdate(maxdate);
                }
            }
		}

		private void overwriteRequired(UIInput input, boolean required) {
			ValueExpression valueExpression = input.getValueExpression("required");
			if (valueExpression != null) {
			    FacesContext context = FacesContext.getCurrentInstance();

				if (valueExpression.isLiteralText()) {

					ValueExpression requiredVE= context.getApplication().getExpressionFactory().createValueExpression(
							context.getELContext(), Boolean.toString(required), Boolean.class);

					input.setValueExpression("required", requiredVE);
				} else {

					ValueExpression requiredVE= context.getApplication().getExpressionFactory().createValueExpression(
							context.getELContext(), "#{" + Boolean.toString(required) + "}", Boolean.class);

					input.setValueExpression("required", requiredVE);
				}
			} else {
				input.setRequired(required);
			}

		}

	}

}
