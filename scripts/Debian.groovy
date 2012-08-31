import org.vafer.jdeb.*
import org.vafer.jdeb.ant.*

import groovy.text.SimpleTemplateEngine

includeTargets << grailsScript("_GrailsWar")

target(deb:"Generate debian package") {

	depends(war)

	def targetDir = grailsSettings.projectWarFile.parent

	def version = metadata.getApplicationVersion()

	def controlDir = buildConfig.debian.control ?: "debian"
	def dataElements = buildConfig.debian.data 

	def targetControlDirPath = "${targetDir}/deb/control"

	def installHome = buildConfig.debian.install.home ?: "/opt"
	def packageName = buildConfig.debian.name ?: "${grailsAppName}_${version}"
	def warDestination = buildConfig.debian.install.target.directory ?: "${installHome}/${grailsAppName}-${version}/webapps"
	def warDestinationName = buildConfig.debian.install.war.name ?: grailsSettings.projectWarFile.name
	def user = buildConfig.debian.install.user ?: grailsAppName
	def group = buildConfig.debian.install.user ?: grailsAppName

	def targetControlDir = new File(targetControlDirPath)

	if (targetControlDir.exists()){
		if (targetControlDir.isDirectory()) {
			targetControlDir.deleteDir()
		}
		else{
			targetControlDir.delete()
		}
	}

	targetControlDir.mkdirs()

	generate(
		"${targetControlDirPath}/preinst",
		"${controlDir}/preinst",
		installHome, user, group, grailsAppName, version
	)

	generate(
		"${targetControlDirPath}/postinst",
		"${controlDir}/postinst",
		installHome, user, group, grailsAppName, version
	)

	generate(
		"${targetControlDirPath}/prerm",
		"${controlDir}/prerm",
		installHome, user, group, grailsAppName, version
	)

	generate(
		"${targetControlDirPath}/postrm",
		"${controlDir}/postrm",
		installHome, user, group, grailsAppName, version
	)

	generate(
		"${targetControlDirPath}/control",
		"${controlDir}/control",
		installHome, user, group, grailsAppName, version
	)

	generate(
		"${targetControlDirPath}/conffiles",
		"${controlDir}/conffiles",
		installHome, user, group, grailsAppName, version
	)

	ant.taskdef ( name : 'deb' , classname : 'org.vafer.jdeb.ant.DebAntTask')

	ant.deb(
		control:"${targetControlDirPath}",
		destfile:"${targetDir}/${packageName}.deb",
		verbose:true
	){
	
		data(
			src:"${grailsSettings.projectWarFile.canonicalPath}",
			dst: "${warDestinationName}",
			type:"file"
		){
			mapper(
				type:"perm",
				prefix:"${warDestination}"
			)
		}
		
		dataElements.each{dataElement->
			
			def src = dataElement.src
			
			File srcFile = new File(src)
			
			def target = dataElement.target ?: "${installHome}/${grailsAppName}-${version}"
			def type = dataElement.type ?: (srcFile.isDirectory() ? "directory" : "file")
			
			def destinationName = srcFile.isDirectory() ? 
				srcFile.name :
				dataElement.name ?: srcFile.name 
			
			if ("file" == type) {
				data(
					src:"${src}",
					dst: "${destinationName}",
					type:"file"
				){
					mapper(
						type:"perm",
						prefix:"${target}"
					)
				}

			}
			else {
				data(
					src:"${src}",
					type:"${type}"
				){
					mapper(
						type:"perm",
						prefix:"${target}"
					)
				}
			}
			
		}
	}

	event("StatusFinal", ["Done creating Debian package ${packageName}.deb"])
}

void generate(destination, templatePath, installHome, user, group, appName, version) {

	 def binding = [
		  installHome: installHome,
		  user: user,
		  group: group,
		  appName: appName,
		  appVersion: version
	 ]

	 def template = new File(templatePath).text

	 def engine = new SimpleTemplateEngine()

	 def out = new FileOutputStream(destination, false)

	 def contents = ""
	 try{
		  contents = engine.createTemplate(template).make(binding).toString()
	 }
	 catch (Exception e){
		  e.printStackTrace()
		  contents = ""
	 }

	 out << contents
	 out.flush()
	 out.close()
}

setDefaultTarget("deb")
