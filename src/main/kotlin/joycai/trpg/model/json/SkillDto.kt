package joycai.trpg.model.json

data class SkillDto(
        var name: String? = null,
        var code: String? = null,
        var base: Int = 0,
        var jobPerk: Int = 0,
        var hobPerk: Int = 0,
        var isJobPerk: Boolean = false,
        var enableJobPerk: Boolean = false,
        var enableHobPerk: Boolean = true
)