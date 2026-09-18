package cn.master.nuts.dto.environment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.Strings;

import java.io.Serial;
import java.io.Serializable;
import java.util.function.BiFunction;

/**
 * @author : 11's papa
 * @since : 2026/9/15, 星期二
 **/
@Data
public class HttpConfigPathMatchRule implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 匹配规则 CONTAINS/EQUALS
     * {@link MatchRuleCondition}
     */
    @Schema(description = "匹配条件 CONTAINS/EQUALS")
    private String condition;
    @Schema(description = "路径")
    private String path;

    public enum MatchRuleCondition {
        /**
         * 包含
         */
        CONTAINS((envPath, path) -> Strings.CS.contains(path, envPath)),
        /**
         * 等于
         */
        EQUALS((envPath, path) -> Strings.CS.equals(path, envPath));

        MatchRuleCondition(BiFunction<String, String, Boolean> matchFunc) {
            this.matchFunc = matchFunc;
        }

        private final BiFunction<String, String, Boolean> matchFunc;

        public boolean match(String value, String expect) {
            return matchFunc.apply(value, expect);
        }
    }
}
