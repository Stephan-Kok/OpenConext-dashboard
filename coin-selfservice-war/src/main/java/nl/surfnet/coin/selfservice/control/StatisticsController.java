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

package nl.surfnet.coin.selfservice.control;

import static java.util.Calendar.YEAR;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.surfnet.cruncher.Cruncher;

/**
 * Controller for statistics
 */
@Controller
@RequestMapping(value = "/stats/*")
public class StatisticsController extends BaseController {

  private static final Logger LOG = LoggerFactory.getLogger(StatisticsController.class);

  @Resource
  private Cruncher cruncher;

  @RequestMapping("/stats.shtml")
  public String stats(ModelMap model, @ModelAttribute(value = SELECTED_IDP) InstitutionIdentityProvider selectedIdp,
                      @RequestParam(value = "spEntityId", required = false) final String selectedSp) {
    model.put(SELECTED_IDP, selectedIdp);

    // default return all statistics for the last two years
    Calendar twoYearsBack = Calendar.getInstance();
    twoYearsBack.roll(YEAR, -2);
    if (StringUtils.isNotBlank(selectedSp)) {
      model.put("login_stats", cruncher.getLoginsByIdpAndSp(twoYearsBack.getTime(), new Date(), selectedIdp.getId(), selectedSp));
    } else {
      model.put("login_stats", cruncher.getLoginsByIdp(twoYearsBack.getTime(), new Date(), selectedIdp.getId()));      
    }
    return "stats/statistics";
  }
}
