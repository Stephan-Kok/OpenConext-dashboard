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

package nl.surfnet.coin.selfservice.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import nl.surfnet.coin.selfservice.domain.Provider;
import nl.surfnet.coin.selfservice.service.ProviderService;

/**
 * Provider Service that can query multiple ProviderServices and will combine the results.
 */
public class CompositeProviderService implements ProviderService {

  private List<ProviderService> providerServices;

  @Override
  public List<Provider> getProviders(String idpId) {
    List<Provider> ret = new ArrayList<Provider>();
    for (ProviderService p : providerServices) {
      final List<Provider> providers = p.getProviders(idpId);
      if (CollectionUtils.isNotEmpty(providers)) {
        ret.addAll(providers);
      }
    }

    return ret;
  }

  public void setProviderServices(List<ProviderService> providerServices) {
    this.providerServices = providerServices;
  }

}