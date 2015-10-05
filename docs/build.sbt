import com.typesafe.sbt.SbtSite.SiteKeys._

tutSettings

site.settings

site.addMappingsToSiteDir(tut, "_tut")

site.addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), "api")

includeFilter in makeSite := "*.yml" | "*.md" | "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js"