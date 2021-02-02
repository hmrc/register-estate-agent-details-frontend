/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package navigation

import javax.inject.Inject
import models.{Mode, UserAnswers}
import pages._
import play.api.mvc.Call

class AgentNavigator @Inject()() extends Navigator {

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call =
    routes(mode)(page)(userAnswers)

  private def simpleNavigation(mode: Mode): PartialFunction[Page, Call] = {
    case AgentInternalReferencePage => controllers.routes.AgentNameController.onPageLoad(mode)
    case AgentNamePage => controllers.routes.AgentUKAddressYesNoController.onPageLoad(mode)
    case AgentUKAddressPage => controllers.routes.AgentTelephoneNumberController.onPageLoad(mode)
    case AgentInternationalAddressPage => controllers.routes.AgentTelephoneNumberController.onPageLoad(mode)
    case AgentTelephoneNumberPage => controllers.routes.CheckYourAnswersController.onPageLoad()
  }

  private def yesNoNavigation(mode: Mode): PartialFunction[Page, UserAnswers => Call] = {
    case AgentUKAddressYesNoPage => ua =>
        yesNoNav(ua, AgentUKAddressYesNoPage,
        controllers.routes.AgentUKAddressController.onPageLoad(mode),
        controllers.routes.AgentInternationalAddressController.onPageLoad(mode))
  }

  private def yesNoNav(ua: UserAnswers, fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call): Call = {
    ua.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }

  def routes(mode: Mode): PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation(mode) andThen (c => (_: UserAnswers) => c) orElse
      yesNoNavigation(mode)

}

