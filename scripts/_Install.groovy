ant.mkdir(dir:"${basedir}/debian")

ant.copy(
	file: "${pluginBasedir}/src/debian/conffiles",
	todir: "${basedir}/debian"
)

ant.copy(
	file: "${pluginBasedir}/src/debian/control",
	todir: "${basedir}/debian"
)

ant.copy(
	file: "${pluginBasedir}/src/debian/preinst",
	todir: "${basedir}/debian"
)

ant.copy(
	file: "${pluginBasedir}/src/debian/prerm",
	todir: "${basedir}/debian"
)

ant.copy(
	file: "${pluginBasedir}/src/debian/postinst",
	todir: "${basedir}/debian"
)

ant.copy(
	file: "${pluginBasedir}/src/debian/postrm",
	todir: "${basedir}/debian"
)
