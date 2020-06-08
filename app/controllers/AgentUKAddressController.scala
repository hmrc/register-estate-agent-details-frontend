/*
 * Copyright 2020 HM Revenue & Customs
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
import forms.AgentUKAddressFormProvider
import javax.inject.Inject
import models.{Mode, NormalMode}
import navigation.Navigator
import pages.{AgentNamePage, AgentUKAddressPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.AgentUKAddressView

import scala.concurrent.{ExecutionContext, Future}

class AgentUKAddressController @Inject()(
                                      override val messagesApi: MessagesApi,
                                      sessionRepository: SessionRepository,
                                      @EstateRegistration navigator: Navigator,
                                      identify: IdentifierAction,
                                      getData: DataRetrievalAction,
                                      requireData: DataRequiredAction,
                                      requiredAnswer: RequiredAnswerActionProvider,
                                      formProvider: AgentUKAddressFormProvider,
                                      val controllerComponents: MessagesControllerComponents,
                                      view: AgentUKAddressView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions() =
    identify andThen getData andThen requireData andThen
      requiredAnswer(RequiredAnswer(AgentNamePage, routes.AgentNameController.onPageLoad(NormalMode)))

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = actions() {
    implicit request =>

      val agencyName = request.userAnswers.get(AgentNamePage).get

      val preparedForm = request.userAnswers.get(AgentUKAddressPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, agencyName))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = actions().async {
    implicit request =>

      val agencyName = request.userAnswers.get(AgentNamePage).get

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode, agencyName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AgentUKAddressPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AgentUKAddressPage, mode, updatedAnswers))
      )
  }
}
