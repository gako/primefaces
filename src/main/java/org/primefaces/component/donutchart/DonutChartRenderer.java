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
package org.primefaces.component.donutchart;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.charts.ChartRenderer;
import org.primefaces.model.charts.donut.DonutChartOptions;
import org.primefaces.util.WidgetBuilder;

public class DonutChartRenderer extends ChartRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        DonutChart chart = (DonutChart) component;
        String clientId = chart.getClientId(context);
        String style = chart.getStyle();
        String styleClass = chart.getStyleClass();

        encodeMarkup(context, clientId, style, styleClass);
        encodeScript(context, chart);
    }

    protected void encodeScript(FacesContext context, DonutChart chart) throws IOException {
        String clientId = chart.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("DonutChart", chart.resolveWidgetVar(), clientId);

        encodeConfig(context, chart.getModel());
        encodeClientBehaviors(context, chart);

        wb.finish();
    }

    @Override
    protected void encodeOptions(FacesContext context, String type, Object options) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (options == null) {
            return;
        }

        DonutChartOptions donutOptions = (DonutChartOptions) options;

        writer.write(",options:{");

        writer.write("animation:{");
        writer.write("animateRotate:" + donutOptions.isAnimateRotate());
        writer.write(",animateScale:" + donutOptions.isAnimateScale());
        writer.write("}");

        if (donutOptions.getCutoutPercentage() != null) {
            writer.write(",cutoutPercentage:" + donutOptions.getCutoutPercentage());
        }

        if (donutOptions.getRotation() != null) {
            writer.write(",rotation:" + donutOptions.getRotation());
        }

        if (donutOptions.getCircumference() != null) {
            writer.write(",circumference:" + donutOptions.getCircumference());
        }

        encodeElements(context, donutOptions.getElements(), true);
        encodeTitle(context, donutOptions.getTitle(), true);
        encodeTooltip(context, donutOptions.getTooltip(), true);
        encodeLegend(context, donutOptions.getLegend(), true);

        writer.write("}");
    }
}