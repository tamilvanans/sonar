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
package org.sonar.plugins.dbcleaner.api;

import org.sonar.api.task.TaskExtension;

import com.google.common.annotations.Beta;

/**
 * @since 2.14
 */
@Beta
public interface PurgeTask extends TaskExtension {
  /**
   * Purges the data related to a tree of resources.
   *
   * Exceptions are logged and are not thrown again, so this method fails only on {@link Error}s.
   *
   * @param resourceId the root of the tree
   * @return this
   */
  PurgeTask purge(long resourceId);

  /**
   * Completely deletes a tree of resources.
   *
   * @param resourceId the root of the tree
   * @return this
   */
  PurgeTask delete(long resourceId);
}
