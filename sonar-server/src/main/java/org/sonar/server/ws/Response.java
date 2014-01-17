/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2013 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.ws;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * HTTP response
 *
 * @since 4.2
 */
public class Response {

  private int httpStatus = HttpServletResponse.SC_OK;
  private final Writer writer;

  public Response(Writer writer) {
    this.writer = writer;
  }

  public JsonWriter newJsonWriter() {
    return JsonWriter.of(writer);
  }

  public XmlWriter newXmlWriter() {
    return XmlWriter.of(writer);
  }

  public Writer writer() {
    return writer;
  }

  public int status() {
    return httpStatus;
  }

  public Response setStatus(int httpStatus) {
    this.httpStatus = httpStatus;
    return this;
  }
}