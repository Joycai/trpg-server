package joycai.trpg.model.json

data class SkillDto(
        var name: String? = null,
        var code: String? = null,
        var base: Int = 0,
        var value: Int = 0,
        var value2: Int = 0,
        var isJobPerk: Boolean = false,
        var enableJobPerk: Boolean = false,
        var enableHobPerk: Boolean = true
)