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

package nl.surfnet.coin.selfservice.dao.impl;

import static junit.framework.Assert.assertEquals;

import java.util.List;

import nl.surfnet.coin.selfservice.dao.StatisticDao;
import nl.surfnet.coin.selfservice.domain.ChartSerie;

import org.junit.Test;

public class MockStatisticDaoImplTest {

  private StatisticDao dao = new MockStatisticDaoImpl();
  
  @Test
  public void testConvertStatResultsToChartSeries() throws Exception {
    List<ChartSerie> series = dao.getLoginsPerSpPerDay("http://mock-idp");
    assertEquals(13, series.size());

  }

}