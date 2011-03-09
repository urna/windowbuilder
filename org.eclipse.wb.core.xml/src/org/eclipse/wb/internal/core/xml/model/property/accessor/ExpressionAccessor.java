/*******************************************************************************
 * Copyright (c) 2011 Google, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Google, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.wb.internal.core.xml.model.property.accessor;

import org.eclipse.wb.internal.core.model.property.Property;
import org.eclipse.wb.internal.core.utils.execution.ExecutionUtils;
import org.eclipse.wb.internal.core.utils.xml.DocumentElement;
import org.eclipse.wb.internal.core.xml.model.XmlObjectInfo;
import org.eclipse.wb.internal.core.xml.model.description.AbstractDescription;
import org.eclipse.wb.internal.core.xml.model.property.XmlProperty;

/**
 * Accessor for {@link XmlProperty} value.
 * <p>
 * FIXME This class is too specific. It is better to rename it to "AttributeExpressionAccessor" and
 * introduce abstract "ExpressionAccessor" instead.
 * 
 * @author scheglov_ke
 * @coverage XML.model.property
 */
public abstract class ExpressionAccessor extends AbstractDescription {
  protected final String m_attribute;

  ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  ////////////////////////////////////////////////////////////////////////////
  public ExpressionAccessor(String attribute) {
    m_attribute = attribute;
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // Access
  //
  /////////////////////////////////////////////////////////////////////////////
  /**
   * @return the name of attribute.
   */
  public final String getAttribute() {
    return m_attribute;
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // Value
  //
  ////////////////////////////////////////////////////////////////////////////
  /**
   * @return <code>true</code> if this attribute is set.
   */
  public boolean isModified(XmlObjectInfo object) throws Exception {
    // attribute
    if (getExpression(object) != null) {
      return true;
    }
    // sub-element
    {
      DocumentElement element = getElement(object);
      String propertyName = element.getTag() + "." + m_attribute;
      if (element.getChild(propertyName, true) != null) {
        return true;
      }
    }
    // no value
    return false;
  }

  /**
   * @return the {@link String} expression of attribute, may be <code>null</code> if not set.
   */
  public String getExpression(XmlObjectInfo object) {
    DocumentElement element = getElement(object);
    return element.getAttribute(m_attribute);
  }

  /**
   * Updates attribute of given {@link XmlObjectInfo} to have given expression.
   * 
   * @param expression
   *          the new expression of attribute, may be <code>null</code> to remove attribute.
   */
  public void setExpression(XmlObjectInfo object, String expression) throws Exception {
    DocumentElement element = getElement(object);
    element.setAttribute(m_attribute, expression);
    ExecutionUtils.refresh(object);
  }

  /**
   * @return the {@link DocumentElement} to use to get/set attribute.
   */
  protected DocumentElement getElement(XmlObjectInfo object) {
    return object.getElement();
  }

  /**
   * @return the value of property in given {@link XmlObjectInfo}, may be
   *         {@link Property#UNKNOWN_VALUE} is attribute was not set.
   */
  public Object getValue(XmlObjectInfo object) throws Exception {
    return object.getAttributeValue(m_attribute);
  }

  /**
   * @return the default value of property in given {@link XmlObjectInfo}.
   */
  public Object getDefaultValue(XmlObjectInfo object) throws Exception {
    return Property.UNKNOWN_VALUE;
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // Default value
  //
  ////////////////////////////////////////////////////////////////////////////
  /**
   * Property can be marked with this tag (with value "true") to specify, that
   * {@link ExpressionAccessor} should not try to fetch default value. For example in eSWT
   * <code>Menu.getVisible()</code> contains bug - infinite recursion, so we should prevent its
   * invocation.
   */
  public static final String NO_DEFAULT_VALUE_TAG = "noDefaultValue";
}