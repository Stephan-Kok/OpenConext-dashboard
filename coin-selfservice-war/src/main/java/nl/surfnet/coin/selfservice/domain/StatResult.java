/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.surfnet.coin.selfservice.domain;

import java.util.Date;

/**
* Represents the result row of the mysql query to get login statistics
*/
public class StatResult implements Comparable<StatResult> {

  private String spEntityId;
  private Date date;
  private Integer logins;

  public String getSpEntityId() {
    return spEntityId;
  }

  public void setSpEntityId(String spEntityId) {
    this.spEntityId = spEntityId;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Integer getLogins() {
    return logins;
  }

  public void setLogins(Integer logins) {
    this.logins = logins;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    StatResult that = (StatResult) o;

    if (date != null ? !date.equals(that.date) : that.date != null) {
      return false;
    }
    if (logins != null ? !logins.equals(that.logins) : that.logins != null) {
      return false;
    }
    if (spEntityId != null ? !spEntityId.equals(that.spEntityId) : that.spEntityId != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = spEntityId != null ? spEntityId.hashCode() : 0;
    result = 31 * result + (date != null ? date.hashCode() : 0);
    result = 31 * result + (logins != null ? logins.hashCode() : 0);
    return result;
  }

  @Override
  public int compareTo(StatResult that) {
    if (this == that) {
      return 0;
    }

    final String thisSP = this.getSpEntityId();
    final String thatSP = that.getSpEntityId();
    final Date thisDate = this.getDate();
    final Date thatDate = that.getDate();

    if (thisSP.equals(thatSP)) {
      return thisDate.compareTo(thatDate);
    } else {
      return thisSP.compareTo(thatSP);
    }
  }

}