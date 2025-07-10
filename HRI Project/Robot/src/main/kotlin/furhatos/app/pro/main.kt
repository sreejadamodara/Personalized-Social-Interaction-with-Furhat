package furhatos.app.pro

import furhatos.app.pro.flow.Init
import furhatos.flow.kotlin.Flow
import furhatos.skills.Skill

class ProSkill : Skill() {
    override fun start() {
        Flow().run(Init)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}