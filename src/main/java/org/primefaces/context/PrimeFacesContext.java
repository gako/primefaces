/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.context;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;
import javax.faces.context.ResponseWriter;
import org.primefaces.application.resource.MoveScriptsToBottomResponseWriter;
import org.primefaces.application.resource.MoveScriptsToBottomState;

/**
 * Custom {@link FacesContextWrapper} to init and release our {@link PrimeRequestContext}.
 */
public class PrimeFacesContext extends FacesContextWrapper {

    private final FacesContext wrapped;
    private final boolean moveScriptsToBottom;

    private MoveScriptsToBottomState moveScriptsToBottomState;
    private PrimeExternalContext externalContext;

    @SuppressWarnings("deprecation") // the default constructor is deprecated in JSF 2.3
    public PrimeFacesContext(FacesContext wrapped) {
        this.wrapped = wrapped;

        PrimeRequestContext requestContext = new PrimeRequestContext(wrapped);
        PrimeRequestContext.setCurrentInstance(requestContext, wrapped);

        moveScriptsToBottom = requestContext.getApplicationContext().getConfig().isMoveScriptsToBottom();
        if (moveScriptsToBottom) {
            moveScriptsToBottomState = new MoveScriptsToBottomState();
        }
    }

    @Override
    public ExternalContext getExternalContext() {
        if (externalContext == null) {
            externalContext = new PrimeExternalContext(wrapped.getExternalContext());
        }
        return externalContext;
    }

    @Override
    public void setResponseWriter(ResponseWriter writer) {
        if (!getPartialViewContext().isAjaxRequest() && moveScriptsToBottom && !(writer instanceof MoveScriptsToBottomResponseWriter)) {
            getWrapped().setResponseWriter(new MoveScriptsToBottomResponseWriter(writer, moveScriptsToBottomState));
        }
        else {
            getWrapped().setResponseWriter(writer);
        }
    }

    @Override
    public FacesContext getWrapped() {
        return wrapped;
    }

    @Override
    public void release() {
        PrimeRequestContext requestContext = PrimeRequestContext.getCurrentInstance(wrapped);
        if (requestContext != null) {
            requestContext.release();
        }

        super.release();
    }
}