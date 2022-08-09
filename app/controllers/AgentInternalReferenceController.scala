/*
 * Copyright 2022 HM Revenue & Customs
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

import config.annotations.EstateRegistration
import controllers.actions._
import forms.AgentInternalReferenceFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.{AgentARNPage, AgentInternalReferencePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.AgentInternalReferenceView

import scala.concurrent.{ExecutionContext, Future}

class AgentInternalReferenceController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: SessionRepository,
                                        @EstateRegistration navigator: Navigator,
                                        formProvider: AgentInternalReferenceFormProvider,
                                        actions: Actions,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: AgentInternalReferenceView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = actions.authWithData {
    implicit request =>

      val preparedForm = request.userAnswers.get(AgentInternalReferencePage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = actions.authWithData.async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AgentInternalReferencePage, value))
            updatedAnswersWithARN <- Future.fromTry(updatedAnswers.set(AgentARNPage, request.agentReferenceNumber))
            _              <- sessionRepository.set(updatedAnswersWithARN)
          } yield Redirect(navigator.nextPage(AgentInternalReferencePage, mode, updatedAnswers))
      )
  }
}
