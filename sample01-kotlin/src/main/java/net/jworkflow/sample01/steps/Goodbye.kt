package net.jworkflow.sample01.steps
import net.jworkflow.kernel.interfaces.StepBody
import net.jworkflow.kernel.models.*

class Goodbye:StepBody {
    @Override
    fun run(context:StepExecutionContext?):ExecutionResult? {
        System.out.println("Goodbye world")
        return ExecutionResult.next()
    }
}