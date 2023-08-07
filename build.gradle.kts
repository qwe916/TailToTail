import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*


plugins {
	id("org.springframework.boot") version "3.1.2"
	id("io.spring.dependency-management") version "1.1.2"
	kotlin("jvm") version "1.8.22"
	kotlin("plugin.spring") version "1.8.22"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	//implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val frontendDir = "$projectDir/frontend"

sourceSets {
	named("main") {
		resources.srcDirs("$projectDir/src/main/resources")
	}
}

tasks.named("processResources") {
	dependsOn("copyReactBuildFiles")
}

val installReact by tasks.registering(Exec::class) {
	workingDir(frontendDir)
	inputs.dir(frontendDir)
	group = BasePlugin.BUILD_GROUP

	if (System.getProperty("os.name").lowercase(Locale.getDefault()).contains("windows")) {
		commandLine("npm.cmd", "audit", "fix")
		commandLine("npm.cmd", "install")
	} else {
		commandLine("npm", "audit", "fix")
		commandLine("npm", "install")
	}
}

val buildReact by tasks.registering(Exec::class) {
	dependsOn(installReact)
	workingDir(frontendDir)
	inputs.dir(frontendDir)
	group = BasePlugin.BUILD_GROUP

	if (System.getProperty("os.name").lowercase(Locale.getDefault()).contains("windows")) {
		commandLine("npm.cmd", "run-script", "build")
	} else {
		commandLine("npm", "run-script", "build")
	}
}

val copyReactBuildFiles by tasks.registering(Copy::class) {
	dependsOn(buildReact)
	from("$frontendDir/build")
	into("$buildDir/resources/main/static")
}

tasks.named("bootJar") {
	dependsOn("copyReactBuildFiles")
}

tasks.processResources{
	duplicatesStrategy = DuplicatesStrategy.INCLUDE
}