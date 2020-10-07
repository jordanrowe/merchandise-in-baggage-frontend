import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-27" % "2.24.0",
    "uk.gov.hmrc"             %% "play-frontend-hmrc"         % "0.17.0-play-27",
    "uk.gov.hmrc"             %% "play-frontend-govuk"        % "0.49.0-play-27",
    "uk.gov.hmrc"             %% "simple-reactivemongo"       % "7.30.0-play-27",
    "com.github.pureconfig"   %% "pureconfig"                 % "0.13.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-27"  % "2.24.0"   % Test,
    "org.scalatest"           %% "scalatest"               % "3.2.2"    % Test,
    "org.jsoup"               %  "jsoup"                   % "1.10.2"   % Test,
    "com.typesafe.play"       %% "play-test"               % current    % Test,
    "com.vladsch.flexmark"    %  "flexmark-all"            % "0.35.10"  % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"      % "3.1.2"    % "test, it",
    "org.scalatestplus"       %% "scalacheck-1-14"         % "3.2.2.0"  % Test,
    "com.github.tomakehurst"  %  "wiremock-standalone"     % "2.27.1"   % Test
  )
}