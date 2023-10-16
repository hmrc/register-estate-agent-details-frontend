import sbt.*

object AppDependencies {

  private val mongoHmrcVersion = "1.3.0"
  private val bootstrapVersion = "7.22.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"             % mongoHmrcVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc"             % "7.23.0-play-28",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.13.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"     % bootstrapVersion,
    "uk.gov.hmrc"       %% "domain"                         % "8.3.0-play-28"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                 %% "bootstrap-test-play-28"   % bootstrapVersion,
    "uk.gov.hmrc.mongo"           %% "hmrc-mongo-test-play-28"  % mongoHmrcVersion,
    "org.scalatest"               %% "scalatest"                % "3.2.17",
    "org.scalatestplus"           %% "scalacheck-1-17"          % "3.2.17.0",
    "org.jsoup"                   %  "jsoup"                    % "1.16.1",
    "io.github.wolfendale"        %% "scalacheck-gen-regexp"    % "1.1.0",
    "org.wiremock"                %  "wiremock-standalone"      % "3.2.0",
    "org.mockito"                 %% "mockito-scala-scalatest"  % "1.17.27",
    "com.vladsch.flexmark"        % "flexmark-all"              % "0.64.8"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

}
