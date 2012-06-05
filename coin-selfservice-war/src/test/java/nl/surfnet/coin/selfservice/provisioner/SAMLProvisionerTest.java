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

package nl.surfnet.coin.selfservice.provisioner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.service.impl.FederationProviderService;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class SAMLProvisionerTest {

  @Mock
  private FederationProviderService fedProvSvc;

  @InjectMocks
  private SAMLProvisioner s;

  @Before
  public void before() {
    s = new SAMLProvisioner();
    MockitoAnnotations.initMocks(this);
  }
  @Test
  public void testProvision() throws Exception {

    final Assertion a = readAssertionFromFile("assertion.xml");

    IdentityProvider idp = new IdentityProvider("https://surfguest.nl", "institutionId", "idp-name");
    IdentityProvider idp2 = new IdentityProvider("https://surfguest.nl/2", "institutionId", "idp-name2");
    IdentityProvider idp3 = new IdentityProvider("https://surfguest.nl/3", "institutionId", "idp-name3");

    when(fedProvSvc.getIdentityProvider("https://surfguest.nl")).thenReturn(idp);
    when(fedProvSvc.getInstituteIdentityProviders("institutionId")).thenReturn(Arrays.asList(idp, idp2, idp3));

    CoinUser cu = (CoinUser) s.provisionUser(a);

//    verify(fedProvSvc).getIdentityProvider("boo");
    assertEquals("urn:collab:person:surfguest.nl:gvanderploeg", cu.getUsername());
    assertEquals("https://surfguest.nl", cu.getIdp());
    assertEquals("surfguest.nl", cu.getSchacHomeOrganization());
    assertEquals("Geert van der Ploeg", cu.getDisplayName());
    assertEquals("gvanderploeg@iprofs.nl", cu.getEmail());
    assertEquals("urn:collab:person:surfguest.nl:gvanderploeg", cu.getUid());
    assertThat(cu.getInstitutionId(), is("institutionId"));

    assertThat("Multiple idps belonging to the same institution should be linked",
        cu.getInstitutionIdps().size(), is(3));
  }

  private Assertion readAssertionFromFile(String filename) throws ConfigurationException, IOException,
      UnmarshallingException,
      SAXException, ParserConfigurationException {
    DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
    f.setNamespaceAware(true);
    DefaultBootstrap.bootstrap();
    return (Assertion) readFromFile(f.newDocumentBuilder(), new ClassPathResource(filename).getFile());
  }


  /**
   OpenSAML Helper method to read an XML object from a DOM element.
   */
  public static XMLObject fromElement (Element element)
      throws IOException, UnmarshallingException, SAXException
  {
    return Configuration.getUnmarshallerFactory()
        .getUnmarshaller (element).unmarshall (element);
  }

  /**
   OpenSAML Helper method to read an XML object from a file.
   */
  public XMLObject readFromFile (DocumentBuilder builder, File file)
      throws IOException, UnmarshallingException, SAXException
  {
    return fromElement (builder.parse (file).getDocumentElement ());
  }
}