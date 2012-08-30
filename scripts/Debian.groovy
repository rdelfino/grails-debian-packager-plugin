@Grab('org.vafer:jdeb:0.11')
import org.vafer.jdeb.*
import org.vafer.jdeb.ant.*

import groovy.text.SimpleTemplateEngine

includeTargets << grailsScript("_GrailsWar")

def generate(destination, templatePath, installHome, user, group, appName, version) {

    def binding = [
        "installHome": installHome,
        "user": user,
        "group": group,
        "appName": appName,
        "appVersion": version
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


target(deb:"Generate debian package") {
    
    depends(war)
    
    def targetDir = grailsSettings.projectWarFile.parent
    
    def version = metadata.getApplicationVersion()
    
    def controlDir = buildConfig.debian.control ?: "deb/control"
    def dataDir = buildConfig.debian.data ?: "deb/data"
    
    def targetControlDirPath = "${targetDir}/deb/control"
    
    def packageName = buildConfig.debian.package ?: "${grailsAppName}_${version}"
    def installHome = buildConfig.debian.install.home ?: "/opt"
    def warDestination = buildConfig.debian.install.war.directory ?: "${installHome}/${grailsAppName}-${version}/webapps"
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
    		src:"${dataDir}", 
    		type:"directory"
    	){
    		mapper(
    			type:"perm",
    			prefix:"${installHome}/${grailsAppName}-${version}"
    		)
    	}
    	
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
    }
    
    event("StatusFinal", ["Done creating Debian package ${packageName}.deb"])
}

setDefaultTarget("deb")

