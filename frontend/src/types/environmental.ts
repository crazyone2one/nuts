export interface EnvConfigItem {
    [key: string]: any;
}

export interface DataSourceItem {
    id: string;
    dataSource: string; // 数据源名称
    driverId: string; // 驱动id
    dbUrl: string; // 数据库连接url
    username: string; // 用户名
    password: string; // 密码
    poolMax?: number; // 最大连接数
    timeout?: number; // 超时时间
}

export interface CommonParams {
    requestTimeout: number;
    responseTimeout: number;

    [key: string]: any;
}

export interface AssertionConfig {
    assertions: EnvConfigItem[];
}
export interface FtpItem {
    id?: string;
    ip: string; // IP地址
    port: string; // 端口
    username: string; // 用户名
    password: string; // 密码
    localPath: string; // 本地路径
    remotePath: string; // 远程路径
    description?: string; // 描述
}
export interface EnvConfig {
    id?: string;
    commonParams?: CommonParams;
    commonVariables: EnvConfigItem[];
    httpConfig: EnvConfigItem[];
    dataSources: DataSourceItem[];
    hostConfig: EnvConfigItem;
    ftpConfig: FtpItem;
    name?: string;
    assertionConfig: AssertionConfig;
    pluginConfigMap: EnvConfigItem;
}

export interface EnvDetailItem {
    id?: string;
    projectId: string;
    name: string;
    config: EnvConfig;
    mock?: boolean;
    description?: string;
}

export interface GlobalParamsItem {
    headers: EnvConfigItem[];
    commonVariables: EnvConfigItem[];
}

export interface GlobalParams {
    id?: string;
    projectId: string;
    globalParams: GlobalParamsItem;
}
export interface EnvListItem {
    mock?: boolean;
    name: string;
    id: string;
    description: string;
}