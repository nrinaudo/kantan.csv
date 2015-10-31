import com.typesafe.sbt.SbtSite.SiteKeys._
import com.typesafe.sbt.SbtGhPages.GhPagesKeys._

tutSettings

site.settings

site.addMappingsToSiteDir(tut, "_tut")

site.addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), "api")

includeFilter in makeSite := "*.yml" | "*.md" | "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.eot" | "*.svg" | "*.ttf" | "*.woff" | "*.woff2" | "*.otf"

ghpages.settings

git.remoteRepo := "git@github.com:nrinaudo/tabulate.git"

ghpagesNoJekyll := false
