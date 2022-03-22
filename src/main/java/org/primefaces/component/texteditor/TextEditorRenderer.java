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
package org.primefaces.component.texteditor;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.primefaces.context.PrimeApplicationContext;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.HtmlSanitizer;
import org.primefaces.util.WidgetBuilder;

public class TextEditorRenderer extends InputRenderer {

    private static final Logger LOGGER = Logger.getLogger(TextEditorRenderer.class.getName());

    @Override
    public void decode(FacesContext context, UIComponent component) {
        TextEditor editor = (TextEditor) component;

        if (!shouldDecode(editor)) {
            return;
        }

        decodeBehaviors(context, editor);

        String inputParam = editor.getClientId(context) + "_input";
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String value = sanitizeHtml(context, editor, params.get(inputParam));

        if (value != null && value.equals("<br/>")) {
            value = Constants.EMPTY_STRING;
        }

        editor.setSubmittedValue(value);
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        TextEditor editor = (TextEditor) component;

        // #5163 fail rendering if insecure
        checkSecurity(facesContext, editor);
        encodeMarkup(facesContext, editor);
        encodeScript(facesContext, editor);
    }

    protected void encodeMarkup(FacesContext context, TextEditor editor) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = editor.getClientId(context);
        String valueToRender = sanitizeHtml(context, editor, ComponentUtils.getValueToRender(context, editor));
        String inputId = clientId + "_input";
        String editorId = clientId + "_editor";
        UIComponent toolbar = editor.getFacet("toolbar");

        String style = editor.getStyle();
        String styleClass = editor.getStyleClass();
        styleClass = (styleClass != null) ? TextEditor.EDITOR_CLASS + " " + styleClass : TextEditor.EDITOR_CLASS;

        writer.startElement("div", editor);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        if (toolbar != null && editor.isToolbarVisible()) {
            writer.startElement("div", editor);
            writer.writeAttribute("id", clientId + "_toolbar", null);
            writer.writeAttribute("class", "ui-editor-toolbar", null);
            toolbar.encodeAll(context);
            writer.endElement("div");
        }

        writer.startElement("div", editor);
        writer.writeAttribute("id", editorId, null);
        if (valueToRender != null) {
            writer.write(valueToRender);
        }
        writer.endElement("div");

        writer.startElement("input", null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("name", inputId, null);
        // #2905
        if (valueToRender != null) {
            writer.writeAttribute("value", valueToRender, null);
        }
        writer.endElement("input");

        writer.endElement("div");
    }

    private void encodeScript(FacesContext context, TextEditor editor) throws IOException {
        String clientId = editor.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("TextEditor", editor.resolveWidgetVar(), clientId)
                .attr("toolbarVisible", editor.isToolbarVisible())
                .attr("readOnly", editor.isReadonly(), false)
                .attr("placeholder", editor.getPlaceholder(), null)
                .attr("height", editor.getHeight(), Integer.MIN_VALUE);

        List formats = editor.getFormats();
        if (formats != null) {
            wb.append(",formats:[");
            for (int i = 0; i < formats.size(); i++) {
                if (i != 0) {
                    wb.append(",");
                }

                wb.append("\"" + EscapeUtils.forJavaScript((String) formats.get(i)) + "\"");
            }
            wb.append("]");
        }

        encodeClientBehaviors(context, editor);
        wb.finish();
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        TextEditor editor = (TextEditor) component;
        String value = (String) submittedValue;
        Converter converter = ComponentUtils.getConverter(context, component);

        if (converter != null) {
            return converter.getAsObject(context, editor, value);
        }

        return value;
    }

    /**
     * Enforce security by default requiring the OWASP sanitizer on the classpath.  Only if a user marks the editor
     * with secure="false" will they opt-out of security.
     *
     * @param context the FacesContext
     * @param editor the editor to check for security
     */
    private void checkSecurity(FacesContext context, TextEditor editor) {
        boolean sanitizerAvailable = PrimeApplicationContext.getCurrentInstance(context).getEnvironment().isHtmlSanitizerAvailable();
        if (editor.isSecure() && !sanitizerAvailable) {
            throw new FacesException("TextEditor component is marked secure='true' but the HTML Sanitizer was not found on the classpath. "
                    + "Either add the HTML sanitizer to the classpath per the documentation"
                    + " or mark secure='false' if you would like to use the component without the sanitizer.");
        }
    }

    /**
     * If security is enabled sanitize the HTML string to prevent XSS.
     *
     * @param context the FacesContext
     * @param editor the TextEditor instance
     * @param value the value to sanitize
     * @return the sanitized value
     */
    private String sanitizeHtml(FacesContext context, TextEditor editor, String value) {
        String result = value;
        if (editor.isSecure() && PrimeApplicationContext.getCurrentInstance(context).getEnvironment().isHtmlSanitizerAvailable()) {
            result = HtmlSanitizer.sanitizeHtml(value,
                    editor.isAllowBlocks(), editor.isAllowFormatting(),
                    editor.isAllowLinks(), editor.isAllowStyles(), editor.isAllowImages());
        }
        else {
            if (!editor.isAllowBlocks() || !editor.isAllowFormatting()
                    || !editor.isAllowLinks() || !editor.isAllowStyles() || !editor.isAllowImages()) {
                LOGGER.warning("HTML sanitizer not available - skip sanitizing....");
            }
        }
        return result;
    }
}
