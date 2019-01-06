package joycai.trpg.model.json

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("num", "skills")
data class CustomJobGroup(

        @JsonProperty("num")
        var num: Int = 0,
        @JsonProperty("skills")
        var skills: List<String>? = null

)