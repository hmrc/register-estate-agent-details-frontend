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

package config

import com.google.inject.AbstractModule
import config.annotations.EstateRegistration
import controllers.actions._
import navigation.{AgentNavigator, Navigator}
import repositories.{DefaultSessionRepository, SessionRepository}
import utils.countryOptions.{CountryOptions, CountryOptionsNonUK}

class Module extends AbstractModule {

  override def configure(): Unit = {

    bind(classOf[DataRetrievalAction]).to(classOf[DataRetrievalActionImpl]).asEagerSingleton()
    bind(classOf[DataRequiredAction]).to(classOf[DataRequiredActionImpl]).asEagerSingleton()

    bind(classOf[CountryOptions]).to(classOf[CountryOptionsNonUK]).asEagerSingleton()
    bind(classOf[SessionRepository]).to(classOf[DefaultSessionRepository]).asEagerSingleton()
    bind(classOf[Navigator]).annotatedWith(classOf[EstateRegistration]).to(classOf[AgentNavigator]).asEagerSingleton()
  }
}
