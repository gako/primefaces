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
package org.primefaces.model.menu;

/**
 * Default implementation of a MenuModel optimized for static menus that do not
 * change once built.
 */
public class DefaultMenuModel extends BaseMenuModel {

    private static final long serialVersionUID = 1L;

    private boolean generated = false;

    protected String seed;

    public DefaultMenuModel() {
	this.seed = null;
    }

    public DefaultMenuModel(String seed) {
	this.seed = seed;
    }

    @Override
    public void generateUniqueIds() {
	if (!generated) {
	    super.generateUniqueIds(getElements(), seed);
	    generated = true;
	}
    }

    public void expandAll() {
	for (MenuElement element : getElements()) {
	    if (element instanceof DefaultSubMenu) {
		((DefaultSubMenu) element).setExpanded(true);
		expandAll((DefaultSubMenu) element);
	    }
	}
    }

    protected void expandAll(Submenu menu) {
	for (Object element : menu.getElements()) {
	    if (element instanceof DefaultSubMenu) {
		((DefaultSubMenu) element).setExpanded(true);
		expandAll((DefaultSubMenu) element);
	    }
	}
    }

    public int getTotalMenuItemSize() {
	int totalSize = 0;
	for (MenuElement element : getElements()) {
	    if (element instanceof Submenu) {
		totalSize += getTotalMenuItemSize((Submenu) element);
	    } else {
		totalSize++;
	    }
	}
	return totalSize;
    }

    protected int getTotalMenuItemSize(Submenu menu) {
	int totalSize = 0;
	for (Object element : menu.getElements()) {
	    if (element instanceof Submenu) {
		totalSize += getTotalMenuItemSize((Submenu) element);
	    } else {
		totalSize++;
	    }
	}

	return totalSize;
    }
}
