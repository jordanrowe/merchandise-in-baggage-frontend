resolvers += MavenRepository("HMRC-open-artefacts-maven2", "https://open.artefacts.tax.service.gov.uk/maven2")
resolvers += Resolver.url("HMRC-open-artefacts-ivy2", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(
  Resolver.ivyStylePatterns
)

// this scala-xml version scheme is to get around some library dependency conflicts, remove once we get rid of scalapact
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always

addSbtPlugin("uk.gov.hmrc"        % "sbt-auto-build"           % "3.21.0")
addSbtPlugin("uk.gov.hmrc"        % "sbt-distributables"       % "2.5.0")
addSbtPlugin("org.playframework"  % "sbt-plugin"               % "3.0.2")
addSbtPlugin("io.github.irundaia" % "sbt-sassify"              % "1.5.2")
addSbtPlugin("org.scalameta"      % "sbt-scalafmt"             % "2.5.2")
addSbtPlugin("org.scalastyle"     % "scalastyle-sbt-plugin"    % "1.0.0" exclude ("org.scala-lang.modules", "scala-xml_2.12"))
addSbtPlugin("org.scoverage"      % "sbt-scoverage"            % "2.0.11")
addSbtPlugin("com.itv"            % "sbt-scalapact"            % "3.3.1")
addSbtPlugin("com.timushev.sbt"   % "sbt-updates"              % "0.6.4")
addSbtPlugin("uk.gov.hmrc"        % "sbt-accessibility-linter" % "0.39.0")
