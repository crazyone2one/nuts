// api/constants.ts

/** 后端统一响应码 */
export const RESPONSE_CODE = {
    /** 成功 */
    OK: 100200,
    /** token 过期 / 未授权 */
    TOKEN_EXPIRED: 100401,
} as const;

/**
 * 业务错误：后端返回 code !== OK 时抛出。
 * 用于让调用方区分「业务失败」与「网络/非预期错误」，避免 onError 重复提示。
 */
export class BusinessError extends Error {
    readonly code?: number;

    constructor(message: string, code?: number) {
        super(message);
        this.name = 'BusinessError';
        this.code = code;
    }
}

export const OrgOption = [{value: '100001', label: '默认Org'}];