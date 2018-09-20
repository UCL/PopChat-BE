// We need to add some extra resolvers to allow us to use the supersafe plugin
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

// Add some plugins for autocomplete in VScode
addSbtPlugin("org.ensime" % "sbt-ensime" % "2.5.1")
addSbtPlugin("com.artima.supersafe" % "sbtplugin" % "1.1.6")
