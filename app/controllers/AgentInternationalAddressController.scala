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
import forms.AgentInternationalAddressFormProvider
import javax.inject.Inject
import models.{Mode, NormalMode}
import navigation.Navigator
import pages.{AgentInternationalAddressPage, AgentNamePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.countryOptions.CountryOptionsNonUK
import views.html.AgentInternationalAddressView

import scala.concurrent.{ExecutionContext, Future}

class AgentInternationalAddressController @Inject()(
                                      override val messagesApi: MessagesApi,
                                      sessionRepository: SessionRepository,
                                      @EstateRegistration navigator: Navigator,
                                      actions: Actions,
                                      requiredAnswer: RequiredAnswerActionProvider,
                                      formProvider: AgentInternationalAddressFormProvider,
                                      val controllerComponents: MessagesControllerComponents,
                                      view: AgentInternationalAddressView,
                                      val countryOptions: CountryOptionsNonUK
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form = formProvider()

  private val agentNameRequired = requiredAnswer(RequiredAnswer(AgentNamePage, routes.AgentNameController.onPageLoad(NormalMode)))

  def onPageLoad(mode: Mode): Action[AnyContent] = actions.authWithData.andThen(agentNameRequired) {
    implicit request =>

      val agencyName = request.userAnswers.get(AgentNamePage).get

      val preparedForm = request.userAnswers.get(AgentInternationalAddressPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, countryOptions.options, mode, agencyName))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = actions.authWithData.andThen(agentNameRequired).async {
    implicit request =>

      val agencyName = request.userAnswers.get(AgentNamePage).get

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, countryOptions.options, mode, agencyName))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AgentInternationalAddressPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AgentInternationalAddressPage, mode, updatedAnswers))
      )
  }
}
