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
package org.sonar.core.i18n;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

class DurationLabel {

  private static String durationPrefix = "duration.";
  private static String seconds = "seconds";
  private static String minute = "minute";
  private static String minutes = "minutes";
  private static String hour = "hour";
  private static String hours = "hours";
  private static String day = "day";
  private static String days = "days";
  private static String month = "month";
  private static String months = "months";
  private static String year = "year";
  private static String years = "years";

  private DurationLabel() {
    // Utility class
  }

  public static Result label(long durationInMillis) {
    double nbSeconds = durationInMillis / 1000.0;
    double nbMinutes = nbSeconds / 60;
    double nbHours = nbMinutes / 60;
    double nbDays = nbHours / 24;
    double nbYears = nbDays / 365;
    return getMessage(nbSeconds, nbMinutes, nbHours, nbDays, nbYears);
  }


  private static Result getMessage(double nbSeconds, double nbMinutes, double nbHours, double nbDays, double nbYears) {
    if (nbSeconds < 45) {
      return message(DurationLabel.seconds);
    } else if (nbSeconds < 90) {
      return message(DurationLabel.minute);
    } else if (nbMinutes < 45) {
      return message(DurationLabel.minutes, Math.round(nbMinutes));
    } else if (nbMinutes < 90) {
      return message(DurationLabel.hour);
    } else if (nbHours < 24) {
      return message(DurationLabel.hours, Math.round(nbHours));
    } else if (nbHours < 48) {
      return message(DurationLabel.day);
    } else if (nbDays < 30) {
      return message(DurationLabel.days, Double.valueOf(Math.floor(nbDays)).longValue());
    } else if (nbDays < 60) {
      return message(DurationLabel.month);
    } else if (nbDays < 365) {
      return message(DurationLabel.months, Double.valueOf(Math.floor(nbDays / 30)).longValue());
    } else if (nbYears < 2) {
      return message(DurationLabel.year);
    }
    return message(DurationLabel.years, Double.valueOf(Math.floor(nbYears)).longValue());
  }

  private static Result message(String key) {
    return message(key, null);
  }

  private static Result message(String key, @Nullable Long value) {
    return new Result(durationPrefix + key, value);
  }

  static class Result {
    private String key;
    private Long value;

    public Result(String key, @Nullable Long value) {
      this.key = key;
      this.value = value;
    }

    public String key() {
      return key;
    }

    @CheckForNull
    public Long value() {
      return value;
    }
  }

}
