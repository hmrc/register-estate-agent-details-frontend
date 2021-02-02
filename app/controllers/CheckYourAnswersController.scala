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

package controllers

import com.google.inject.Inject
import config.FrontendAppConfig
import connector.EstateConnector
import controllers.actions.Actions
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.countryOptions.CountryOptions
import utils.mappers.AgentDetailsMapper
import utils.{CheckYourAnswersHelper, Session}
import viewmodels.AnswerSection
import views.html.CheckYourAnswersView

import scala.concurrent.{ExecutionContext, Future}

class CheckYourAnswersController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            val appConfig: FrontendAppConfig,
                                            actions: Actions,
                                            agentMapper: AgentDetailsMapper,
                                            estateConnector: EstateConnector,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: CheckYourAnswersView,
                                            countryOptions : CountryOptions
                                          ) (implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad(): Action[AnyContent] = actions.authWithData {
    implicit request =>

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(request.userAnswers)

      val sections = Seq(
        AnswerSection(
          None,
          Seq(
            checkYourAnswersHelper.agentInternalReference,
            checkYourAnswersHelper.agentName,
            checkYourAnswersHelper.agentUKAddressYesNo,
            checkYourAnswersHelper.agentUKAddress,
            checkYourAnswersHelper.agentInternationalAddress,
            checkYourAnswersHelper.agentTelephoneNumber
          ).flatten
        )
      )

      Ok(view(sections))
  }

  def onSubmit(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>

      agentMapper(request.userAnswers) match {
        case Some(agentDetails) =>
          for {
            _ <- estateConnector.addAgentDetails(agentDetails)
          } yield {
            Redirect(appConfig.registrationProgress)
          }
        case None =>
          logger.warn(s"[Session ID: ${Session.id(hc)}] Unable to generate agent details to submit.")
          Future.successful(InternalServerError)
      }
  }

}
