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
package org.sonar.plugins.core.sensors;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.batch.*;
import org.sonar.api.database.model.Snapshot;
import org.sonar.api.i18n.I18n;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.profiles.Alert;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;
import org.sonar.core.timemachine.Periods;

import java.util.List;
import java.util.Locale;

public class CheckAlertThresholds implements Decorator {

  private static final String VARIATION_METRIC_PREFIX = "new_";
  private static final String VARIATION = "variation";

  private final Snapshot snapshot;
  private final RulesProfile profile;
  private final Periods periods;
  private final I18n i18n;


  public CheckAlertThresholds(Snapshot snapshot, RulesProfile profile, Periods periods, I18n i18n) {
    this.snapshot = snapshot;
    this.profile = profile;
    this.periods = periods;
    this.i18n = i18n;
  }

  @DependedUpon
  public Metric generatesAlertStatus() {
    return CoreMetrics.ALERT_STATUS;
  }

  @DependsUpon
  public String dependsOnVariations() {
    return DecoratorBarriers.END_OF_TIME_MACHINE;
  }

  @DependsUpon
  public List<Metric> dependsUponMetrics() {
    List<Metric> metrics = Lists.newLinkedList();
    for (Alert alert : profile.getAlerts()) {
      metrics.add(alert.getMetric());
    }
    return metrics;
  }


  public boolean shouldExecuteOnProject(Project project) {
    return profile != null
        && profile.getAlerts() != null
        && !profile.getAlerts().isEmpty()
        && ResourceUtils.isRootProject(project);
  }

  public void decorate(final Resource resource, final DecoratorContext context) {
    if (shouldDecorateResource(resource)) {
      decorateResource(context);
    }
  }

  private void decorateResource(DecoratorContext context) {
    Metric.Level globalLevel = Metric.Level.OK;
    List<String> labels = Lists.newArrayList();

    for (final Alert alert : profile.getAlerts()) {
      Measure measure = context.getMeasure(alert.getMetric());
      if (measure != null) {
        Metric.Level level = AlertUtils.getLevel(alert, measure);

        measure.setAlertStatus(level);
        String text = getText(alert, level);
        if (!StringUtils.isBlank(text)) {
          measure.setAlertText(text);
          labels.add(text);
        }

        context.saveMeasure(measure);

        if (Metric.Level.WARN == level && globalLevel != Metric.Level.ERROR) {
          globalLevel = Metric.Level.WARN;

        } else if (Metric.Level.ERROR == level) {
          globalLevel = Metric.Level.ERROR;
        }
      }
    }

    Measure globalMeasure = new Measure(CoreMetrics.ALERT_STATUS, globalLevel);
    globalMeasure.setAlertStatus(globalLevel);
    globalMeasure.setAlertText(StringUtils.join(labels, ", "));
    context.saveMeasure(globalMeasure);
  }

  private boolean shouldDecorateResource(final Resource resource) {
    return ResourceUtils.isRootProject(resource);
  }

  private String getText(Alert alert, Metric.Level level) {
    if (level == Metric.Level.OK) {
      return null;
    }
    return getAlertLabel(alert, level);
  }

  private String getAlertLabel(Alert alert, Metric.Level level) {
    Integer alertPeriod = alert.getPeriod();
    String metric = i18n.message(getLocale(), "metric." + alert.getMetric().getKey() + ".name", alert.getMetric().getName());

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(metric);

    if (alertPeriod != null && !alert.getMetric().getKey().startsWith(VARIATION_METRIC_PREFIX)) {
      String variation = i18n.message(getLocale(), VARIATION, VARIATION).toLowerCase();
      stringBuilder.append(" ").append(variation);
    }

    stringBuilder
        .append(" ").append(alert.getOperator()).append(" ")
        .append(level.equals(Metric.Level.ERROR) ? alert.getValueError() : alert.getValueWarning());

    if (alertPeriod != null){
      stringBuilder.append(" ").append(periods.label(snapshot, alertPeriod));
    }

    return stringBuilder.toString();
  }


  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  private Locale getLocale() {
    return Locale.ENGLISH;
  }

}
