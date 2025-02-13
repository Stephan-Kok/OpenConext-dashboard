package dashboard.manage;

import org.junit.Test;
import dashboard.domain.IdentityProvider;
import dashboard.domain.ServiceProvider;

import java.util.List;

import static org.junit.Assert.*;

//All functional tests reside in UrlResourceManageTest
public class ClassPathResourceManageTest {

  private ClassPathResourceManage subject = new ClassPathResourceManage();

  @Test
  public void testIdentityProviders() {
    List<IdentityProvider> identityProviders = subject.getAllIdentityProviders();
    assertEquals(194, identityProviders.size());
  }

  @Test
  public void testServiceProviders() {
    List<ServiceProvider> serviceProviders = subject.getAllServiceProviders();
    assertEquals(1292, serviceProviders.size());

    ServiceProvider surfcloud = serviceProviders.stream().filter(sp -> sp.getId()
      .equals("https://teams.surfconext.nl/shibboleth")).findFirst().get();
    assertEquals(0, surfcloud.getArp().getAttributes().size());

  }
}
