package joycai.trpg.model.json

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("name", "code", "mainAttr", "credMin", "credMax", "jobSkill", "exJobNum", "customJobGroup", "jobSkillMsg", "info")
data class JobDto(

        @JsonProperty("name")
        var name: String? = null,
        @JsonProperty("code")
        var code: String? = null,
        @JsonProperty("mainAttr")
        var mainAttr: List<Int>? = null,
        @JsonProperty("credMin")
        var credMin: Int = 0,

        @JsonProperty("credMax")
        var credMax: Int = 99,

        @JsonProperty("jobSkill")
        var jobSkill: List<String>? = null,
        @JsonProperty("exJobNum")
        var exJobNum: Int = 0,
        @JsonProperty("customJobGroup")
        var customJobGroup: List<CustomJobGroup>? = null,
        @JsonProperty("jobSkillMsg")
        var jobSkillMsg: String? = null,
        @JsonProperty("info")
        var info: String? = null
)