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
import handlers.ErrorHandler
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsError, JsSuccess}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.Session
import utils.mappers.AgentDetailsMapper
import utils.print.AgentDetailsPrinter
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
                                            printHelper: AgentDetailsPrinter,
                                            errorHandler: ErrorHandler
                                          )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad(): Action[AnyContent] = actions.authWithData {
    implicit request =>

      Ok(view(Seq(printHelper(request.userAnswers))))
  }

  def onSubmit(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>

      agentMapper(request.userAnswers) match {
        case JsSuccess(agentDetails, _) =>
          for {
            _ <- estateConnector.addAgentDetails(agentDetails)
          } yield {
            Redirect(appConfig.registrationProgress)
          }
        case JsError(errors) =>
          logger.error(s"[Session ID: ${Session.id(hc)}] Unable to map agent details for submission: $errors")
          Future.successful(InternalServerError(errorHandler.internalServerErrorTemplate))
      }
  }

}
