package net.jworkflow.sample01

import net.jworkflow.kernel.interfaces.WorkflowHost
import net.jworkflow.kernel.services.WorkflowModule
import java.util.Scanner
import java.util.logging.Level
import java.util.logging.Logger

object Main {
    @Throws(Exception::class)
    fun main(args: Array<String>?) {
        val rootLogger = Logger.getLogger("")
        rootLogger!!.setLevel(Level.SEVERE)
        WorkflowModule.setup()
        val host = WorkflowModule.getHost()
        host!!.registerWorkflow(HelloWorkflow::class.java)
        host!!.start()
        val id = host!!.startWorkflow("hello", 1, null)
        System.out.println("started workflow " + id!!)
        val scanner = Scanner(System.`in`)
        scanner!!.nextLine()
        System.out.println("shutting down...")
        host!!.stop()
    }
}