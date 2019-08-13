package com.oos.oos

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean;

@SpringBootApplication
class OosApplication{
	fun controller() = HomeController()
}

fun main(args: Array<String>) {
	runApplication<OosApplication>(*args)
}
