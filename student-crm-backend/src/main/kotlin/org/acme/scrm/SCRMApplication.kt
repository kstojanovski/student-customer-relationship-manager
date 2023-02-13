package org.acme.scrm

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext

@SpringBootApplication
class SCRMApplication : CommandLineRunner {

    var logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private val appContext: ApplicationContext? = null
    override fun run(args: Array<String>) {
        if (logger.isInfoEnabled) {
            listLoadedClasses()
        }
    }

    private fun listLoadedClasses() {
        val beans = appContext!!.beanDefinitionNames
        beans.sort()
        logger.info("Show Loaded Classes Begin")
        for (bean in beans) {
            logger.info(bean)
        }
        logger.info("Show Loaded Classes End")
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(org.acme.scrm.SCRMApplication::class.java, *args)
}
