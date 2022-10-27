import sbt._

object AppDependencies {
  import play.core.PlayVersion

  private lazy val mongoHmrcVersion = "0.73.0"

  private val bootstrapVersion = "5.25.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"             % mongoHmrcVersion,
    "uk.gov.hmrc"       %% "bootstrap-test-play-28"         % bootstrapVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc"             % "1.1.0-play-28",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.11.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"     % "5.24.0",
    "com.typesafe.play" %% "play-json-joda"                 % "2.9.3",
    "uk.gov.hmrc"       %% "domain"                         % "8.1.0-play-28"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"               %% "scalatest"                % "3.2.14",
    "org.scalatestplus.play"      %% "scalatestplus-play"       % "5.1.0",
    "org.scalatestplus"           %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
    "org.pegdown"                 %  "pegdown"                  % "1.6.0",
    "org.jsoup"                   %  "jsoup"                    % "1.15.3",
    "com.typesafe.play"           %% "play-test"                % PlayVersion.current,
    "org.scalacheck"              %% "scalacheck"               % "1.17.0",
    "wolfendale"                  %% "scalacheck-gen-regexp"    % "0.1.2",
    "com.github.tomakehurst"      %  "wiremock-standalone"      % "2.27.2",
    "org.pegdown"                 %  "pegdown"                  % "1.6.0",
    "org.mockito"                 %% "mockito-scala-scalatest"  % "1.17.12",
    "org.scalacheck"              %% "scalacheck"               % "1.17.0",
    "com.vladsch.flexmark"        % "flexmark-all"              % "0.62.2"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

  val akkaVersion = "2.6.10"
  val akkaHttpVersion = "10.1.12"

  val overrides: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-stream_2.12"     % akkaVersion,
    "com.typesafe.akka" %% "akka-protobuf_2.12"   % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j_2.12"      % akkaVersion,
    "com.typesafe.akka" %% "akka-actor_2.12"      % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core_2.12"  % akkaHttpVersion,
    "commons-codec"     %  "commons-codec"        % "1.12"
  )
}
