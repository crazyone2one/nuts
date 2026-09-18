package cn.master.nuts.constants;

/**
 * @author : 11's papa
 * @since : 2026/9/11, 星期五
 **/
public enum InternalUserRole {
    ADMIN("admin"),
    MEMBER("member"),
    ORG_ADMIN("org_admin"),
    ORG_MEMBER("org_member"),
    PROJECT_ADMIN("project_admin"),
    PROJECT_MEMBER("project_member");

    private final String value;

    InternalUserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
