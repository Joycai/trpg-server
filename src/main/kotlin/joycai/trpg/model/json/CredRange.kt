package joycai.trpg.model.json

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("min", "max")
data class CredRange(
        @JsonProperty("min")
        var min: Int = 0,
        @JsonProperty("max")
        var max: Int = 90
)