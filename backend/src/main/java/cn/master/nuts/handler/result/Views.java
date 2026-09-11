package cn.master.nuts.handler.result;

/**
 * @author : 11's papa
 * @since : 2026/9/7, 星期一
 **/
public class Views {
    // 公开视图：仅包含基础信息
    public interface Base {}
    public interface Internal extends Base {}
    // 内部视图：包含敏感或详细信息
    public interface Public extends Base {}
}
