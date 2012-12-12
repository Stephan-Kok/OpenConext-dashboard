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
package nl.surfnet.coin.selfservice.util;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import nl.surfnet.coin.api.client.OAuthVersion;
import nl.surfnet.coin.api.client.OpenConextOAuthClient;
import nl.surfnet.coin.api.client.domain.Email;
import nl.surfnet.coin.api.client.domain.Group;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Person;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

/**
 * OpenConextOAuthClientMock.java
 * 
 */
public class OpenConextOAuthClientMock implements OpenConextOAuthClient, InitializingBean {

  public enum Users {
    /*
     * ROLE_IDP_SURFCONEXT_ADMIN=IdP Institution Administrator
     */
    ADMIN_IDP_SURFCONEXT("adminidpsc"),
    /*
     * ROLE_IDP_LICENSE_ADMIN=IdP License Administrator
     */
    ADMIN_IDP_LICENSE("adminidpli"),
    /*
     * ROLE_DISTRIBUTION_CHANNEL_ADMIN=Distribution Channel Administrator
     */
    ADMIN_DISTRIBUTIE_CHANNEL("admindk"),
    /*
     * ROLE_USER=Distribution Channel User
     */
    USER("user"),
    /*
     * Both IdP admins
     */
    ADMIN_IDP_ADMIN("adminidp"),

    ALL("NA");

    private String user;

    private Users(String user) {
      this.user = user;
    }

    public String getUser() {
      return user;
    }

    public static Users fromUser(String userName) {
      Users[] values = Users.values();
      for (Users user : values) {
        if (user.getUser().equalsIgnoreCase(userName)) {
          return user;
        }
      }
      return ALL;
    }
  }

  private String adminLicentieIdPTeam;
  private String adminSurfConextIdPTeam;
  private String adminDistributionTeam;

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.OpenConextOAuthClient#isAccessTokenGranted(java
   * .lang.String)
   */
  @Override
  public boolean isAccessTokenGranted(String userId) {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.api.client.OpenConextOAuthClient#getAuthorizationUrl()
   */
  @Override
  public String getAuthorizationUrl() {
    throw new RuntimeException("Not implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.OpenConextOAuthClient#oauthCallback(javax.servlet
   * .http.HttpServletRequest, java.lang.String)
   */
  @Override
  public void oauthCallback(HttpServletRequest request, String onBehalfOf) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.OpenConextOAuthClient#getPerson(java.lang.String
   * , java.lang.String)
   */
  @Override
  public Person getPerson(String userId, String onBehalfOf) {
    throw new RuntimeException("Not implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.OpenConextOAuthClient#getGroupMembers(java.lang
   * .String, java.lang.String)
   */
  @Override
  public List<Person> getGroupMembers(String groupId, String onBehalfOf) {
    List<Person> persons = new ArrayList<Person>();
    String group = groupId.substring(groupId.lastIndexOf(":") + 1);
    persons.add(createPerson("John Doe", "john.doe@"+group));
    persons.add(createPerson("Pitje Puck", "p.p@"+group));
    persons.add(createPerson("Yan Yoe", "yan@"+group));
    return persons;
  }

  private Person createPerson(String displayName, String email) {
    Person p = new Person();
    p.setDisplayName(displayName);
    p.setEmails(Collections.singleton(new Email(email)));
    return p;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.OpenConextOAuthClient#getGroups(java.lang.String
   * , java.lang.String)
   */
  @Override
  public List<Group> getGroups(String userId, String onBehalfOf) {
    throw new RuntimeException("Not implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.OpenConextOAuthClient#getGroups20(java.lang.
   * String, java.lang.String)
   */
  @Override
  public List<Group20> getGroups20(String userId, String onBehalfOf) {
    final Users user = Users.fromUser(userId);
    switch (user) {
    case ADMIN_DISTRIBUTIE_CHANNEL:
      return asList(createGroup20(adminDistributionTeam));
    case ADMIN_IDP_LICENSE:
      return asList(createGroup20(adminLicentieIdPTeam));
    case ADMIN_IDP_SURFCONEXT:
      return asList(createGroup20(adminSurfConextIdPTeam));
    case ADMIN_IDP_ADMIN:
      return asList(createGroup20(adminLicentieIdPTeam), createGroup20(adminSurfConextIdPTeam));
    case USER:
      return new ArrayList<Group20>();
    case ALL:
      return asList(createGroup20(adminLicentieIdPTeam), createGroup20(adminSurfConextIdPTeam), createGroup20(adminDistributionTeam));
    default:
      throw new RuntimeException("Unknown");
    }

  }

  private Group20 createGroup20(String id) {
    return new Group20(id, id.substring(id.lastIndexOf(":") + 1), id);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.OpenConextOAuthClient#getGroup20(java.lang.String
   * , java.lang.String, java.lang.String)
   */
  @Override
  public Group20 getGroup20(String userId, String groupId, String onBehalfOf) {
    throw new RuntimeException("Not implemented");
  }

  /*
   * The following is needed to be conform the contract of the real
   * OpenConextOAuthClient. For the same reason we get the values our selves
   * from the properties files, as we can't inject them
   */

  public void setCallbackUrl(String url) {
  }

  public void setConsumerSecret(String secret) {
  }

  public void setConsumerKey(String key) {
  }

  public void setEndpointBaseUrl(String url) {
  }

  public void setVersion(OAuthVersion v) {
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Properties prop = new Properties();
    prop.load(new ClassPathResource("coin-selfservice.properties").getInputStream());
    adminLicentieIdPTeam = prop.getProperty("admin.licentie.idp.teamname");
    adminSurfConextIdPTeam = prop.getProperty("admin.surfconext.idp.teamname");
    adminDistributionTeam = prop.getProperty("admin.distribution.channel.teamname");
  }

}
