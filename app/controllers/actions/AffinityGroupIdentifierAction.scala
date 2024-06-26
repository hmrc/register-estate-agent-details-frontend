/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.actions

import com.google.inject.Inject
import config.FrontendAppConfig
import models.requests.IdentifierRequest
import play.api.Logging
import play.api.mvc.Results._
import play.api.mvc.{Request, Result, _}
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.{HeaderCarrier, UnauthorizedException}
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.Session

import scala.concurrent.{ExecutionContext, Future}

class AffinityGroupIdentifierAction[A] @Inject()(action: Action[A],
                                                 estatesAuthFunctions: EstatesAuthorisedFunctions,
                                                 config: FrontendAppConfig
                                                ) extends Action[A] with Logging {

  private def authoriseAgent(request : Request[A],
                             enrolments : Enrolments,
                             internalId : String,
                             action: Action[A]
                            ): Future[Result] = {

    def redirectToCreateAgentServicesAccount(reason: String): Future[Result] = {
      implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
      logger.info(s"[Session ID: ${Session.id(hc)}][authoriseAgent]: Agent services account required - $reason")
      Future.successful(Redirect(config.createAgentServicesAccountUrl))
    }

    val hmrcAgentEnrolmentKey = "HMRC-AS-AGENT"
    val arnIdentifier = "AgentReferenceNumber"

    enrolments.getEnrolment(hmrcAgentEnrolmentKey).fold(
      redirectToCreateAgentServicesAccount("missing HMRC-AS-AGENT enrolment group")
    ){
      agentEnrolment =>
        agentEnrolment.getIdentifier(arnIdentifier).fold(
          redirectToCreateAgentServicesAccount("missing agent reference number")
        ){
          enrolmentIdentifier =>
            val arn = enrolmentIdentifier.value

            if(arn.isEmpty) {
              redirectToCreateAgentServicesAccount("agent reference number is empty")
            } else {
              action(IdentifierRequest(request, internalId, arn))
            }
      }
    }
  }

  def apply(request: Request[A]): Future[Result] = {

        implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    val retrievals = Retrievals.internalId and
                     Retrievals.affinityGroup and
                     Retrievals.allEnrolments

    estatesAuthFunctions.authorised().retrieve(retrievals) {
      case Some(internalId) ~ Some(Agent) ~ enrolments =>
        logger.info(s"[Session ID: ${Session.id(hc)}] successfully identified as an Agent")
        authoriseAgent(request, enrolments, internalId, action)
      case Some(_) ~ Some(Organisation) ~ _ =>
        logger.info(s"[Session ID: ${Session.id(hc)}] identified as Organisation user. Kicking out of service.")
        Future.successful(Redirect(config.loginUrl))
      case Some(_) ~ _ ~ _ =>
        logger.info(s"[Session ID: ${Session.id(hc)}] Unauthorised due to affinityGroup being Individual")
        Future.successful(Redirect(controllers.routes.UnauthorisedController.onPageLoad))
      case _ =>
        logger.warn(s"[Session ID: ${Session.id(hc)}] Unable to retrieve internal id")
        throw new UnauthorizedException("Unable to retrieve internal Id")
    } recover estatesAuthFunctions.recoverFromAuthorisation
  }

  override def parser: BodyParser[A] = action.parser
  override implicit def executionContext: ExecutionContext = action.executionContext

}
