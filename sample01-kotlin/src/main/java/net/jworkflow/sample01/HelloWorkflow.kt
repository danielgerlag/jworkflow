package net.jworkflow.sample01
import net.jworkflow.sample01.steps.Goodbye
import net.jworkflow.sample01.steps.Hello
import net.jworkflow.kernel.interfaces.*

class HelloWorkflow:Workflow {
    val id:String?
        @Override
        get() = "hello"

    val version:Int
        @Override
        get() = 1

    val dataType:Class?
        @Override
        get() = Object::class.java

    @Override
    fun build(builder:WorkflowBuilder?) {
        builder!!
                .startsWith(Hello::class.java)
                .then(Goodbye::class.java)
    }
}